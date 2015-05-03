package com.levelup.jiemimoshengren.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMNotifier;
import com.easemob.util.HanziToPinyin;
import com.easemob.util.NetUtils;
import com.easemod.chat.HXSDKHelper;
import com.levelup.jiemimoshengren.R;
import com.levelup.jiemimoshengren.base.BaseActivity;
import com.levelup.jiemimoshengren.base.SmyApplication;
import com.levelup.jiemimoshengren.config.Constant;
import com.levelup.jiemimoshengren.db.InviteMessgeDao;
import com.levelup.jiemimoshengren.db.UserDao;
import com.levelup.jiemimoshengren.model.InviteMessage;
import com.levelup.jiemimoshengren.model.InviteMessage.InviteMesageStatus;
import com.levelup.jiemimoshengren.model.User;
import com.levelup.jiemimoshengren.widget.PagerSlidingTabStrip;

/**
 * Created by smy on 2015/3/4. 解密陌生人 主界面
 */
public class MainActivityBackup extends BaseActivity implements EMEventListener,
		EMContactListener, EMConnectionListener {

	/* 聊天 */
	private MsgFragment chatFragment;
	/* 通讯录 */
	private ContactFragment contactFragment;
	/* 更多 */
	private SettingFragment moreFragment;

	private PagerSlidingTabStrip tabs;
	private ViewPager pager;

	private UserDao userDao;
	private InviteMessgeDao inviteMessgeDao;

	private static final String TABS_COLOR = "#45c01a";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_main_backup);
	}

	private void initListeners() {
		// 监听联系人列表的变化
		EMContactManager.getInstance().setContactListener(this);
		// 监听连接状态的变化
		EMChatManager.getInstance().addConnectionListener(this);
		// 通知SDK，UI初始化完毕，注册了相应的receiver和listener，可以接受Broadcast了
		EMChat.getInstance().setAppInited();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// TODO 在此处判断账号是否在别处登录，暂时不做处理
		if (true) {
			updateUnreadMsgLabel();
			updateUnreadContactLabel();
			EMChatManager.getInstance().activityResumed();
		}
		// register the event listener when enter the foreground
		EMChatManager.getInstance().registerEventListener(this,
						new EMNotifierEvent.Event[] { EMNotifierEvent.Event.EventNewMessage });
		initListeners();
	}

	@Override
	protected void onStop() {
		EMChatManager.getInstance().unregisterEventListener(this);
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void initData() {
		chatFragment = new MsgFragment();
		contactFragment = new ContactFragment();
		moreFragment = new SettingFragment();
	}

	@Override
	protected void initView() {
		pager = (ViewPager) getView(R.id.pager);
		tabs = (PagerSlidingTabStrip) getView(R.id.tabs);
		pager.setAdapter(new SlidingPagerAdapter(getSupportFragmentManager()));
		tabs.setViewPager(pager);
		setTabsValue();
	}

	private void setTabsValue() {
		tabs.setShouldExpand(true);
		tabs.setDividerColor(Color.TRANSPARENT);
		DisplayMetrics dm = getResources().getDisplayMetrics();
		tabs.setUnderlineHeight((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 1, dm));
		tabs.setIndicatorHeight((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 4, dm));
		tabs.setTextSize((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_SP, 16, dm));
		tabs.setIndicatorColor(Color.parseColor(TABS_COLOR));
		tabs.setSelectedTextColor(Color.parseColor(TABS_COLOR));
		tabs.setTabBackground(0);
	}

	public class SlidingPagerAdapter extends FragmentPagerAdapter {
		public SlidingPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		private final String[] titles = { "聊天", "通讯录", "更多" };

		@Override
		public CharSequence getPageTitle(int position) {
			return titles[position];
		}

		@Override
		public int getCount() {
			return titles.length;
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				if (chatFragment == null) {
					chatFragment = new MsgFragment();
				}
				return chatFragment;
			case 1:
				if (contactFragment == null) {
					contactFragment = new ContactFragment();
				}
				return contactFragment;
			case 2:
				if (moreFragment == null) {
					moreFragment = new SettingFragment();
				}
				return moreFragment;
			default:
				return null;
			}
		}
	}

	// 处理event
	public void onEvent(EMNotifierEvent event) {
		switch (event.getEvent()) {
		case EventNewMessage:
			EMMessage msg = (EMMessage) event.getData(); // 新消息
			// 提示新消息到来
			HXSDKHelper.getInstance().getNotifier().onNewMsg(msg);
			refreshUI();
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
				updateUnreadMsgLabel();
				// 当前界面为消息历史记录界面,刷新消息历史界面chatFragment
				if (pager.getCurrentItem() == 0) {
					chatFragment.refreshUI();
				}
			}
		});
	}

	// 刷新未读消息数
	protected void updateUnreadMsgLabel() {
		int unreadCount = getUnreadMsgCount();
		// TODO
		if (unreadCount > 0) {

		} else {

		}
	}

	// 刷新申请与通知消息数
	protected void updateUnreadContactLabel() {
		runOnUiThread(new Runnable() {
			public void run() {
				int unreadCount = getUnreadContactCount();
				if (unreadCount > 0) {

				} else {

				}
			}
		});
	}

	// 获取未读消息数
	private int getUnreadMsgCount() {
		return EMChatManager.getInstance().getUnreadMsgsCount();
	}

	// 获取未读申请与通知数
	private int getUnreadContactCount() {
		// 从联系人列表中查看是否有新名字
		User newFriends = SmyApplication.getSingleton().getContactList()
				.get(Constant.NEW_FRIENDS_USERNAME);
		if (newFriends != null) {
			return newFriends.getUnreadMsgCount();
		} else {
			return 0;
		}
	}

	public void onConnected() {
		runOnUiThread(new Runnable() {
			public void run() {
				// 设置消息历史界面的网络错误提醒布局不可见
				chatFragment.refreshErrorItem(View.GONE);
			}
		});
	}

	public void onDisconnected(final int error) {
		final String st1 = getResources().getString(
				R.string.Less_than_chat_server_connection);
		final String st2 = getResources().getString(
				R.string.the_current_network);
		runOnUiThread(new Runnable() {
			public void run() {
				if (error == EMError.USER_REMOVED) {
					// TODO 显示帐号已经被移除
				} else if (error == EMError.CONNECTION_CONFLICT) {
					// TODO 显示帐号在其他设备登陆dialog
				} else {
					if (chatFragment == null) {
						System.err.println("char==null");
					} else {
						/*chatFragment.refreshErrorItem(View.VISIBLE);
						if (NetUtils.hasNetwork(MainActivity.this))
							chatFragment.setErrorItemText(st1);
						else
							chatFragment.setErrorItemText(st2);*/
					}

				}
			}

		});
	}

	// 联系人增加
	public void onContactAdded(List<String> userNameList) {
		Map<String, User> localUsers = SmyApplication.getSingleton()
				.getContactList();
		Map<String, User> toAddUsers = new HashMap<String, User>();
		for (String userName : userNameList) {
			if (!localUsers.containsKey(userName)) {
				User user = setUserHead(userName);
				// 存储起来
				userDao.saveContact(user);
				toAddUsers.put(userName, user);
			}
		}
		localUsers.putAll(toAddUsers); // 新要加入的联系人加入到本地联系人列表
		// 如果在联系人界面的话，刷新列表
		if (pager.getCurrentItem() == 1) {
			contactFragment.refreshUI();
		}
	}

	public void onContactAgreed(String username) {
		List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
		for (InviteMessage inviteMessage : msgs) {
			if (inviteMessage.getFrom().equals(username)) {
				return;
			}
		}
		InviteMessage msg = new InviteMessage();
		msg.setFrom(username);
		msg.setTime(System.currentTimeMillis());
		Log.d("MainActivity", username + "同意了你的好友请求");
		msg.setStatus(InviteMesageStatus.BEAGREED);
		notifyNewIviteMessage(msg);

	}

	public void onContactDeleted(final List<String> arg0) {
		Map<String, User> localUsers = SmyApplication.getSingleton()
				.getContactList();
		for (String userName : arg0) {
			localUsers.remove(userName);
			userDao.deleteContact(userName);
			inviteMessgeDao.deleteMessage(userName);
		}
		runOnUiThread(new Runnable() {
			public void run() {
				// 如果正在与此用户的聊天页面
				String st10 = getResources().getString(
						R.string.have_you_removed);
				if (ChatActivity.activityInstance != null
						&& arg0.contains(ChatActivity.activityInstance
								.getToChatUsername())) {
					Toast.makeText(
							MainActivityBackup.this,
							ChatActivity.activityInstance.getToChatUsername()
									+ st10, 1).show();
					ChatActivity.activityInstance.finish();
				}
				updateUnreadMsgLabel();
				// 刷新ui
				contactFragment.refreshUI();
				chatFragment.refreshUI();
			}
		});
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
		Log.d("MainActivity", username + "请求加你为好友,reason: " + reason);
		// 设置相应status
		msg.setStatus(InviteMesageStatus.BEINVITEED);
		notifyNewIviteMessage(msg);
	}

	public void onContactRefused(String arg0) {
		// TODO 仿onContactInvited
	}

	// 生成一个配置好用户名和头信息的User
	private User setUserHead(String userName) {
		User user = new User();
		user.setUsername(userName);
		String headerName = null;
		if (!TextUtils.isEmpty(user.getNick())) {
			headerName = user.getNick();
		} else {
			headerName = user.getUsername();
		}
		if (userName.equals(Constant.NEW_FRIENDS_USERNAME)) {
			user.setHeader("");
		} else if (Character.isDigit(headerName.charAt(0))) {
			user.setHeader("#");
		} else {
			user.setHeader(HanziToPinyin.getInstance()
					.get(headerName.substring(0, 1)).get(0).target.substring(0,
					1).toUpperCase());
			char header = user.getHeader().toLowerCase().charAt(0);
			if (header < 'a' || header > 'z') {
				user.setHeader("#");
			}
		}
		return user;
	}

	// 保存提示新消息
	private void notifyNewIviteMessage(InviteMessage msg) {
		saveInviteMsg(msg);
		EMNotifier.getInstance(getApplicationContext()).notifyOnNewMsg(); // 提示有新消息
		updateUnreadContactLabel(); // 刷新bottom bar消息未读数
		// 刷新好友页面ui
		if (pager.getCurrentItem() == 1)
			contactFragment.refreshUI();
	}

	// 保存邀请信息
	private void saveInviteMsg(InviteMessage msg) {
		inviteMessgeDao.saveMessage(msg); // 保存msg
		User user = SmyApplication.getSingleton().getContactList()
				.get(Constant.NEW_FRIENDS_USERNAME); // 未读数加1
		if (user.getUnreadMsgCount() == 0)
			user.setUnreadMsgCount(user.getUnreadMsgCount() + 1);
	}
}
