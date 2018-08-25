package com.xykj.bean;

public class VWillMessage {
    //发出类型
    public static final int TYPE_SEND = 1;
    //收到类型
    public static final int TYPE_RECEIV = 2;
    //消息id
    private int id;
    //发送者是谁
    private int senderId;
    //发送者昵称
    private String nick;
    //发送者头像
    private String photo;
    //发送的消息
    private String msg;
    //消息类型（收发类型）
    private int type;
    //内容类型（文本，图像，视频，音乐，普通文件，位置）
    private int contentType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public VWillMessage(int id, int senderId, String nick, String photo, String msg, int type, int contentType) {
        this.id = id;
        this.senderId = senderId;
        this.nick = nick;
        this.photo = photo;
        this.msg = msg;
        this.type = type;
        this.contentType = contentType;
    }

    public VWillMessage(int senderId, String nick, String photo, String msg, int type, int contentType) {
       this(0,senderId,nick,photo,msg,type,contentType);
    }
}
