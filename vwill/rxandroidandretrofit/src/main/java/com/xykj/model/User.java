package com.xykj.model;

/**
 * Created by admin on 2016/10/11.
 */
public class User {

    /**
     * id : 10000003
     * nick : qwqw
     * photo : /upload/1476106721987.jpg
     * question :
     * sex : 男
     * sign : 啊啊所大撒所多阿斯达所
     */

    private int id;
    private String nick;
    private String photo;
    private String question;
    private String sex;
    private String sign;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nick='" + nick + '\'' +
                ", photo='" + photo + '\'' +
                ", question='" + question + '\'' +
                ", sex='" + sex + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
