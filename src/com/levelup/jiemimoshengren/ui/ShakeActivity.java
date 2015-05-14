package com.levelup.jiemimoshengren.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.hardware.SensorEvent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.levelup.jiemimoshengren.R;
import com.levelup.jiemimoshengren.base.BaseShakeActivity;
import com.levelup.jiemimoshengren.config.Constant;
import com.levelup.jiemimoshengren.model.FindUser;
import com.levelup.jiemimoshengren.model.User;
import com.smy.volley.extend.EasyJsonObject;
/**摇一摇*/
public class ShakeActivity extends BaseShakeActivity {
	public final static int SECOND_SHAKE_MSG = 10;
	
	private LocationClient mLocClient;
	private BDLocation mLastLocation; //上次的位置
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what == SECOND_SHAKE_MSG){ //在收到了消息后开始摇一摇的第二次请求
				int lasttime = msg.arg1;
				//使用上次返回的时间lasttime
				//TODO 错误
				doSecondShake("uid",mLastLocation.getLatitude(),mLastLocation.getLongitude(),msg.arg1);
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState,R.layout.activity_game);
	}


	@Override
	protected void initData(){
		super.initData();
		//创建摇一摇成功的监听
		this.onShakeSuccessListener = new OnShakeSuccessListener() {
			public void onShakeSuccess(SensorEvent event) {
				//摇一摇成功，向服务器发送自己的位置等信息
				System.out.println("摇一摇成功");
				if(mLastLocation!=null){
					doFirstShake("", mLastLocation.getLatitude(), mLastLocation.getLongitude());
				}else{
					showMsgFromRes(R.string.cannot_find_location);
				}
			}
		};
		
		//创建位置监听
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(new BDLocationListener() {
			public void onReceiveLocation(BDLocation location) {
				if(location==null){
					System.err.println("得到的location为空");
					return ;
				}
				if(mLastLocation!=null){
					if(mLastLocation.getLongitude()==location.getLongitude() && mLastLocation.getLatitude()==location.getLatitude()){
						System.err.println("相同location");
						return ;
					}
				}
				mLastLocation = location;
			}
		});
		final LocationClientOption option = new LocationClientOption();
		option.setCoorType("gcj02");
		option.setScanSpan(30000);
		mLocClient.setLocOption(option);
	}
	
	@Override
	protected void initView() {
		
	}
	
	@Override
	protected void onPause(){
		if(mLocClient!=null){
			mLocClient.stop();
		}
		super.onPause();
	}
	
	@Override
	protected void onResume(){
		if(mLocClient!=null){
			mLocClient.start();
		}
		super.onResume();
	}
	
	@Override
	protected void onDestroy(){
		if(mLocClient!=null){
			mLocClient.stop();
		}
		super.onDestroy();
	}
	
	/**第一次摇一摇请求*/
	private void doFirstShake(final String uid,final double latitude,final double longitude){
		JSONObject firstShakeJson = makeFirstShakeJson(uid,latitude,longitude);
		requestQueue.add(new JsonObjectRequest(Constant.URL_SHAKE_FIRST, firstShakeJson, new Listener<JSONObject>() {
			public void onResponse(JSONObject response) {
				EasyJsonObject easyRespons = new EasyJsonObject(response);
				final int currtime = easyRespons.getInt("currtime"); //第一次请求的服务器时间
				final int nexttime = easyRespons.getInt("nexttime"); //下一次请求的时间间隔
				Message msg = handler.obtainMessage(SECOND_SHAKE_MSG, currtime, nexttime);
				handler.sendMessageDelayed(msg, nexttime*1000); //handler发送延迟消息
			}
		},this));
	}
	
	/**第二次摇一摇请求*/
	private void doSecondShake(final String uid,final double latitude,final double longitude,final int lasttime){
		JSONObject secondShakeJson = makeSecondShakeJson(uid, latitude, longitude, lasttime);
		requestQueue.add(new JsonObjectRequest(Constant.URL_SHAKE_SECOND, secondShakeJson, new Listener<JSONObject>() {
			public void onResponse(JSONObject response) {
				EasyJsonObject easyJsonObject = new EasyJsonObject(response);
				if(easyJsonObject.getBoolean("success")){ //请求成功被响应
					JSONArray findUsers = easyJsonObject.getStringAsJSONArray("msg");
					try {
						handleFindUsers(findUsers); //处理
					} catch (JSONException e) {
						e.printStackTrace();
					} 
				}else{
					showMsg(easyJsonObject.getString("error"));
				}
			}
		}, this));
	}
	
	/**处理摇一摇找到的人
	 * @throws JSONException */
	protected List<FindUser> handleFindUsers(JSONArray findUsers) throws JSONException {
		List<FindUser> users = new ArrayList<FindUser>(findUsers.length());
		for(int i=0,len=findUsers.length();i<len;++i){
			EasyJsonObject userJson = new EasyJsonObject(findUsers.getJSONObject(i));
			FindUser findUser = new FindUser();
			findUser.setUsername(userJson.getString("uid"));
			findUser.setNick(userJson.getString("nick"));
			findUser.setSign(userJson.getString("sign"));
			findUser.setImgUrl(userJson.getString("head"));
			findUser.setLatitude(userJson.getDouble("latitude"));
			findUser.setLongitude(userJson.getDouble("longitude"));
			findUser.setDistanceFromMe(userJson.getDouble("distance"));
			users.add(findUser);
		}
		return users;
	}


	/**创建第一次摇一摇的json*/
	private JSONObject makeFirstShakeJson(final String uid,final double latitude,final double longitude){
		EasyJsonObject firstShakeJson = new EasyJsonObject();
		firstShakeJson.put("uid",uid );
		firstShakeJson.put("latitude",latitude);
		firstShakeJson.put("longitude", longitude);
		return firstShakeJson;
	}
	/**创建第二次摇一摇的json*/
	private JSONObject makeSecondShakeJson(final String uid,final double latitude,double longitude,final int lasttime){
		EasyJsonObject secondShakeJson = new EasyJsonObject();
		secondShakeJson.put("uid" , uid);
		secondShakeJson.put("latitude", latitude);
		secondShakeJson.put("longitude", longitude);
		secondShakeJson.put("lasttime", lasttime);
		return secondShakeJson;
	}
}
