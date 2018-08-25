package com.xykj.vwill;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.mct.client.ClientManager;
import com.mct.model.CMessage;
import com.xykj.adapter.VWillMsgAdapter;
import com.xykj.bean.Chatter;
import com.xykj.bean.MediaItem;
import com.xykj.bean.User;
import com.xykj.bean.VWillMessage;
import com.xykj.utils.Common;
import com.xykj.utils.DbUtil;
import com.xyy.utils.TipsUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 聊天界面
 */
public class ChatActivity extends VWillBaseActivity {
    //获取媒体
    private static final int REQUEST_MEDIA = 1;
    //获取位置
    private static final int REQUEST_LOCATION = 2;

    @BindView(R.id.iv_input_type)
    ImageView ivInputType;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.tv_voice_touch)
    TextView tvVoiceTouch;
    @BindView(R.id.iv_add_media)
    ImageView ivAddMedia;
    @BindView(R.id.media_tool_layout)
    LinearLayout mediaToolLayout;
    @BindView(R.id.list_msg)
    ListView listMsg;

    //所聊天的对方信息
    private Chatter chatter;
    //会话id
    private int threadsId;
    private VWillMsgAdapter adapter;
    private VWillApp app;
    private int isGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        app = (VWillApp) getApplication();
        Intent it = getIntent();
        if (it.hasExtra("userId")) {
            //从个人中心过来，带着聊天用户信息
            int userId = it.getIntExtra("userId", 0);
            //检测本地是否有聊天者
            chatter = DbUtil.getInstance(this).getChatter(userId);
            if (chatter == null) {
                //本地没有聊天者信息，保存聊天者信息
                chatter = DbUtil.getInstance(this).saveChatter(userId, it.getStringExtra("nick"), it.getStringExtra("photo"));
            }
            threadsId = DbUtil.getInstance(this).getThreadsId(app.getLoginUser().getId(), chatter.getUserId());
        } else if (it.hasExtra("threads_id")) {
            threadsId = it.getIntExtra("threads_id", 0);
            chatter = it.getParcelableExtra("chatter");
        }
        isGroup = it.getIntExtra("is_group", 0);
        super.onCreate(savedInstanceState);
        //告诉服务当前所聊天的对方是谁
        Intent vwillServiceIntent = new Intent(this, VWillService.class);
        vwillServiceIntent.putExtra("friendId", chatter.getUserId());
        startService(vwillServiceIntent);
        //注册监听服务发来的消息
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initLayout() {
        super.initLayout();
        adapter = new VWillMsgAdapter(this);
        listMsg.setAdapter(adapter);
        //加载聊天记录
        if (threadsId != 0) {
            new LoadMsgTask().execute();
        }
        listMsg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                VWillMessage msg = adapter.getItem(position);
                switch (msg.getContentType()) {
                    case CMessage.TYPE_PICTURE:
                        String url;
                        ArrayList<String> paths = new ArrayList<>();
                        if (msg.getType() == VWillMessage.TYPE_RECEIV) {
                            //网址（相对地址）
                            url = Common.SERVER_URL + msg.getMsg();
                        } else {
                            //如果是发出的，则图像地址为地址
                            url = msg.getMsg();
                        }
                        paths.add(url);
                        Intent it = new Intent(ChatActivity.this, ImageShowActivity.class);
                        it.putStringArrayListExtra("data", paths);
                        startActivity(it);
                        break;
                    case CMessage.TYPE_LOCATION:
                        Intent locIntent = new Intent(ChatActivity.this, MapActivity.class);
                        //告诉它查看的位置是哪个
                        try {
                            JSONObject obj = new JSONObject(msg.getMsg());
                            String locName = obj.optString("extras");
                            double lat = obj.optDouble("lat");
                            double lon = obj.optDouble("lon");
                            LatLng ll = new LatLng(lat,lon);
                            locIntent.putExtra("address",locName);
                            locIntent.putExtra("location",ll);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        startActivity(locIntent);
                        break;
                }

            }
        });
    }


    class LoadMsgTask extends AsyncTask<Object, Void, List<VWillMessage>> {

        @Override
        protected List<VWillMessage> doInBackground(Object... objects) {
            List<VWillMessage> list = DbUtil.getInstance(ChatActivity.this).getMsgs(threadsId, app);
            return list;
        }

        @Override
        protected void onPostExecute(List<VWillMessage> vWillMessages) {
            adapter.setList(vWillMessages);
        }
    }

    @Override
    protected String getActivityTitle() {
        return chatter.getNick();
    }

    @Override
    protected int getType() {
        return TYPE_BACK;
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_chat;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(VWillMessage msg) {
        adapter.addMsg(msg);
        //让ListView滚动到新增的数据上
        listMsg.smoothScrollToPosition(adapter.getCount() - 1);
    }

    @OnClick({R.id.iv_input_type, R.id.iv_add_media, R.id.btn_send_msg, R.id.iv_face, R.id.iv_image, R.id.iv_video, R.id.iv_music, R.id.iv_loc, R.id.iv_file})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_input_type:
                //切换发送语音或者文本内容
                //如果当前“按下 说话”的视图没有显示（说明现在是文本内容发送），则切换时显示“按下 说话”视图，同时隐藏文本输入框
                if (tvVoiceTouch.getVisibility() != View.VISIBLE) {
                    tvVoiceTouch.setVisibility(View.VISIBLE);
                    etContent.setVisibility(View.GONE);
                    ivInputType.setImageResource(R.drawable.ic_input_word);
                } else {
                    tvVoiceTouch.setVisibility(View.GONE);
                    etContent.setVisibility(View.VISIBLE);
                    ivInputType.setImageResource(R.drawable.ic_input_voise);
                }
                break;
            case R.id.iv_add_media:
                //当前是否显示了媒体选择的布局
                if (mediaToolLayout.getVisibility() == View.VISIBLE) {
                    //显示了，隐藏
                    mediaToolLayout.setVisibility(View.GONE);
                    ObjectAnimator.ofFloat(ivAddMedia, View.ROTATION, 45, 0).setDuration(500).start();
                } else {
                    ObjectAnimator.ofFloat(ivAddMedia, View.ROTATION, 0, 45).setDuration(500).start();
                    mediaToolLayout.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.btn_send_msg:
                //发送文本
                String content = etContent.getText().toString();
                if (!TextUtils.isEmpty(content)) {
                    //发送文本
                    ClientManager.getInstance().sendTextMsg(app.getLoginUser().getId(), chatter.getUserId(), content);
                    //显示消息
                    showAndSaveMsg(content, CMessage.TYPE_TEXT);
                    etContent.setText("");
                } else {
                    TipsUtil.toast(this, "不能发送空内容");
                }
                break;
            case R.id.iv_face:
                break;
            case R.id.iv_image:
                toGetMedia(Common.TYPE_IMAGE);
                break;
            case R.id.iv_video:
                toGetMedia(Common.TYPE_VIDEO);
                break;
            case R.id.iv_music:
                toGetMedia(Common.TYPE_AUDIO);
                break;
            case R.id.iv_loc:
                Intent it = new Intent(this, MapActivity.class);
                startActivityForResult(it, REQUEST_LOCATION);
                break;
            case R.id.iv_file:
                break;
        }
    }

    private void showAndSaveMsg(String content, int contentType) {
        VWillMessage vWillMessage = new VWillMessage(app.getLoginUser().getId(), app.getLoginUser().getNick(), app.getLoginUser().getPhoto(), content, VWillMessage.TYPE_SEND, contentType);
        adapter.addMsg(vWillMessage);
        listMsg.smoothScrollToPosition(adapter.getCount() - 1);
        //保存聊天记录
        DbUtil.getInstance(this).saveMsg(app.getLoginUser().getId(), 0, vWillMessage, chatter.getUserId(), chatter.getUserId(), isGroup);
    }

    private void toGetMedia(int type) {
        Intent it = new Intent(this, GetMediaActivity.class);
        it.putExtra("type", type);
        it.putExtra("max", 1);
        startActivityForResult(it, REQUEST_MEDIA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_MEDIA:
                if (resultCode == RESULT_OK) {
                    //有数据
                    int returnType = data.getIntExtra("type", 0);
                    ArrayList<MediaItem> medias = data.getParcelableArrayListExtra("result");
                    //发送媒体信息到服务器
                    int contentType = change2CMessageType(returnType);
                    int size = medias.size();
                    for (int i = 0; i < size; i++) {
                        File f = new File(medias.get(i).getPath());
                        ClientManager.getInstance().sendFile(app.getLoginUser().getId(), chatter.getUserId(), contentType, f);
                        showAndSaveMsg(f.getAbsolutePath(), contentType);
                    }
                }
                break;
            case REQUEST_LOCATION:
                if (resultCode == RESULT_OK) {
                    String selectLocatioName = data.getStringExtra("address");
                    LatLng selectLocation = data.getParcelableExtra("location");
                    //发送位置
                    ClientManager.getInstance().sendLocation(app.getLoginUser().getId(), chatter.getUserId(), selectLocation.longitude, selectLocation.latitude, selectLocatioName);
                    StringBuilder sb = new StringBuilder();
                    sb.append("{lon:").append(selectLocation.longitude).append(",lat:").append(selectLocation.latitude).append(",extras:").append(selectLocatioName).append("}");
                    showAndSaveMsg(sb.toString(), CMessage.TYPE_LOCATION);
                }
                break;
        }
        mediaToolLayout.setVisibility(View.GONE);
        ObjectAnimator.ofFloat(ivAddMedia, View.ROTATION, 45, 0).setDuration(500).start();
    }

    private int change2CMessageType(int type) {
        switch (type) {
            case Common.TYPE_IMAGE:
                return CMessage.TYPE_PICTURE;
            case Common.TYPE_VIDEO:
                return CMessage.TYPE_VIDEO;
            case Common.TYPE_AUDIO:
                return CMessage.TYPE_VOICE;
        }
        return CMessage.TYPE_OTHER;
    }

    @Override
    protected void onDestroy() {
        //取消注册EventBus
        EventBus.getDefault().unregister(this);
        //告诉服务当前没有在聊天界面
        Intent vwillServiceIntent = new Intent(this, VWillService.class);
        vwillServiceIntent.putExtra("friendId", 0);
        startService(vwillServiceIntent);
        super.onDestroy();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (adapter.isWindowShow()) {
                adapter.hidePlayerInWindow();
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }
}
