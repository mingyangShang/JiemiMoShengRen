package com.levelup.jiemimoshengren.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.levelup.jiemimoshengren.R;
import com.levelup.jiemimoshengren.adapter.ChatAllHistoryAdapter;
import com.levelup.jiemimoshengren.base.SmyApplication;
import com.levelup.jiemimoshengren.config.Constant;
import com.levelup.jiemimoshengren.db.InviteMessgeDao;

/**
 * Created by smy on 2015/3/4.
 */
public class MsgFragment extends Fragment implements
		AdapterView.OnItemClickListener {

	private ListView listView;
	private ChatAllHistoryAdapter adapter;
	public RelativeLayout errorItem;
	public TextView errorText;
	private boolean hidden;
	private List<EMConversation> conversationList = new ArrayList<EMConversation>();

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View chatView = inflater.inflate(R.layout.fragment_msg, container,false);
		initView(chatView);
		return chatView;
	}

	// 初始化View
	private void initView(View rootView) {
		errorItem = (RelativeLayout) rootView.findViewById(R.id.rl_error_item);
		errorText = (TextView) errorItem.findViewById(R.id.tv_connect_errormsg);

		conversationList.addAll(loadConversationsWithRecentChat());
		listView = (ListView) rootView.findViewById(R.id.list_chats);
		adapter = new ChatAllHistoryAdapter(getActivity(), 1, conversationList);
		// 设置adapter
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(this);
		// 注册上下文菜单
		registerForContextMenu(listView);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	/*
	 * @Override public void onCreateContextMenu(ContextMenu menu, View v,
	 * ContextMenuInfo menuInfo) { super.onCreateContextMenu(menu, v, menuInfo);
	 * // if(((AdapterContextMenuInfo)menuInfo).position > 0){ m,
	 * getActivity().getMenuInflater().inflate(R.menu.delete_message, menu); //
	 * } }
	 * 
	 * @Override public boolean onContextItemSelected(MenuItem item) { boolean
	 * handled = false; boolean deleteMessage = false; if (item.getItemId() ==
	 * R.id.delete_message) { deleteMessage = true; handled = true; } else if
	 * (item.getItemId() == R.id.delete_conversation) { deleteMessage = false;
	 * handled = true; } EMConversation tobeDeleteCons = adapter
	 * .getItem(((AdapterContextMenuInfo) item.getMenuInfo()).position); //
	 * 删除此会话 EMChatManager.getInstance().deleteConversation(
	 * tobeDeleteCons.getUserName(), tobeDeleteCons.isGroup(), deleteMessage);
	 * InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(getActivity());
	 * inviteMessgeDao.deleteMessage(tobeDeleteCons.getUserName());
	 * adapter.remove(tobeDeleteCons); adapter.notifyDataSetChanged();
	 * 
	 * // 更新消息未读数 ((MainActivity) getActivity()).updateUnreadMsgLabel();
	 * 
	 * return handled ? true : super.onContextItemSelected(item); }
	 */

	/** 刷新页面 */
	public void refreshUI() {
		conversationList.clear();
		conversationList.addAll(loadConversationsWithRecentChat());
		if (adapter != null)
			adapter.notifyDataSetChanged();
	}

	/** 刷新错误提示布局的显示状态 */
	public void refreshErrorItem(int visibility) {
		System.err.println("refreshErrorItem");
		if (errorItem == null) {
			System.err.println("erroritem is null");
		} else {
			if (errorItem.getVisibility() != visibility) {
				errorItem.setVisibility(visibility);
			}
		}
	}

	/** 为刷新错误提示布局设置错误提示文字 */
	public void setErrorItemText(String errText) {
		this.errorText.setText(errText);
	}

	/** 获取所有会话 */
	private List<EMConversation> loadConversationsWithRecentChat() {
		// 获取所有会话，包括陌生人
		Hashtable<String, EMConversation> conversations = EMChatManager
				.getInstance().getAllConversations();
		// 过滤掉messages size为0的conversation
		/**
		 * 如果在排序过程中有新消息收到，lastMsgTime会发生变化 影响排序过程，Collection.sort会产生异常
		 * 保证Conversation在Sort过程中最后一条消息的时间不变 避免并发问题
		 */
		List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
		synchronized (conversations) {
			for (EMConversation conversation : conversations.values()) {
				if (conversation.getAllMessages().size() != 0) {
					sortList.add(new Pair<Long, EMConversation>(conversation
							.getLastMessage().getMsgTime(), conversation));
				}
			}
		}
		try {
			// Internal is TimSort algorithm, has bug
			sortConversationByLastChatTime(sortList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<EMConversation> list = new ArrayList<EMConversation>();
		for (Pair<Long, EMConversation> sortItem : sortList) {
			list.add(sortItem.second);
		}
		return list;
	}

	/** 根据最后一条消息的时间排序 */
	private void sortConversationByLastChatTime(
			List<Pair<Long, EMConversation>> conversationList) {
		Collections.sort(conversationList,
				new Comparator<Pair<Long, EMConversation>>() {
					public int compare(final Pair<Long, EMConversation> con1,
							final Pair<Long, EMConversation> con2) {
						if (con1.first == con2.first) {
							return 0;
						} else if (con2.first > con1.first) {
							return 1;
						} else {
							return -1;
						}
					}
				});
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		this.hidden = hidden;
		if (!hidden) {
			refreshUI();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!hidden) {
			refreshUI();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// TODO 存储账号被移除和多地登录状态，现在不考虑
	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		EMConversation conversation = adapter.getItem(position);
		String username = conversation.getUserName();
		if (username.equals(SmyApplication.getSingleton().getUserName())) {
			final String hintMsg = getResources().getString(
					R.string.Cant_chat_with_yourself);
			Toast.makeText(getActivity(), hintMsg, Toast.LENGTH_SHORT).show();
		} else {
			// 进入聊天页面
			Intent intent = new Intent(getActivity(), ChatActivity.class);
			if (!conversation.isGroup()) { //单人会话
				intent.putExtra("userId", username);
			}
			startActivity(intent);
		}
	}
}
