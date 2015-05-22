package com.levelup.jiemimoshengren.ui;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.platform.comapi.map.l;
import com.levelup.jiemimoshengren.R;
import com.levelup.jiemimoshengren.base.BaiduMapActivity;
import com.levelup.jiemimoshengren.model.FindUser;

public class MapMarkerActivity extends BaiduMapActivity{

	private final static String TAG = "map";
	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	public NotifyLister mNotifyer = null;


	EditText indexText = null;
	int index = 0;
	static BDLocation lastLocation = null;
	public static MapMarkerActivity instance = null;
	ProgressDialog progressDialog;
	private BaiduMap mBaiduMap;
	
	private LocationMode mCurrentMode;
	private List<FindUser> findUsers; //找到的陌生人
	
	/**
	 * 构造广播监听类，监听 SDK key 验证以及网络异常广播
	 */
	public class BaiduSDKReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			final String s = intent.getAction();
			if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
				showMsgFromRes(R.string.please_check);
			} else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
				showMsgFromRes(R.string.Network_error);
			}
		}
	}

	private BaiduSDKReceiver mBaiduReceiver;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		//在使用SDK各组件之前初始化context信息，传入ApplicationContext  
        //注意该方法要再setContentView方法之前实现  
        SDKInitializer.initialize(getApplicationContext());  
		setContentView(R.layout.activity_map_marker);
		initView();
		initData();
		
		// 注册 SDK 广播监听者
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		mBaiduReceiver = new BaiduSDKReceiver();
		registerReceiver(mBaiduReceiver, iFilter);
	}
	
	@Override
	protected void initData() {
		super.initData();
		Intent intent = getIntent();
		findUsers = intent.getParcelableArrayListExtra("findusers");
		//仅供调试使用
		if(findUsers==null){
			findUsers = new ArrayList<FindUser>();
			FindUser user = new FindUser();
			user.setNick("nick");
			findUsers.add(user);
		}
		
		double latitude = intent.getDoubleExtra("latitude", 0);
		mCurrentMode = LocationMode.NORMAL;
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setOnMarkerClickListener(this);
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
		mBaiduMap.setMapStatus(msu);
		initMapView();
		if (latitude == 0) {
			mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
							mCurrentMode, true, null));
			showMapWithLocationClient();
		} else {
			double longtitude = intent.getDoubleExtra("longitude", 0);
			String address = intent.getStringExtra("address");
			LatLng p = new LatLng(latitude, longtitude);
			mMapView = new MapView(this,
					new BaiduMapOptions().mapStatus(new MapStatus.Builder()
							.target(p).build()));
			showMap(latitude, longtitude, address);
		}
	}

	@Override
	protected void initView() {
		super.initView();
		mMapView = (MapView) findViewById(R.id.bmapView);
	}

	private void showMap(double latitude, double longtitude, String address) {
		LatLng llA = new LatLng(latitude, longtitude);
		
		CoordinateConverter converter= new CoordinateConverter();
		converter.coord(llA);
		converter.from(CoordinateConverter.CoordType.COMMON);
		LatLng convertLatLng = converter.convert();
		OverlayOptions ooA = new MarkerOptions().position(convertLatLng).icon(BitmapDescriptorFactory
				.fromResource(R.drawable.icon_marka))
				.zIndex(4).draggable(true);
		mBaiduMap.addOverlay(ooA);
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(convertLatLng, 17.0f);
		mBaiduMap.animateMapStatus(u);
	}

	private void showMapWithLocationClient() {
		progressDialog = makeProgressDialog(this, getString(R.string.Making_sure_your_location), new OnCancelListener() {
			public void onCancel(DialogInterface arg0) {
				if (progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				Log.d("map", "cancel retrieve location");
				finish();
			}
		});
		progressDialog.show();

		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);

		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("gcj02");
		option.setScanSpan(30000);
		mLocClient.setLocOption(option);
	}

	@Override
	protected void onPause() {
		if (mLocClient != null) {
			mLocClient.stop();
		}
		super.onPause();
		lastLocation = null;
	}

	@Override
	protected void onResume() {
		if (mLocClient != null) {
			mLocClient.start();
		}
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		if (mLocClient != null)
			mLocClient.stop();
		unregisterReceiver(mBaiduReceiver);
		super.onDestroy();
	}
	private void initMapView() {
		mMapView.setLongClickable(true);
	}

	/**
	 * 监听函数，有新位置的时候，格式化成字符串，输出到屏幕中
	 */
	public class MyLocationListenner implements BDLocationListener {
		public void onReceiveLocation(BDLocation location) {
			if (location == null) {
				return;
			}
			Log.d("map", "On location change received:" + location);
			Log.d("map", "addr:" + location.getAddrStr());
			if (progressDialog != null) {
				progressDialog.dismiss();
			}

			if (lastLocation != null) {
				if (lastLocation.getLatitude() == location.getLatitude() && lastLocation.getLongitude() == location.getLongitude()) {
					Log.d("map", "same location, skip refresh");
					return;
				}
			}
			lastLocation = location;
			mBaiduMap.clear();
			LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());

			//添加自己的标记
			addMeMarker(latLng);
			//添加陌生人标记
			for(int i=0,len=findUsers.size();i<len;++i){
				FindUser user = findUsers.get(i);
				Bundle extra = new Bundle();
				extra.putInt("pos", i);
				addTextImgMarler(new LatLng(user.getLatitude(), user.getLongitude()), user.getNick(), extra);
//				addTextImgMarler(latLng, user.getNick(), extra);
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}
	}

	public class NotifyLister extends BDNotifyListener {
		public void onNotify(BDLocation mlocation, float distance) {
		}
	}

	/**发送自己的位置，暂时不用*/
	public void sendLocation(View view) {
		Intent intent = this.getIntent();
		intent.putExtra("latitude", lastLocation.getLatitude());
		intent.putExtra("longitude", lastLocation.getLongitude());
		intent.putExtra("address", lastLocation.getAddrStr());
		this.setResult(RESULT_OK, intent);
		finish();
		overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
	}

	public boolean onMarkerClick(Marker arg0) {
		Bundle extra = arg0.getExtraInfo();
		int pos = 0;
		if(extra==null){
			pos = 0;
		}else{
			pos = extra.getInt("pos");
		}
		FindUser findUser = findUsers.get(pos);
		if(findUser!=null){
			Intent intent = new Intent(this,GameActivity.class);
			intent.putExtra("finduser", findUser);
			startActivity(intent);
		}
		return false;
	}
}
