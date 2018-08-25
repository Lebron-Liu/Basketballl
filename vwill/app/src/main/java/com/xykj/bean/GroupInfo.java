package com.xykj.bean;

public class GroupInfo {

    /**
     * creatorId : 0
     * date :
     * extras : 传道授业解惑也
     * groupName : 老师
     * id : 1
     * members : 7
     * parent : 0
     * status : -1
     */

    private int creatorId;
    private String date;
    private String extras;
    private String groupName;
    private int id;
    private int members;
    private int parent;
    private int status;

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMembers() {
        return members;
    }

    public void setMembers(int members) {
        this.members = members;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GroupInfo) {
            return id == ((GroupInfo) obj).id;
        }
        return super.equals(obj);
    }
}
