package com.xykj.vwill;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.alibaba.fastjson.JSON;
import com.mct.client.ClientManager;
import com.mct.client.OnClientListener;
import com.mct.model.CMessage;
import com.xykj.bean.Chatter;
import com.xykj.bean.RoomState;
import com.xykj.bean.User;
import com.xykj.bean.VWillMessage;
import com.xykj.utils.Common;
import com.xykj.utils.DbUtil;
import com.xyy.net.NetManager;
import com.xyy.net.RequestItem;
import com.xyy.net.ResponceItem;
import com.xyy.net.imp.Callback;
import com.xyy.utils.TipsUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

public class VWillService extends Service {
    private VWillApp app;
    ClientManager clientManager;
    DbUtil dbUtil;
    //记录当前界面上正在聊天的对方账号
    private int friendId;

    public VWillService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null != intent) {
            if (intent.hasExtra("friendId")) {
                friendId = intent.getIntExtra("friendId", 0);
            }
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = (VWillApp) getApplication();
        //将登陆者的id登录到ApacheMina服务器上才可以接收(/发送)即时消息
        clientManager = ClientManager.getInstance();
        //添加消息监听
        clientManager.setOnClientListener(new MessageListener());
        //连接服务器
        new Thread() {
            @Override
            public void run() {
                clientManager.connect(Common.IP, Common.PORT);
            }
        }.start();
        dbUtil = DbUtil.getInstance(this);
    }

    class MessageListener implements OnClientListener {

        @Override
        public void onConnectOk() {
            TipsUtil.log("连接服务器成功");
            //登录ApacheMina服务器
            clientManager.login(app.getLoginUser().getId());
            //加载离线消息

        }

        @Override
        public void onConnectFail() {
            TipsUtil.log("连接服务器失败");
        }

        @Override
        public void onReceivMessage(CMessage cMessage) {
            String content = cMessage.getMsgString();
            TipsUtil.log("收到消息:" + content);
            //消息处理
            int type = cMessage.getType();
            if (type >= CMessage.TYPE_ORDER_MIN && type <= CMessage.TYPE_ORDER_STATE) {
                //订单类型

            } else {
                //普通聊天信息
                int receiver = cMessage.getReceiver();
                int sender = cMessage.getSender();
                //检测当前用户是否在聊天界面，在聊天界面直接发送内容去显示，如果不在则通知提醒
                if (receiver == app.getLoginUser().getId()) {
                    if (sender == 0) {
                        if (type == CMessage.TYPE_LOCATION) {
                            //使用接人功能，收到了好友的位置
                            //基于自己的位置和好友的位置建立路径规划

                        }
                    } else {
                        //单聊
                        //发消息的人在本地是否已经存在，没有则加载用户基本信息保存到数据库中，组装界面显示需要的消息类型VWillMessage
                        optMsg(sender, content, type, null);
                    }
                } else {
                    //群聊
                    if (receiver < 0) {
                        //直播中的消息
                        if (sender == 0) {
                            //房间状态
                            RoomState state = (RoomState) JSON.parseObject(content, RoomState.class);
                            EventBus.getDefault().post(state);
                        } else {
                            //房间中的聊天信息
                            //将消息发送到界面上去显示
                            optMsg(sender, content, type, null, true);
                        }
                    } else {
                        //发送消息的群（信息）是否已经存在，如果没存在，需要加载群消息(群号、昵称)
                        Chatter group = dbUtil.getChatter(receiver);
                        if (group == null) {
                            //加载群组信息
                            loadGroupInfo(receiver, sender, content, type);
                        } else {
                            //基于当前登录者和群组之间处理会话存储或者更新
                            //将发送者和当前登录者的具体消息存储到消息表中
                            optMsg(sender, content, type, group);
                        }
                    }

                }

            }
        }
    }

    private void optMsg(int sender, String content, int type, Chatter group) {
        optMsg(sender, content, type, group, false);
    }

    private void optMsg(int sender, String content, int type, Chatter group, boolean isLive) {
        Chatter chatter = dbUtil.getChatter(sender);
        if (chatter == null) {
            //加载用户信息，保存，
            loadUserInfo(sender, content, type, group, isLive);
        } else {
            showAndSaveMsg(content, type, chatter, group, isLive);
        }
    }

    private void showAndSaveMsg(String content, int contentType, Chatter chatter, Chatter group, boolean isLive) {
        // 拼接显示消息内容，处理显示(提醒)
        VWillMessage vWillMessage = new VWillMessage(chatter.getUserId(), chatter.getNick(), chatter.getPhoto(), content, VWillMessage.TYPE_RECEIV, contentType);
        // 如果是直播消息，则直接发送到界面显示即可
        if (isLive) {
            EventBus.getDefault().post(vWillMessage);
            return;
        }
        //检测当前用户是否在聊天界面上，在则显示，不在就通知
        int unread = 1;
        if (friendId == chatter.getUserId()) {
            //说明收到的消息刚好是用户所在的聊天界面上的人发来的
            unread = 0;
            EventBus.getDefault().post(vWillMessage);
        }
        //如果用户在聊天界面上再将unread设置为0

        //将消息记录到数据库中实现聊天记录(收到消息的记录)
        int tagetId; //会话的目标(如果是群消息，那么目标就是群号)
        int isGroup = 0;
        if (group == null) {
            tagetId = chatter.getUserId();
        } else {
            tagetId = group.getUserId();
            isGroup = 1;
        }
        dbUtil.saveMsg(app.getLoginUser().getId(), unread, vWillMessage, chatter.getUserId(), tagetId, isGroup);
    }

    private void loadUserInfo(int userId, final String content, final int type, final Chatter group, final boolean isLive) {
        NetManager.getInstance().execute(new RequestItem.Builder()
                .url(Common.URL_LOAD_USER_INFO + "?u_id=" + userId)
                .build(), new Callback<User>() {
            @Override
            public User changeData(ResponceItem responce) {
                String json = responce.getString();
                if (!json.startsWith("{result")) {
                    User u = (User) JSON.parseObject(json, User.class);
                    return u;
                }
                return null;
            }

            @Override
            public void onResult(User result) {
                //保存用户信息
                if (null != result) {
                    Chatter chatter = dbUtil.saveChatter(result.getId(), result.getNick(), result.getPhoto());
                    showAndSaveMsg(content, type, chatter, group, isLive);
                }
            }
        });
    }

    private void loadGroupInfo(final int groupId, final int sender, final String content, final int type) {
        NetManager.getInstance().execute(new RequestItem.Builder()
                        .url(Common.URL_LOAD_GROUP_INFO + "?group_id=" + groupId)
                        .build()
                , new Callback<Chatter>() {
                    @Override
                    public Chatter changeData(ResponceItem responce) {
                        String json = responce.getString();
                        if (!json.startsWith("{result")) {
                            try {
                                JSONObject obj = new JSONObject(json);
                                String name = obj.optString("groupName");
                                Chatter group = new Chatter(groupId, name, "");
                                return group;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        return null;
                    }

                    @Override
                    public void onResult(Chatter group) {
                        if (group != null) {
                            dbUtil.saveChatter(group.getUserId(), group.getNick(), "");
                            optMsg(sender, content, type, group);
                        }
                    }
                });
    }

    @Override
    public void onDestroy() {
        clientManager.release();
        clientManager = null;
        super.onDestroy();
    }
}
