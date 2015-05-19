package com.levelup.jiemimoshengren.ui;

import org.json.JSONObject;

import android.os.Bundle;

import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;
import com.levelup.jiemimoshengren.R;
import com.levelup.jiemimoshengren.base.DefaultActivity;
import com.levelup.jiemimoshengren.base.SmyApplication;
import com.levelup.jiemimoshengren.config.Constant;
import com.levelup.jiemimoshengren.model.User;
import com.smy.volley.extend.EasyJsonObject;

/** 个人信息界面(其他好友的) */
public class UserInfoActivity extends DefaultActivity {

	private User user; // user对象

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_user_info);
	}

	@Override
	protected void initData() {
		super.initData();
	}

	@Override
	protected void initView() {
		super.initView();
	}

	/** 获取某人的信息 */
	private void getUserInfo(final String uid) {
		JSONObject queryUserJson = makeQueryUserJson(uid);
		requestQueue.add(new JsonObjectRequest(Constant.URL_USER_INFO,
				queryUserJson, new Listener<JSONObject>() {
					public void onResponse(JSONObject response) {
						EasyJsonObject userJson = new EasyJsonObject(response);
						if (userJson.getBoolean("success")) {
							User user = new User();
							userJson = userJson.getStringAsJSONObject("msg");
							user.setUsername(userJson.getString("uid"));
							user.setNick(userJson.getString("nick"));
							user.setFemale(userJson.getString("sex").equals(
									User.SEX_FEMALE));
							user.setSign(userJson.getString("sign"));
							user.setImgUrl(userJson.getString("head"));
							refreshUI();
						} else {
							showMsg(userJson.getString("error"));
						}
					}
				}, this));
	}

	/** 创建查询用户信息的json */
	private JSONObject makeQueryUserJson(final String uid) {
		EasyJsonObject queryUserJson = new EasyJsonObject();
		queryUserJson.put("uid", uid);
		return queryUserJson;
	}

	/** 刷新UI */
	private void refreshUI() {

	}

	/** 添加好友 */
	private void addContact(final String uid, final String reason) {
		new Thread(new Runnable() {
			public void run() {
				try {
					EMContactManager.getInstance().addContact(uid,reason);
					runOnUiThread(new Runnable() {
						public void run() {
							showMsgFromRes(R.string.add_successful);
						}
					});
					//发送请求后默认对方已同意，将对方加入自己的好友
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							showMsg(getString(R.string.Request_add_buddy_failure) + e.getMessage());
						}
					});
				}
			}
		}).start();
	}

}
