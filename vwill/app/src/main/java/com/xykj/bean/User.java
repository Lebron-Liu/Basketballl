package com.xykj.bean;

public class User {

    /**
     * id : 2
     * nick : aaa
     * photo : http://192.168.0.98:8080/AskMeServer/d?filename=aaa.jpg
     * question : 世界上最帅的人是谁？
     * sex : 男
     * sign : 哈哈哈哈尽快尽快将立即
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
}
