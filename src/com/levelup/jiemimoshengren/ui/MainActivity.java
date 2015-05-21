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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.easemob.EMConnectionListener;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMNotifier;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.HanziToPinyin;
import com.easemob.util.NetUtils;
import com.levelup.jiemimoshengren.R;
import com.levelup.jiemimoshengren.base.DefaultActivity;
import com.levelup.jiemimoshengren.base.SmyApplication;
import com.levelup.jiemimoshengren.config.Constant;
import com.levelup.jiemimoshengren.db.InviteMessgeDao;
import com.levelup.jiemimoshengren.db.UserDao;
import com.levelup.jiemimoshengren.model.InviteMessage;
import com.levelup.jiemimoshengren.model.InviteMessage.InviteMesageStatus;
import com.levelup.jiemimoshengren.model.User;
import com.smy.volley.extend.EasyJsonObject;

public class MainActivity extends DefaultActivity implements EMEventListener {

	protected static final String TAG = "MainActivity";

	// 未读消息textview
	private TextView unreadLabel;
	// 未读通讯录textview
	private TextView unreadAddressLable;

	private ImageButton[] mTabs;
	private ContactFragment contactListFragment;
	private MsgFragment chatHistoryFragment;
	private SettingFragment settingFragment;
	private Fragment[] fragments;
	private int index;
	// 当前fragment的index
	private int currentTabIndex;

	private InviteMessgeDao messgeDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_main);
		initListener();
	}

	private void initListener() {
		// setContactListener监听联系人的变化等
		EMContactManager.getInstance().setContactListener(
				new MyContactListener());
		// 注册一个监听连接状态的listener
		EMChatManager.getInstance().addConnectionListener(
				new MyConnectionListener());
		// 通知sdk，UI 已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
		EMChat.getInstance().setAppInited();
	}

	@Override
	protected void initData() {
		super.initData();
		inviteMessgeDao = new InviteMessgeDao(this);
		userDao = new UserDao(this);
		chatHistoryFragment = new MsgFragment();
		contactListFragment = new ContactFragment();
		settingFragment = new SettingFragment();
		fragments = new Fragment[] { chatHistoryFragment, contactListFragment,
				settingFragment };
		// 添加显示第一个fragment
		getSupportFragmentManager().beginTransaction()
				.add(R.id.fragment_container, chatHistoryFragment)
				.add(R.id.fragment_container, contactListFragment)
				.hide(contactListFragment).show(chatHistoryFragment).commit();
	}

	@Override
	protected void initView() {
		super.initView();
		unreadLabel = (TextView) findViewById(R.id.unread_msg_number);
		unreadAddressLable = (TextView) findViewById(R.id.unread_address_number);
		mTabs = new ImageButton[3];
		mTabs[0] = (ImageButton) findViewById(R.id.btn_conversation);
		mTabs[1] = (ImageButton) findViewById(R.id.btn_address_list);
		mTabs[2] = (ImageButton) findViewById(R.id.btn_setting);
		// 把第一个tab设为选中状态
		mTabs[0].setSelected(true);
	}

	public void onTabClicked(View view) {
		switch (view.getId()) {
		case R.id.btn_conversation:
			index = 0;
			break;
		case R.id.btn_address_list:
			index = 1;
			//设置新朋友数为0
			SmyApplication.getSingleton().getContacts().get(Constant.NEW_FRIENDS_USERNAME).setUnreadMsgCount(0);
			updateUnreadAddressLable();
			break;
		case R.id.btn_setting:
			index = 2;
			break;
		}
		handleTabChange();
	}

	// 处理Tab变化
	private void handleTabChange() {
		if (currentTabIndex != index) {
			FragmentTransaction trx = getSupportFragmentManager()
					.beginTransaction();
			trx.hide(fragments[currentTabIndex]);
			if (!fragments[index].isAdded()) {
				trx.add(R.id.fragment_container, fragments[index]);
			}
			trx.show(fragments[index]).commit();
		}
		mTabs[currentTabIndex].setSelected(false);
		// 把当前tab设为选中状态
		mTabs[index].setSelected(true);
		currentTabIndex = index;
	}

	/**
	 * 消息监听可以注册多个，SDK支持事件链的传递，不过一旦消息链中的某个监听返回能够处理某一事件，消息将不会进一步传递。
	 * 后加入的事件监听会先收到事件的通知 如果收到的事件，能够被处理并且不需要其他的监听再处理，可以返回true，否则返回false
	 */
	public void onEvent(EMNotifierEvent event) {
		switch (event.getEvent()) {
		case EventNewMessage:
			refreshUI();
			EMMessage message = (EMMessage) event.getData();
			SmyApplication.getSdkHelper().getNotifier().onNewMsg(message);
			break;
		case EventOfflineMessage:
			refreshUI();
			break;
		default:
			break;
		}
	}

	// 刷新界面
	private void refreshUI() {
		runOnUiThread(new Runnable() {
			public void run() {
				updateUnreadLabel(); // 刷新bottom bar消息未读数
				if (currentTabIndex == 0) {
					// 当前页面如果为聊天历史页面，刷新此页面
					if (chatHistoryFragment != null) {
						chatHistoryFragment.refreshUI();
					}
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 刷新未读消息数
	 */
	public void updateUnreadLabel() {
		int count = getUnreadMsgCountTotal();
		if (count > 0) {
			unreadLabel.setText(String.valueOf(count));
			unreadLabel.setVisibility(View.VISIBLE);
		} else {
			unreadLabel.setVisibility(View.INVISIBLE);
		}
	}

	/** 刷新申请与通知消息数 */
	public void updateUnreadAddressLable() {
		runOnUiThread(new Runnable() {
			public void run() {
				int count = getUnreadAddressCountTotal();
				if (count > 0) {
					unreadAddressLable.setText(String.valueOf(count));
					unreadAddressLable.setVisibility(View.VISIBLE);
				} else {
					unreadAddressLable.setVisibility(View.INVISIBLE);
				}
			}
		});
	}

	/** 获取未读申请与通知消息 */
	public int getUnreadAddressCountTotal() {
		int unreadAddressCountTotal = 0;
		if (SmyApplication.getSingleton().getContacts()
				.get(Constant.NEW_FRIENDS_USERNAME) != null)
			unreadAddressCountTotal = SmyApplication.getSingleton()
					.getContacts().get(Constant.NEW_FRIENDS_USERNAME)
					.getUnreadMsgCount();
		return unreadAddressCountTotal;
	}

	/** 获取未读消息数 */
	public int getUnreadMsgCountTotal() {
		int unreadMsgCountTotal = 0;
		unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
		return unreadMsgCountTotal;
	}

	private InviteMessgeDao inviteMessgeDao;
	private UserDao userDao;

	/** 好友变化Listener */
	private class MyContactListener implements EMContactListener {
		public void onContactAdded(List<String> usernameList) {
			final List<String> usernameList2 = usernameList;
			runOnUiThread(new Runnable() {
				public void run() {
					SmyApplication.getSingleton().getContacts().get(Constant.NEW_FRIENDS_USERNAME).setUnreadMsgCount(usernameList2.size());
					unreadAddressLable.setText(""+usernameList2.size());
					unreadAddressLable.setVisibility(View.VISIBLE);
				}
			});
		
			try {
				processContacts(usernameList);
			} catch (EaseMobException e) {
				e.printStackTrace();
			}
			// 刷新ui
		}

		public void onContactDeleted(final List<String> usernameList) {
			// 被删除
			Map<String, User> localUsers = SmyApplication.getSingleton()
					.getContacts();
			for (String username : usernameList) {
				if (localUsers.containsKey(username)) {
					userDao.deleteContact(username);
				}
				localUsers.remove(username);
			}
			if (currentTabIndex == 1)
				contactListFragment.refreshUI();
		}

		public void onContactInvited(String username, String reason) {
			// 接到邀请的消息，如果不处理(同意或拒绝)，掉线后，服务器会自动再发过来，所以客户端不需要重复提醒
			List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
			for (InviteMessage inviteMessage : msgs) {
				if (inviteMessage.getGroupId() == null
						&& inviteMessage.getFrom().equals(username)) {
					inviteMessgeDao.deleteMessage(username);
				}
			}
			InviteMessage msg = new InviteMessage();
			msg.setFrom(username);
			msg.setTime(System.currentTimeMillis());
			msg.setReason(reason);
			System.err.println(username + "请求加你为好友,reason: " + reason);
			// 设置相应status
			msg.setStatus(InviteMesageStatus.BEINVITEED);
			notifyNewIviteMessage(msg);
			// acceptInvitation(msg); //当被申请添加好友后马上自动同意对方
		}

		public void onContactAgreed(String username) {
			List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
			for (InviteMessage inviteMessage : msgs) {
				if (inviteMessage.getFrom().equals(username)) {
					return;
				}
			}
			// 自己封装的javabean
			InviteMessage msg = new InviteMessage();
			msg.setFrom(username);
			msg.setTime(System.currentTimeMillis());
			Log.d(TAG, username + "同意了你的好友请求");
			msg.setStatus(InviteMesageStatus.BEAGREED);
			notifyNewIviteMessage(msg);
		}

		public void onContactRefused(String username) {
			// 参考同意，被邀请实现此功能,TODO 暂时不考虑
			Log.d(username, username + "拒绝了你的好友请求");
		}
	}

	/** 连接监听 */
	private class MyConnectionListener implements EMConnectionListener {

		public void onConnected() {
			runOnUiThread(new Runnable() {
				public void run() {
					chatHistoryFragment.errorItem.setVisibility(View.GONE);
				}
			});
		}

		public void onDisconnected(final int error) {
			runOnUiThread(new Runnable() {
				public void run() {
					chatHistoryFragment.errorItem.setVisibility(View.VISIBLE);
					if (NetUtils.hasNetwork(MainActivity.this)) {
						chatHistoryFragment.errorText.setText(getString(
								R.string.Less_than_chat_server_connection));
					} else {
						chatHistoryFragment.errorText.setText(getString(
								R.string.the_current_network));
					}
				}
			});
		}
	}

	/** 保存提示新消息 */
	private void notifyNewIviteMessage(InviteMessage msg) {
		saveInviteMsg(msg);
		// 提示有新消息
		EMNotifier.getInstance(getApplicationContext()).notifyOnNewMsg();
		// 刷新bottom bar消息未读数
		updateUnreadAddressLable();
		// 刷新好友页面ui
		if (currentTabIndex == 1)
			contactListFragment.refreshUI();
	}

	/** 保存邀请等msg */
	private void saveInviteMsg(InviteMessage msg) {
		// 保存msg
		inviteMessgeDao.saveMessage(msg);
		// 未读数加1
		User user = SmyApplication.getSingleton().getContacts()
				.get(Constant.NEW_FRIENDS_USERNAME);
		if (user.getUnreadMsgCount() == 0)
			user.setUnreadMsgCount(user.getUnreadMsgCount() + 1);
	}

	/** 查询用户信息 */
	protected User queryUserInfo(final String uid, final int flag,
			final String... args) {
		final User user = new User();
		JSONObject contactQueryJson = makeQueryJson(uid);
		requestQueue.add(new JsonObjectRequest(Constant.URL_CONTACTS_INFO,
				contactQueryJson, new Listener<JSONObject>() {
					public void onResponse(JSONObject response) {
						EasyJsonObject contactsJson = new EasyJsonObject(
								response);
						if (contactsJson.getBoolean("success")) {
							EasyJsonObject contact = contactsJson
									.getStringAsJSONObject("msg");
							processContact(contact, user);
						} else {
							showMsg(contactsJson.getString("error"));
						}
					}
				}, this));
		return user;
	}

	/** 产生查询用户信息的json */
	private JSONObject makeQueryJson(final String uid) {
		EasyJsonObject queryJson = new EasyJsonObject();
		queryJson.put("uid", uid);
		return queryJson;
	}

	/** 处理用户信息 */
	private void processContact(EasyJsonObject contactJson, User user) {
		user.setUsername(contactJson.getString("uid"));
		user.setNick(contactJson.getString("nick"));
		user.setFemale(contactJson.getString("sex").equals(User.SEX_FEMALE));
		user.setSign(contactJson.getString("sign"));
		user.setImgUrl(contactJson.getString("head"));
		// setUserHeader(user.getUsername(),user);
		// userlist.put(user.getUsername(), user);
	}

	/**处理用户和群组信息*/
	private void processContacts(List<String> usernames) throws EaseMobException {
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
				}
			}
		}, this));
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
	
	/**处理获得的好友信息
	 * @throws JSONException */
	private void processContacts(JSONArray contacts) throws JSONException{
		//要添加的user
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
		//加入现在的好友列表
		Map<String,User> localUsers = SmyApplication.getSingleton().getContacts();
		localUsers.putAll(userlist);
		
		// 存入内存
		SmyApplication.getSingleton().setContacts(localUsers);
		
		//刷新ui
		if (currentTabIndex == 1)
			contactListFragment.refreshUI();
		
		// 存入db
		UserDao dao = new UserDao(MainActivity.this);
		List<User> users = new ArrayList<User>(localUsers.values());
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

	@Override
	protected void onResume() {
		super.onResume();
		EMChatManager
				.getInstance()
				.registerEventListener(
						this,
						new EMNotifierEvent.Event[] { EMNotifierEvent.Event.EventNewMessage });
		updateUnreadLabel(); //进入界面时刷新未读消息提示数	
		updateUnreadAddressLable();
	}
	
	@Override
	protected void onStop() {
		EMChatManager.getInstance().unregisterEventListener(this);
		super.onStop();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			moveTaskToBack(true);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
