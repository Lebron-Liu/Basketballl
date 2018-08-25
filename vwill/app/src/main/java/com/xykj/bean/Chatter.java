package com.xykj.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 聊天的用户信息
 */
public class Chatter implements Parcelable{
    private int userId;
    private String nick;
    private String photo;

    protected Chatter(Parcel in) {
        userId = in.readInt();
        nick = in.readString();
        photo = in.readString();
    }

    public static final Creator<Chatter> CREATOR = new Creator<Chatter>() {
        @Override
        public Chatter createFromParcel(Parcel in) {
            return new Chatter(in);
        }

        @Override
        public Chatter[] newArray(int size) {
            return new Chatter[size];
        }
    };

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public Chatter(int userId, String nick, String photo) {
        this.userId = userId;
        this.nick = nick;
        this.photo = photo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(userId);
        parcel.writeString(nick);
        parcel.writeString(photo);
    }
}
