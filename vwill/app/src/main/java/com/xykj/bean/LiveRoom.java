package com.xykj.bean;

/**
 * 直播房间类
 */
public class LiveRoom {

    /**
     * anchorId : 10000001
     * anchorName : qqq
     * anchorPhoto : /upload/1477644365809.jpg
     * members : 1
     * photo : /live_photo/abc123.jpeg
     * playUrl : rtmp://zhibo2.xinyanyuan.com.cn/test_app/-10000001
     * pushUrl :
     * roomId : -10000001
     * state : 0
     * time : 1479282559172
     */

    //主播id
    private int anchorId;
    //主播名称
    private String anchorName;
    //主播头像
    private String anchorPhoto;
    //房间中的人数
    private int members;
    private String photo;
    //观看地址(观众用的)
    private String playUrl;
    //主播推流地址(主播用的)
    private String pushUrl;
    //房间号(就是主播id的负数)
    private int roomId;
    private int state;
    private String time;

    public int getAnchorId() {
        return anchorId;
    }

    public void setAnchorId(int anchorId) {
        this.anchorId = anchorId;
    }

    public String getAnchorName() {
        return anchorName;
    }

    public void setAnchorName(String anchorName) {
        this.anchorName = anchorName;
    }

    public String getAnchorPhoto() {
        return anchorPhoto;
    }

    public void setAnchorPhoto(String anchorPhoto) {
        this.anchorPhoto = anchorPhoto;
    }

    public int getMembers() {
        return members;
    }

    public void setMembers(int members) {
        this.members = members;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public String getPushUrl() {
        return pushUrl;
    }

    public void setPushUrl(String pushUrl) {
        this.pushUrl = pushUrl;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
