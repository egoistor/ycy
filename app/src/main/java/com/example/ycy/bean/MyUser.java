package com.example.ycy.bean;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVUser;

@AVClassName("MyUser")
public class MyUser extends AVUser {
    public void setUserName(String name) {
        this.put("nickName", name);
    }

    public String getUserName() {
        return this.getString("UserName");
    }
}
