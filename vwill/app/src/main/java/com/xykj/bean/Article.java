package com.xykj.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.xykj.utils.Common;
import com.xyy.utils.TimeUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Article implements Parcelable{

    /**
     * authorId : 10000003
     * content : 本地拍照的照片直接显示时，强制退出怎么办
     * distance : -1
     * id : 1
     * isHide : 0
     * lat : 39.913249
     * lng : 116.403625
     * locationName : 北京天安门广场
     * noSupport : 1
     * replyCount : 1
     * showName : aaa
     * support : 2
     * tagId : 3
     * tagName : IT工作者
     * time : 1417664629298
     * title : android图像显示
     * type : 1
     * userPhoto : /upload/1476182190788.jpg
     */

    private int authorId;
    private String content;
    private int distance;
    private int id;
    private int isHide;
    private String lat;
    private String lng;
    private String locationName;
    private int noSupport;
    private int replyCount;
    private String showName;
    private int support;
    private int tagId;
    private String tagName;
    private String time;
    private String title;
    private int type;
    private String userPhoto;
    //记录当前用户对文章的赞或者踩的状态
    private int supportState;

    private String textContent;  //文本内容
    private String[] mediaUrls; //媒体文件地址
    private String[] thumbs; //视频文件的缩略图

    //正在播放的地址
    private String playUrl;
    //正在播放的位置
    private int playPos;
    public Article() {
    }

    protected Article(Parcel in) {
        authorId = in.readInt();
        distance = in.readInt();
        id = in.readInt();
        isHide = in.readInt();
        lat = in.readString();
        lng = in.readString();
        locationName = in.readString();
        noSupport = in.readInt();
        replyCount = in.readInt();
        showName = in.readString();
        support = in.readInt();
        tagId = in.readInt();
        tagName = in.readString();
        time = in.readString();
        title = in.readString();
        type = in.readInt();
        userPhoto = in.readString();
        supportState = in.readInt();
        textContent = in.readString();
        mediaUrls = in.createStringArray();
        thumbs = in.createStringArray();
        playUrl = in.readString();
        playPos = in.readInt();
    }

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        try {
            JSONObject obj = new JSONObject(content);
            textContent = obj.optString("text");
            JSONArray ary = obj.optJSONArray("files");
            int len;
            if (ary != null && (len = ary.length()) > 0) {
                mediaUrls = new String[len];
                //检测是否有缩略图
                JSONArray thumbAry = null;
                if (obj.has("thumb")) {
                    thumbAry = obj.optJSONArray("thumb");
                    thumbs = new String[len];
                }
                for (int i = 0; i < len; i++) {
                    mediaUrls[i] = Common.SERVER_URL + ary.optString(i);
                    if (null != thumbAry) {
                        thumbs[i] = Common.SERVER_URL + thumbAry.optString(i);
                    }
                }
            }
        } catch (JSONException e) {
            textContent = content;
        }
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIsHide() {
        return isHide;
    }

    public void setIsHide(int isHide) {
        this.isHide = isHide;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public int getNoSupport() {
        return noSupport;
    }

    public void setNoSupport(int noSupport) {
        this.noSupport = noSupport;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public int getSupport() {
        return support;
    }

    public void setSupport(int support) {
        this.support = support;
    }

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        long t = Long.parseLong(time);
        this.time = TimeUtil.getInstance().formatTime(t);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getTextContent() {
        return textContent;
    }

    public String[] getMediaUrls() {
        return mediaUrls;
    }

    public String[] getThumbs() {
        return thumbs;
    }

    public int getSupportState() {
        return supportState;
    }

    public void setSupportState(int supportState) {
        this.supportState = supportState;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(authorId);
        parcel.writeInt(distance);
        parcel.writeInt(id);
        parcel.writeInt(isHide);
        parcel.writeString(lat);
        parcel.writeString(lng);
        parcel.writeString(locationName);
        parcel.writeInt(noSupport);
        parcel.writeInt(replyCount);
        parcel.writeString(showName);
        parcel.writeInt(support);
        parcel.writeInt(tagId);
        parcel.writeString(tagName);
        parcel.writeString(time);
        parcel.writeString(title);
        parcel.writeInt(type);
        parcel.writeString(userPhoto);
        parcel.writeInt(supportState);
        parcel.writeString(textContent);
        parcel.writeStringArray(mediaUrls);
        parcel.writeStringArray(thumbs);
        parcel.writeString(playUrl);
        parcel.writeInt(playPos);
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public Article setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
        return this;
    }

    public int getPlayPos() {
        return playPos;
    }

    public Article setPlayPos(int playPos) {
        this.playPos = playPos;
        return this;
    }
}
