package com.xykj.bean;

public class Reply {

    /**
     * articleId : 3
     * content : 再匿名一次
     * id : 3
     * isHide : 1
     * parent : 2
     * photo : http://111.com/sss.jpg
     * showName : 郭靖
     * time : 1417743322294
     * userId : 1
     */

    private int articleId;
    private String content;
    private int id;
    private int isHide;
    private int parent;
    private String photo;
    private String showName;
    private String time;
    private int userId;

    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
