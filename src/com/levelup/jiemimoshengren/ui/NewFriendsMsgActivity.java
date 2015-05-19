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

import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.levelup.jiemimoshengren.R;
import com.levelup.jiemimoshengren.adapter.NewFriendsMsgAdapter;
import com.levelup.jiemimoshengren.base.BaseActivity;
import com.levelup.jiemimoshengren.base.SmyApplication;
import com.levelup.jiemimoshengren.config.Constant;
import com.levelup.jiemimoshengren.db.InviteMessgeDao;
import com.levelup.jiemimoshengren.model.InviteMessage;

/**
 * 申请与通知
 */
public class NewFriendsMsgActivity extends BaseActivity {
	private ListView listView;
	private NewFriendsMsgAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState,R.layout.activity_new_friends_msg);

		listView = (ListView) findViewById(R.id.list);
		InviteMessgeDao dao = new InviteMessgeDao(this);
		List<InviteMessage> msgs = dao.getMessagesList();
		//设置adapter
		adapter = new NewFriendsMsgAdapter(this, 1, msgs); 
		listView.setAdapter(adapter);
		SmyApplication.getSingleton().getContacts().get(Constant.NEW_FRIENDS_USERNAME).setUnreadMsgCount(0);
		
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		
	}
	
	
}
