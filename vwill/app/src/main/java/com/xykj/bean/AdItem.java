package com.xykj.bean;

public class AdItem {

    /**
     * description : 天猫
     * id : 7
     * photo : /ad/ad_2_2.jpg
     * type : 2
     * url : https://www.tmall.com/
     */

    private String description;
    private int id;
    private String photo;
    private int type;
    private String url;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
