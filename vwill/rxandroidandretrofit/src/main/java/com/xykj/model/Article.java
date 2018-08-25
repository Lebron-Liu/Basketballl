package com.xykj.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.xykj.utils.Common;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 文章类型
 * Created by admin on 2016/10/10.
 */
public class Article implements Parcelable {
    //文本内容
    private String content;
    //文章id
    private int id;
    //文章是否是匿名发布的（0不匿名 1匿名）
    private int isHide;
    //文章回复数量
    private int replyCount;
    //发布者名称
    private String showName;
    //发布时间
    private String time;
    //文章标题
    private String title;
    //用户头像
    private String userPhoto;

    private int support;  //赞的数量

    private int noSupport;//踩的数量
    //显示的内容
    //文本部分
    private String textContent;

    //记录是否已经点赞或者踩 非0则不能再点赞或者踩
    private int supportState;  //1  -1

    private int type;
    //文章发布者id
    private int authorId;

    //媒体（视频、音乐）的地址
    private List<String> mediaUrl;

    //图片部分(如果是视频则这里是缩略图)
    private List<String> imageContent;

    protected Article(Parcel in) {
        id = in.readInt();
        isHide = in.readInt();
        replyCount = in.readInt();
        showName = in.readString();
        time = in.readString();
        title = in.readString();
        userPhoto = in.readString();
        support = in.readInt();
        noSupport = in.readInt();
        textContent = in.readString();
        type = in.readInt();
        imageContent = in.createStringArrayList();
        mediaUrl = in.createStringArrayList();
        authorId = in.readInt();
        supportState =in.readInt();
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        try {
            JSONObject obj = new JSONObject(content);
            textContent = obj.optString("text");
            JSONArray array = obj.optJSONArray("files");
            JSONArray thumbs = obj.optJSONArray("thumb");
            if (null != thumbs && array != null) {
                imageContent = jsonArray2List(thumbs);
                mediaUrl = jsonArray2List(array);
            } else if (null != array && null == thumbs) {
                imageContent = jsonArray2List(array);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(null == textContent && null == imageContent && null == mediaUrl){
            textContent = content;
        }
    }

    //将jsonArray转为List--> "/photo/123.jpg","/photo/456.jpg"--放到List中--> "http://ip:port/ask/photo/123.jpg","http://ip:port/ask/photo/456.jpg"
    private List<String> jsonArray2List(JSONArray array) {
        List<String> list = new ArrayList<String>();
        int lenght = array.length();
        for (int i = 0; i < lenght; i++) {
            String str = array.optString(i);
            String url = Common.BASE_URL + str;
            list.add(url);
        }
        return list;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public List<String> getImageContent() {
        return imageContent;
    }

    public int getNoSupport() {
        return noSupport;
    }

    public void setNoSupport(int noSupport) {
        this.noSupport = noSupport;
    }

    public int getSupport() {
        return support;
    }

    public void setSupport(int support) {
        this.support = support;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(isHide);
        dest.writeInt(replyCount);
        dest.writeString(showName);
        dest.writeString(time);
        dest.writeString(title);
        dest.writeString(userPhoto);
        dest.writeInt(support);
        dest.writeInt(noSupport);
        dest.writeString(textContent);
        dest.writeInt(type);
        dest.writeStringList(imageContent);
        dest.writeStringList(mediaUrl);
        dest.writeInt(authorId);
        dest.writeInt(supportState);
    }

    public Article() {
    }

    public List<String> getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(List<String> mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public int getSupportState() {
        return supportState;
    }

    public void setSupportState(int supportState) {
        this.supportState = supportState;
    }
}
