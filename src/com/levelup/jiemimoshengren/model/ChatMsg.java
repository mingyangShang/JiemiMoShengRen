package com.levelup.jiemimoshengren.model;

import java.util.Date;

/**
 * Created by smy on 2015/3/4.
 */
public class ChatMsg {
    public String imgUrl; //头像地址
    public String name; //备注姓名
    public String msg; //消息
    public String time; //消息时间

    public static final String DEFAULT_HEAD_URL = "NONE";

    public ChatMsg(String imgUrl, String name, String msg, String time) {
        this.imgUrl = imgUrl;
        this.name = name;
        this.msg = msg;
        this.time = time;
    }
}
