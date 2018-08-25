package com.xykj.bean;

public class FunctionItem {
    private String title;
    private String descript;
    private int icon;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescript() {
        return descript;
    }

    public void setDescript(String descript) {
        this.descript = descript;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public FunctionItem(String title, String descript, int icon) {
        this.title = title;
        this.descript = descript;
        this.icon = icon;
    }
}
