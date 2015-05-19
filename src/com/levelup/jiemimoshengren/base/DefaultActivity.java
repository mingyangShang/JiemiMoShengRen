package com.levelup.jiemimoshengren.base;

import android.text.TextUtils;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.easemod.chat.SmyHXSDKHelper;
import com.levelup.jiemimoshengren.model.User;

public class DefaultActivity extends BaseActivity implements ErrorListener {

	protected SmyApplication application;
	protected RequestQueue requestQueue;
	protected User me;

	@Override
	protected void initData() {
		application = SmyApplication.getSingleton();
		requestQueue = application.getRequestQueue();
		me= SmyApplication.getSingleton().getMe();
	}

	@Override
	protected void initView() {

	}

	/** 处理volley请求错误 */
	public void onErrorResponse(VolleyError error) {
		showMsg(error.getMessage());
	}
	
	/**判断当前user是否可以使用*/
	protected boolean meNotNull(){
		return me!=null && me.getUsername()!=null && !TextUtils.isEmpty(me.getUsername());
	}
}
