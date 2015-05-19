/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.levelup.jiemimoshengren.config;

public class Constant {
	public static final String NEW_FRIENDS_USERNAME = "item_new_friends";
	public static final String GROUP_USERNAME = "item_groups";
	public static final String MESSAGE_ATTR_IS_VOICE_CALL = "is_voice_call";
	public static final String MESSAGE_ATTR_IS_VIDEO_CALL = "is_video_call";
	public static final String ACCOUNT_REMOVED = "account_removed";
	
	//服务器交互配置
	public static final String HOST = "http://192.168.0.107:5000"; //主机名
	public static final String URL_REGISTER = HOST +"/user/register"; //注册url
	public static final String URL_LOGIN = HOST + "/user/login"; //登录url
	public static final String URL_UPLOAD = HOST + "/user/upload_img"; //上传图片url
	public static final String URL_SHAKE_FIRST = HOST + "/user/find"; //摇一摇第一次url
	public static final String URL_SHAKE_SECOND = HOST + "/user/refind"; //摇一摇第二次
	public static final String URL_USER_INFO = HOST + "/user/info"; //用户信息url
	public static final String URL_CONTACTS_INFO = HOST + "/user/contacts"; //联系人信息
}
