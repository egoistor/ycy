package com.example.ycy.bean;

import android.os.Parcelable;

import com.avos.avoscloud.AVUser;

import java.io.Serializable;
import java.util.Date;

public class Event implements Serializable {
    private String id;
    private String title;
    private String detail;
    private Date creatTime;
    private boolean open;
    private AVUser owner;

    public Event(String id,String title, String detail, Date creatTime,boolean open,AVUser owner) {
        this.id = id;
        this.title = title;
        this.detail = detail;
        this.creatTime = creatTime;
        this.open = open;
        this.owner = owner;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Date getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(Date creatTime) {
        this.creatTime = creatTime;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public AVUser getOwner() {
        return owner;
    }

    public void setOwner(AVUser owner) {
        this.owner = owner;
    }
}
