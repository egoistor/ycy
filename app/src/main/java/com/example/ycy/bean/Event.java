package com.example.ycy.bean;

import java.util.Date;

public class Event {
    private String id;
    private String title;
    private String detail;
    private Date creatTime;
    private boolean isopen;

    public Event(String id,String title, String detail, Date creatTime,boolean isopen) {
        this.id = id;
        this.title = title;
        this.detail = detail;
        this.creatTime = creatTime;
        this.isopen = isopen;
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
        return isopen;
    }

    public void setOpen(boolean open) {
        this.isopen = open;
    }
}
