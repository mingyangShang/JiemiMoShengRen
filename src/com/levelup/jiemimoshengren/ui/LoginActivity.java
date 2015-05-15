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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.HanziToPinyin;
import com.levelup.jiemimoshengren.R;
import com.levelup.jiemimoshengren.base.DefaultActivity;
import com.levelup.jiemimoshengren.base.SmyApplication;
import com.levelup.jiemimoshengren.config.Constant;
import com.levelup.jiemimoshengren.db.UserDao;
import com.levelup.jiemimoshengren.model.User;
import com.levelup.jiemimoshengren.utils.CommonUtils;
import com.smy.volley.extend.EasyJsonObject;

/**
 * 登陆页面
 * 
 */
public class LoginActivity extends DefaultActivity implements OnCancelListener{

	public static final int REQUEST_CODE_SETNICK = 1;
	private EditText usernameEditText;
	private EditText passwordEditText;
	private ProgressDialog pd;

	private boolean progressShow;
	private boolean autoLogin = false;

	private String currentUsername;
	private String currentPassword;
	
	private User me; //标识当前用户，仅当登录成功后才使用

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState,R.layout.activity_login);

		// 如果用户名密码都有，直接进入主页面,TODO暂时让每次都是登录界面
		/*if (SmyHXSDKHelper.getInstance().isLogined()) {
			autoLogin = true;
			startActivity(new Intent(LoginActivity.this, MainActivity.class));

			return;
		}*/
	}
	
	@Override
	protected void initData() {
		super.initData();
	}

	@Override
	protected void initView() {
		super.initView();
		usernameEditText = (EditText) findViewById(R.id.username);
		passwordEditText = (EditText) findViewById(R.id.password);
		
		// 如果用户名改变，清空密码
		usernameEditText.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,int count) {
				passwordEditText.setText(null);
			}
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
			public void afterTextChanged(Editable s) {}
		});
		if (SmyApplication.getSingleton().getUserName() != null) {
			usernameEditText.setText(SmyApplication.getSingleton().getUserName());
		}
	}

	/**登录,绑定在xml中*/
	public void login(View view) {
		if (!CommonUtils.isNetWorkConnected(this)) {
			showMsgFromRes(R.string.network_isnot_available);
			return;
		}
		currentUsername = usernameEditText.getText().toString().trim();
		currentPassword = passwordEditText.getText().toString().trim();

		if (TextUtils.isEmpty(currentUsername)) {
			showMsgFromRes(R.string.User_name_cannot_be_empty);
			return;
		}
		if (TextUtils.isEmpty(currentPassword)) {
			showMsgFromRes(R.string.Password_cannot_be_empty);
			return;
		}
		userLogin(currentUsername,currentPassword);
	}
	
	/**注册*/
	public void register(View view) {
		startActivityForResult(new Intent(this, RegisterActivity.class), 0);
	}

	/**处理用户和群组信息*/
	private void processContactsAndGroups() throws EaseMobException {
		// demo中简单的处理成每次登陆都去获取好友username，开发者自己根据情况而定
		List<String> usernames = EMContactManager.getInstance().getContactUserNames();
		//获得用户名后再向应用服务器查询用户的具体信息
		JSONObject contactsQueryJson = makeContactsQueryJson(usernames);
		requestQueue.add(new JsonObjectRequest(Constant.URL_CONTACTS_INFO,contactsQueryJson,new Listener<JSONObject>() {
			public void onResponse(JSONObject response) {
				EasyJsonObject contactsJson = new EasyJsonObject(response);
				if(contactsJson.getBoolean("success")){
					JSONArray contacts = contactsJson.getStringAsJSONArray("msg");
					try {
						processContacts(contacts);
					} catch (JSONException e) {
						e.printStackTrace();
						System.err.println("解析contacts的json数据错误");
					}
					EMChatManager.getInstance().updateCurrentUserNick(currentUsername);
					if (!LoginActivity.this.isFinishing() && pd!=null) {pd.dismiss();}
					// 进入主页面
					goToWithFinish(MainActivity.class);
				}else{
					showMsg(contactsJson.getString("error"));
				}
			}
		}, this));
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (autoLogin) {
			return;
		}
	}
	
	/**处理获得的好友信息
	 * @throws JSONException */
	private void processContacts(JSONArray contacts) throws JSONException{
		Map<String, User> userlist = new HashMap<String, User>();
		for(int i=0,len=contacts.length();i<len;++i){
			EasyJsonObject contact = new EasyJsonObject(contacts.getJSONObject(i));
			User user = new User();
			user.setUsername(contact.getString("uid"));
			user.setNick(contact.getString("nick"));
			user.setFemale(contact.getString("sex").equals(User.SEX_FEMALE));
			user.setSign(contact.getString("sign"));
			user.setImgUrl(contact.getString("head"));
			setUserHeader(user.getUsername(),user);
			userlist.put(user.getUsername(), user);
		}
		
		// 添加user"申请与通知"
		User newFriends = new User();
		newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
		newFriends.setNick(getString(R.string.Application_and_notify));
		userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends);

		// 存入内存
		SmyApplication.getSingleton().setContacts(userlist);
		
		// 存入db
		UserDao dao = new UserDao(LoginActivity.this);
		List<User> users = new ArrayList<User>(userlist.values());
		dao.saveContactList(users);
	}

	/**
	 * 设置hearder属性，方便通讯中对联系人按header分类显示，以及通过右侧ABCD...字母栏快速定位联系人
	 * @param username
	 * @param user
	 */
	protected void setUserHeader(String username, User user) {
		String headerName = null;
		if (!TextUtils.isEmpty(user.getNick())) {
			headerName = user.getNick();
		} else {
			headerName = user.getUsername();
		}
		if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
			user.setHeader("");
		} else if (Character.isDigit(headerName.charAt(0))) {
			user.setHeader("#");
		} else {
			user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1)).get(0).target.substring(0,1).toUpperCase());
			char header = user.getHeader().toLowerCase().charAt(0);
			if (header < 'a' || header > 'z') {
				user.setHeader("#");
			}
		}
	}
	
	/**登录环信*/
	private void huanxinLogin(String uid,String password){
		EMChatManager.getInstance().login(uid, password, new EMCallBack() {
			public void onSuccess() {
				// 登陆成功，保存用户名密码
				SmyApplication.getSingleton().setUserName(currentUsername);
				SmyApplication.getSingleton().setPassword(currentPassword);
				SmyApplication.getSingleton().setMe(me);
				runOnUiThread(new Runnable() {
					public void run() {
						if(pd==null){
							pd = makeProgressDialog(LoginActivity.this,getString(R.string.Is_landing),LoginActivity.this);
						}
						pd.setMessage(getString(R.string.list_is_for));
					}
				});
				try {
					//第一次登录或者之前logout后再登录，加载所有本地群和回话
					EMGroupManager.getInstance().loadAllGroups();
					EMChatManager.getInstance().loadAllConversations();
					processContactsAndGroups(); // 处理好友和群组
				} catch (Exception e) {
					// 取好友或者群聊失败，不让进入主页面
					runOnUiThread(new Runnable() {
						public void run() {
							if(pd!=null){pd.dismiss();}
							SmyApplication.getSingleton().logout(null);
							showMsg(getString(R.string.login_failure_failed));
						}
					});
					return;
				}
			}
			
			public void onProgress(int arg0, String arg1) {}
			
			public void onError(int arg0, String arg1) {
				if (!progressShow || pd==null) {
					return;
				}
				runOnUiThread(new Runnable() {
					public void run() {
						pd.dismiss();
						showMsg(getString(R.string.Login_failed));
					}
				});
			}
		});
	}
	
	/**向应用服务器请求登录*/
	private void userLogin(final String username,final String password){
		JSONObject loginJson = makeLoginJson(username,password);
		this.requestQueue.add(new JsonObjectRequest(Constant.URL_LOGIN,loginJson, new Listener<JSONObject>() {
			public void onResponse(JSONObject response) {
				EasyJsonObject easyJsonObject = new EasyJsonObject(response);
				if(easyJsonObject.getBoolean("success")){ //如果登录成功
					EasyJsonObject msgJsonObject = easyJsonObject.getStringAsJSONObject("msg");
					me = new User();
					me.setUsername(msgJsonObject.getString("uid"));
					me.setNick(msgJsonObject.getString("nick"));
					me.setImgUrl(msgJsonObject.getString("head"));
					me.setFemale(msgJsonObject.getString("sex").equals(User.SEX_FEMALE));
					//环信登录，使用id和password
					huanxinLogin(me.getUsername(), password);
				}else{ //登录失败
					showMsg(easyJsonObject.getString("error"));
				}
		}}, this));
	}
	
	/**创建登录json*/
	private JSONObject makeLoginJson(final String username,final String password){
		EasyJsonObject jsonObject = new EasyJsonObject();
		jsonObject.put("nick", username);
		jsonObject.put("password", password);
		return jsonObject;
	}
	
	/**创建查询联系人信息的json*/
	private JSONObject makeContactsQueryJson(List<String> usernames){
		EasyJsonObject easyJsonObject = new EasyJsonObject();
		StringBuffer contacts = new StringBuffer();
		for(int i=0,size=usernames.size();i<size;++i){
			contacts.append(usernames.get(i));
			if(i!=size-1){
				contacts.append(",");
			}
		}
		easyJsonObject.put("contacts", contacts.toString());
		return easyJsonObject;
	}
	
	/**用特定的用户名重置登录表单*/
	private void resetLoginForm(final String username){
		usernameEditText.setText(username);
		passwordEditText.setText("");
	}
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if(arg1 == RESULT_OK){ //注册成功，反馈信息显示
			final String username = arg2.getStringExtra("username");
			resetLoginForm(username);
		}
	}

	/**对话框取消事件*/
	public void onCancel(DialogInterface dialog) {
		progressShow = false;
	}

}
