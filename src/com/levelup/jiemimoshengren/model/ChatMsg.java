package com.levelup.jiemimoshengren.model;

/**
 * 消息model
 * Created by smy on 2015/3/4.
 */
public class ChatMsg {
    public String imgUrl; //头像地址
    public String name; //备注姓名
    public String msg; //消息
    public String time; //消息时间
    public int type; //消息类型
    
    //消息类型
    public static final int TYPE_TEXT = 1; //文本
    public static final int TYPE_VOICE = 2; //语音
    public static final int TYPE_IMG = 3; //图片

    public static final String DEFAULT_HEAD_URL = "NONE";

    public ChatMsg(String imgUrl, String name, String msg, String time,int type) {
        this.imgUrl = imgUrl;
        this.name = name;
        this.msg = msg;
        this.time = time;
        this.type = type;
    }
    
    public ChatMsg(String imgUrl, String name, String msg, String time){
    	this(imgUrl, name, msg, time, TYPE_TEXT);
    }
    
}
