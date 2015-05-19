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
package com.levelup.jiemimoshengren.ui;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.easemob.chat.EMContactManager;
import com.levelup.jiemimoshengren.R;
import com.levelup.jiemimoshengren.base.DefaultActivity;
import com.levelup.jiemimoshengren.base.SmyApplication;
import com.levelup.jiemimoshengren.config.Constant;
import com.smy.volley.extend.EasyJsonObject;

public class AddContactActivity extends DefaultActivity {
	private EditText editText;
	private LinearLayout searchedUserLayout;
	private TextView nameText, mTextView;
	private Button searchBtn;
	private ImageView avatar;
	private InputMethodManager inputMethodManager;

	private ProgressDialog progressDialog;

	private String toAddUserId; // 添加的新朋友的id即环信中的username

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_add_contact);
	}

	@Override
	protected void initData() {
		super.initData();
		inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	}

	@Override
	protected void initView() {
		super.initView();
		mTextView = (TextView) findViewById(R.id.add_list_friends);
		editText = (EditText) findViewById(R.id.edit_note);
		String strAdd = getResources().getString(R.string.add_friend);
		mTextView.setText(strAdd);
		editText.setHint(getResources().getString(R.string.user_name));
		searchedUserLayout = (LinearLayout) findViewById(R.id.ll_user);
		nameText = (TextView) findViewById(R.id.name);
		searchBtn = (Button) findViewById(R.id.search);
		avatar = (ImageView) findViewById(R.id.avatar);
	}

	/**
	 * 查找某用户,在xml中绑定监听
	 * @param v
	 */
	public void searchContact(View v) {
		final String nick = editText.getText().toString();
		final String saveText = searchBtn.getText().toString();

		if (getString(R.string.button_search).equals(saveText)) {
			if (TextUtils.isEmpty(nick)) {
				showMsg(getString(R.string.Please_enter_a_username));
			} else {
				this.progressDialog = makeProgressDialog(this,
						getString(R.string.finding_user), null);
				this.progressDialog.show();
				doSearchContact(nick);
			}
		}
	}

	/** 搜索用户 */
	private void doSearchContact(final String nick) {
		final JSONObject searchJson = makeSearchJson(nick);
		requestQueue.add(new JsonObjectRequest(Constant.URL_USER_INFO,
				searchJson, new Listener<JSONObject>() {
					public void onResponse(JSONObject response) {
						recyclePd();
						EasyJsonObject easyJson = new EasyJsonObject(response);
						if (easyJson.getBoolean("success")) {
							EasyJsonObject userJson = easyJson
									.getStringAsJSONObject("msg"); // 查找到的用户信息
							toAddUserId = userJson.getString("uid");
							// 服务器存在此用户，显示此用户和添加按钮
							searchedUserLayout.setVisibility(View.VISIBLE);
							nameText.setText(nick);
						} else {
							showMsg(easyJson.getString("error"));
						}
					}
				}, this));
	}

	/** 创建搜索用户的json */
	private JSONObject makeSearchJson(final String nick) {
		EasyJsonObject searchJson = new EasyJsonObject();
		searchJson.put("nick", nick);
		return searchJson;
	}

	/** 环信搜索用户 */
	private void doHuanxinAddContact(final String uid) {
		if (SmyApplication.getSingleton().getMe().getUsername()
				.equals(toAddUserId)) {
			showMsgFromRes(R.string.not_add_myself);
			return;
		}

		if (SmyApplication.getSingleton().getContacts()
				.containsKey(toAddUserId)) {
			// 提示已在好友列表中，无需添加
			showMsgFromRes(R.string.This_user_is_already_your_friend);
			return;
		}

		progressDialog = makeProgressDialog(this,
				getString(R.string.Is_sending_a_request), null);
		progressDialog.show();

		new Thread(new Runnable() {
			public void run() {
				try {
					// demo写死了个reason，实际应该让用户手动填入
					EMContactManager.getInstance().addContact(toAddUserId,
							getString(R.string.Add_a_friend));
					runOnUiThread(new Runnable() {
						public void run() {
							recyclePd();
							showMsgFromRes(R.string.send_successful);
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							recyclePd();
							showMsg(getString(R.string.Request_add_buddy_failure) + e.getMessage());
						}
					});
				}
			}
		}).start();
	}

	/** 回收proressdialog */
	private void recyclePd() {
		if (this.progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	/**
	 * 添加contact
	 * @param view
	 */
	public void addContact(View view) {
		doHuanxinAddContact(toAddUserId);
	}

	/** 处理volley请求错误 */
	public void onErrorResponse(VolleyError error) {
		super.onErrorResponse(error);
		recyclePd();
	}
}
