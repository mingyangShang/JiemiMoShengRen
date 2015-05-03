package com.levelup.jiemimoshengren.log;

import android.util.Log;


/**自定义日志记录器*/
public class SmyLogger {
	public static final String TAG = "SmyLogger";
	public static void e(String errorMsg){
		Log.e(TAG, errorMsg);
	}

}
