package com.xykj.bean;

/**
 * 会话类
 */
public class Conversation {
    //id,unread,time,last_msg,u_id,is_group
    private int id;
    private int unread;
    private String time; //123456454->2018/08/6 10:00:00
    private String lastMsg;
    private Chatter chatter;
    private boolean isGroup;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUnread() {
        return unread;
    }

    public void setUnread(int unread) {
        this.unread = unread;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Chatter getChatter() {
        return chatter;
    }

    public void setChatter(Chatter chatter) {
        this.chatter = chatter;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public Conversation(int id, int unread, String time, String lastMsg, Chatter chatter, boolean isGroup) {
        this.id = id;
        this.unread = unread;
        this.time = time;
        this.lastMsg = lastMsg;
        this.chatter = chatter;
        this.isGroup = isGroup;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }
}
