package com.levelup.jiemimoshengren.base;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;

public class DefaultActivity extends BaseActivity implements ErrorListener {

	protected SmyApplication application;
	protected RequestQueue requestQueue;

	@Override
	protected void initData() {
		application = SmyApplication.getSingleton();
		requestQueue = application.getRequestQueue();
	}

	@Override
	protected void initView() {

	}

	/** 处理volley请求错误 */
	public void onErrorResponse(VolleyError error) {
		showMsg(error.getMessage());
	}

	
}
