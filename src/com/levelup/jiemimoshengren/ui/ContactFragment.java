package com.levelup.jiemimoshengren.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.easemob.chat.EMContactManager;
import com.levelup.jiemimoshengren.R;
import com.levelup.jiemimoshengren.adapter.ContactAdapter;
import com.levelup.jiemimoshengren.base.SmyApplication;
import com.levelup.jiemimoshengren.config.Constant;
import com.levelup.jiemimoshengren.db.UserDao;
import com.levelup.jiemimoshengren.model.User;
import com.levelup.jiemimoshengren.widget.Sidebar;
/**
 * Created by smy on 2015/3/4.
 * 联系人列表页
*/

public class ContactFragment extends Fragment {
	private ContactAdapter adapter;
	private List<User> contactList;
	private ListView listView;
	private boolean hidden;
	private Sidebar sidebar;
	private InputMethodManager inputMethodManager;
	private List<String> blackList;
	ImageButton clearSearch;
	EditText query;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_contact, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
		if(savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
		    return;
		inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		listView = (ListView) getView().findViewById(R.id.list);
		sidebar = (Sidebar) getView().findViewById(R.id.sidebar);
		sidebar.setListView(listView);
		//黑名单列表
		blackList = EMContactManager.getInstance().getBlackListUsernames();
		contactList = new ArrayList<User>();
		// 获取设置contactlist
		getContactList();
		
		//搜索框
		query = (EditText) getView().findViewById(R.id.query);
		query.setHint(R.string.search);
		clearSearch = (ImageButton) getView().findViewById(R.id.search_clear);
		query.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				adapter.getFilter().filter(s);
				if (s.length() > 0) {
					clearSearch.setVisibility(View.VISIBLE);
				} else {
					clearSearch.setVisibility(View.INVISIBLE);
					
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void afterTextChanged(Editable s) {
			}
		});
		clearSearch.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				query.getText().clear();
				hideSoftKeyboard();
			}
		});
		
		// 设置adapter
		adapter = new ContactAdapter(getActivity(), R.layout.row_contact, contactList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String username = adapter.getItem(position).getUsername();
				if (Constant.NEW_FRIENDS_USERNAME.equals(username)) {
					// 进入申请与通知页面
					User user = SmyApplication.getSingleton().getContactList().get(Constant.NEW_FRIENDS_USERNAME);
					user.setUnreadMsgCount(0);
					startActivity(new Intent(getActivity(), NewFriendsMsgActivity.class));
				} else {
					// demo中直接进入聊天页面，实际一般是进入用户详情页
					startActivity(new Intent(getActivity(), ChatActivity.class).putExtra("userId", adapter.getItem(position).getUsername()));
				}
			}
		});
		listView.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				v.performClick();
				// 隐藏软键盘
				if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
					if (getActivity().getCurrentFocus() != null)
						inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
				}
				return false;
			}
		});

		ImageView addContactView = (ImageView) getView().findViewById(R.id.iv_new_contact);
		// 进入添加好友页
		addContactView.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				//TODO 跳转到添加联系人界面
//				startActivity(new Intent(getActivity(), AddContactActivity.class));
			}
		});
		registerForContextMenu(listView);

	}

/*	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		// 长按前两个不弹menu
		if (((AdapterContextMenuInfo) menuInfo).position > 1) {
			getActivity().getMenuInflater().inflate(R.menu.context_contact_list, menu);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.delete_contact) {
			User tobeDeleteUser = adapter.getItem(((AdapterContextMenuInfo) item.getMenuInfo()).position);
			// 删除此联系人
			deleteContact(tobeDeleteUser);
			// 删除相关的邀请消息
			InviteMessgeDao dao = new InviteMessgeDao(getActivity());
			dao.deleteMessage(tobeDeleteUser.getUsername());
			return true;
		}else if(item.getItemId() == R.id.add_to_blacklist){
			User user = adapter.getItem(((AdapterContextMenuInfo) item.getMenuInfo()).position);
			moveToBlacklist(user.getUsername());
			return true;
		}
		return super.onContextItemSelected(item);
	}
*/
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

	/**
	 * 删除联系人
	 * 
	 * @param toDeleteUser
	 */
	public void deleteContact(final User tobeDeleteUser) {
		String st1 = getResources().getString(R.string.deleting);
		final String st2 = getResources().getString(R.string.Delete_failed);
		final ProgressDialog pd = new ProgressDialog(getActivity());
		pd.setMessage(st1);
		pd.setCanceledOnTouchOutside(false);
		pd.show();
		new Thread(new Runnable() {
			public void run() {
				try {
					EMContactManager.getInstance().deleteContact(tobeDeleteUser.getUsername());
					// 删除db和内存中此用户的数据
					UserDao dao = new UserDao(getActivity());
					dao.deleteContact(tobeDeleteUser.getUsername());
					SmyApplication.getSingleton().getContactList().remove(tobeDeleteUser.getUsername());
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							adapter.remove(tobeDeleteUser);
							adapter.notifyDataSetChanged();

						}
					});
				} catch (final Exception e) {
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							Toast.makeText(getActivity(), st2 + e.getMessage(), Toast.LENGTH_SHORT).show();
						}
					});

				}

			}
		}).start();

	}

	/**
	 * 把user移入到黑名单
	 * TODO 暂时不使用黑名单功能
	 */
	/*private void moveToBlacklist(final String username){
		final ProgressDialog pd = new ProgressDialog(getActivity());
		String st1 = getResources().getString(R.string.Is_moved_into_blacklist);
		final String st2 = getResources().getString(R.string.Move_into_blacklist_success);
		final String st3 = getResources().getString(R.string.Move_into_blacklist_failure);
		pd.setMessage(st1);
		pd.setCanceledOnTouchOutside(false);
		pd.show();
		new Thread(new Runnable() {
			public void run() {
				try {
					//加入到黑名单
					EMContactManager.getInstance().addUserToBlackList(username,false);
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							Toast.makeText(getActivity(), st2, 0).show();
							refreshUI();
						}
					});
				} catch (EaseMobException e) {
					e.printStackTrace();
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							Toast.makeText(getActivity(), st3, 0).show();
						}
					});
				}
			}
		}).start();
		
	}*/
	
	// 刷新ui
	public void refreshUI() {
		try {
			// 可能会在子线程中调到这方法
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					getContactList();
					adapter.notifyDataSetChanged();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取联系人列表，并过滤掉黑名单和排序
	 */
	private void getContactList() {
		contactList.clear();
		//获取本地好友列表
		Map<String, User> users = SmyApplication.getSingleton().getContactList();
		Iterator<Entry<String, User>> iterator = users.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, User> entry = iterator.next();
			if (!entry.getKey().equals(Constant.NEW_FRIENDS_USERNAME) && !entry.getKey().equals(Constant.GROUP_USERNAME)
					&& !blackList.contains(entry.getKey()))
				contactList.add(entry.getValue());
		}
		// 排序
		Collections.sort(contactList, new Comparator<User>() {
			public int compare(User lhs, User rhs) {
				return lhs.getUsername().compareTo(rhs.getUsername());
			}
		});

		// 加入"申请与通知"和"群聊"
		if(users.get(Constant.GROUP_USERNAME) != null)
		    contactList.add(0, users.get(Constant.GROUP_USERNAME));
		// 把"申请与通知"添加到首位
		if(users.get(Constant.NEW_FRIENDS_USERNAME) != null)
		    contactList.add(0, users.get(Constant.NEW_FRIENDS_USERNAME));
	}
	
	void hideSoftKeyboard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		//TODO 暂时不考虑异地登录和用户被意外删除
	   /* if(((MainActivity)getActivity()).isConflict){
	    	outState.putBoolean("isConflict", true);
	    }else if(((MainActivity)getActivity()).getCurrentAccountRemoved()){
	    	outState.putBoolean(Constant.ACCOUNT_REMOVED, true);
	    }*/
	    
	}
}

