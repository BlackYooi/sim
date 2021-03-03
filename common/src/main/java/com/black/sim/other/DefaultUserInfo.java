package com.black.sim.other;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.black.sim.protobuf.DefaultProtoMsg;
import lombok.Setter;

/**
 * @description：
 * @author：8568
 */
public class DefaultUserInfo implements UserInfo{

    private String uid = "";
    private String userName = "";
    private String password = "";

    @Override
    public String getUid() {
        return uid;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public static UserInfo fromMsg(DefaultProtoMsg.ProtoMsg.LoginRequest message) {
        String other = message.getJson();
        UserInfo u = JSON.parseObject(other, DefaultUserInfo.class);
        return u;
    }

    public DefaultUserInfo setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public DefaultUserInfo setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public DefaultUserInfo setPassword(String password) {
        this.password = password;
        return this;
    }
}
