package com.xykj.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mct.model.CMessage;
import com.xykj.bean.Chatter;
import com.xykj.bean.Conversation;
import com.xykj.bean.VWillMessage;
import com.xykj.vwill.VWillApp;
import com.xyy.utils.TimeUtil;

import java.util.LinkedList;
import java.util.List;

public class DbUtil {
    private static final String DB_NAME = "vwill.db";
    private static final int VERSION = 1;

    private static DbUtil instance;
    private MyOpenHelper mHelper;
    private SQLiteDatabase mDb;

    private DbUtil(Context context) {
        mHelper = new MyOpenHelper(context.getApplicationContext());
        mDb = mHelper.getWritableDatabase();
    }

    public static DbUtil getInstance(Context context) {
        if (instance == null) {
            instance = new DbUtil(context);
        }
        return instance;
    }


    class MyOpenHelper extends SQLiteOpenHelper {

        public MyOpenHelper(Context context) {
            super(context, DB_NAME, null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //用户记录(id,u_id,nick,photo)
            String address = "create table address(id integer primary key autoincrement,u_id integer not null,nick text,photo text)";
            db.execSQL(address);
            //会话(id,login_id,unread,time,last_msg,u_id,is_group)
            String conversation = "create table threads (id integer primary key autoincrement,login_id integer not null,unread integer not null,time text not null,last_msg text not null,u_id integer not null,is_group integer not null)";
            db.execSQL(conversation);
            //消息(id,threads_id,u_id,time,type,body,content_type)
            String msg = "create table msg(id integer primary key autoincrement,threads_id integer not null,u_id integer not null,time text,type integer ,body text,content_type integer)";
            db.execSQL(msg);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }

    /**
     * 会话列表的加载
     * @param loginId
     * @return
     */
    public List<Conversation> loadConversations(int loginId){
        String[] columns = {"id","login_id","unread","time","last_msg","u_id","is_group"};
        Cursor c = mDb.query("threads",columns,"login_id=?",new String[]{String.valueOf(loginId)},null,null,"time desc");
        List<Conversation> list = null;
        if(c.getCount()>0){
            list = new LinkedList<>();
            while(c.moveToNext()){
                int id = c.getInt(0);
                int unread = c.getInt(2);
                String timeStr = c.getString(3);
                String time = TimeUtil.getInstance().formatTime(Long.parseLong(timeStr));
                String lastMsg = c.getString(4);
                int uId = c.getInt(5);
                Chatter chatter = getChatter(uId);
                boolean isGroup = c.getInt(6)==1;
                list.add(new Conversation(id,unread,time,lastMsg,chatter,isGroup));
            }
        }
        c.close();
        return list;
    }

    /**
     * 加载用户在某个会话下的聊天记录
     * @param threadsId
     * @param app
     * @return
     */
    public List<VWillMessage> getMsgs(int threadsId, VWillApp app){
        List<VWillMessage> list = null;
        String[] columns={"id","threads_id","u_id","time","type","body","content_type"};
        Cursor c = mDb.query("msg",columns,"threads_id=?",new String[]{String.valueOf(threadsId)},null,null,null);
        if(c.getCount()>0){
            list = new LinkedList<>();
            while(c.moveToNext()){
                int id = c.getInt(0);
                int userId = c.getInt(2);
//                String time = c.getString(3);
                int type = c.getInt(4);
                String body = c.getString(5);
                int contentType = c.getInt(6);
                String nick,photo;
                if(type == VWillMessage.TYPE_SEND){
                    //昵称和头像都是登陆者的
                    nick = app.getLoginUser().getNick();
                    photo = app.getLoginUser().getPhoto();
                }else{
                    Chatter chatter = getChatter(userId);
                    nick = chatter.getNick();
                    photo = chatter.getPhoto();
                }
                //int id, int senderId, String nick, String photo, String msg, int type, int contentType
                list.add(new VWillMessage(id,userId,nick,photo,body,type,contentType));
            }
        }
        c.close();
        return list;
    }


    /**
     * 添加消息内容
     *
     * @param threadsId
     * @param userId
     * @param type
     * @param content
     * @param contentType
     */
    public void addMsg(int threadsId, int userId, int type, String content, int contentType) {
        ContentValues values = new ContentValues();
        values.put("threads_id", threadsId);
        values.put("u_id", userId);
        values.put("time", String.valueOf(System.currentTimeMillis()));
        values.put("type", type);
        values.put("body", content);
        values.put("content_type", contentType);
        mDb.insert("msg", null, values);
    }

    /**
     * 会话更新
     *
     * @param threadsId
     * @param unread
     * @param content
     * @param contentType
     */
    public void updateThreads(int threadsId, int unread, String content, int contentType) {
        StringBuilder sb = new StringBuilder("update threads set ");
        sb.append("time=").append("'").append(System.currentTimeMillis()).append("',");
        String lastMsg;
        if (contentType == CMessage.TYPE_TEXT) {
            if (content.length() > 20) {
                lastMsg = content.substring(0, 20);
            } else {
                lastMsg = content;
            }
        } else {
            lastMsg = getMsgTips(contentType);
        }
        sb.append("last_msg=").append("'").append(lastMsg).append("',");
        if (unread == 0) {
            sb.append("unread=0");
        } else {
            sb.append("unread=unread+1");
        }
        sb.append(" where id=").append(threadsId);
        String sql = sb.toString();
        mDb.execSQL(sql);
    }

    /**
     * 会话的添加
     *
     * @param loginId
     * @param unread
     * @param content
     * @param contentType
     * @param userId
     * @param isGroup
     * @return
     */
    public int addThreads(int loginId, int unread, String content, int contentType, int userId, int isGroup) {
        ContentValues values = new ContentValues();
        values.put("login_id", loginId);
        values.put("unread", unread);
        values.put("time", String.valueOf(System.currentTimeMillis()));
        if (contentType == CMessage.TYPE_TEXT) {
            values.put("last_msg", content);
        } else {
            values.put("last_msg", getMsgTips(contentType));
        }
        values.put("u_id", userId);
        values.put("is_group", isGroup);
        long rawId = mDb.insert("threads", null, values);
        if (rawId > 0) {
            return (int) rawId;
        }
        return 0;
    }

    public void update2Readed(int threadsId){
        ContentValues values = new ContentValues();
        values.put("unread", 0);
        mDb.update("threads",values,"id=?",new String[]{String.valueOf(threadsId)});
    }

    public void saveMsg(int loginId,int unread,VWillMessage vWillMessage,int chatterUserId,int conversationUserId,int isGroup){
        //确认会话是否已经存在，存在则更新会话，不存在则创建会话
        int threadsId = getThreadsId(loginId,conversationUserId);
        if(threadsId!=0){
            //更新会话
            updateThreads(threadsId,unread,vWillMessage.getMsg(),vWillMessage.getContentType());
        }else{
            //添加会话
            threadsId = addThreads(loginId,unread,vWillMessage.getMsg(),vWillMessage.getContentType(),conversationUserId,isGroup);
        }
        //添加消息
        addMsg(threadsId,chatterUserId,vWillMessage.getType(),vWillMessage.getMsg(),vWillMessage.getContentType());
    }

    private String getMsgTips(int contentType) {
        switch (contentType) {
            case CMessage.TYPE_VOICE:
                return "[音乐]";
            case CMessage.TYPE_VIDEO:
                return "[视频]";
            case CMessage.TYPE_PICTURE:
                return "[图片]";
            case CMessage.TYPE_LOCATION:
                return "[位置]";
            case CMessage.TYPE_NOMAL_FILE:
                return "[文件]";
            case CMessage.TYPE_OTHER:
                return "[其他]";
        }
        return "[订单]";
    }

    /**
     * 根据登陆者id以及对方的id查找看是否有会话记录
     *
     * @param loginId
     * @param otherId
     * @return
     */
    public int getThreadsId(int loginId, int otherId) {
        String[] columns = {"id"};
        Cursor c = mDb.query("threads", columns, "login_id=? and u_id=?", new String[]{String.valueOf(loginId), String.valueOf(otherId)}, null, null, null);
        int id = 0;
        if (c.moveToFirst()) {
            id = c.getInt(0);
        }
        c.close();
        return id;
    }

    /**
     * 获取聊天者在本地的记录
     *
     * @param userId
     * @return
     */
    public Chatter getChatter(int userId) {
        String[] column = {"u_id", "nick", "photo"};
        Cursor c = mDb.query("address", column, "u_id=?", new String[]{String.valueOf(userId)}, null, null, null);
        Chatter chatter = null;
        if (c.moveToFirst()) {
            String nick = c.getString(1);
            String photo = c.getString(2);
            chatter = new Chatter(userId, nick, photo);
        }
        c.close();
        return chatter;
    }

    /**
     * 记录聊天者信息
     *
     * @param userId
     * @param nick
     * @param photo
     * @return
     */
    public Chatter saveChatter(int userId, String nick, String photo) {
        ContentValues values = new ContentValues();
        values.put("u_id", userId);
        values.put("nick", nick);
        values.put("photo", photo);
        long rowId = mDb.insert("address", null, values);
        if (rowId > 0) {
            Chatter chatter = new Chatter(userId, nick, photo);
            return chatter;
        }
        return null;
    }
}
