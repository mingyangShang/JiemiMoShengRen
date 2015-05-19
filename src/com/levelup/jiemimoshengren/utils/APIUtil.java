package com.levelup.jiemimoshengren.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.easemob.chat.EMChatManager;
import com.levelup.jiemimoshengren.config.Constant;
import com.levelup.jiemimoshengren.model.User;
import com.levelup.jiemimoshengren.ui.LoginActivity;
import com.levelup.jiemimoshengren.ui.MainActivity;
import com.smy.volley.extend.EasyJsonObject;


/**网络请求封装*/
public class APIUtil {
	/*public static User queryUser(RequestQueue requestQueue,ErrorListener errorListener,final String uid){
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
					// 进入主页面
					goToWithFinish(MainActivity.class);
				}else{
					showMsg(contactsJson.getString("error"));
				}
			}
		}, errorListener));
	}*/

}
