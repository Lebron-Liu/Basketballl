package com.xykj.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class MediaItem implements Parcelable{
    private String path;
    private boolean isChecked;
    private String name;
    private int defIcon;

    protected MediaItem(Parcel in) {
        path = in.readString();
        name = in.readString();
        defIcon = in.readInt();
    }

    public static final Creator<MediaItem> CREATOR = new Creator<MediaItem>() {
        @Override
        public MediaItem createFromParcel(Parcel in) {
            return new MediaItem(in);
        }

        @Override
        public MediaItem[] newArray(int size) {
            return new MediaItem[size];
        }
    };

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDefIcon() {
        return defIcon;
    }

    public void setDefIcon(int defIcon) {
        this.defIcon = defIcon;
    }

    public MediaItem(String path, String name, int defIcon) {
        this.path = path;
        this.name = name;
        this.defIcon = defIcon;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof MediaItem){
            return path.equals(((MediaItem)obj).path);
        }
        return super.equals(obj);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(path);
        parcel.writeString(name);
        parcel.writeInt(defIcon);
    }
}
