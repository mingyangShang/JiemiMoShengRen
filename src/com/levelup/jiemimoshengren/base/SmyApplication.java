package com.levelup.jiemimoshengren.base;

import java.util.Map;

import android.app.Application;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.TextureView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.easemob.EMCallBack;
import com.easemob.media.IGxStatusCallback;
import com.easemod.chat.SmyHXSDKHelper;
import com.levelup.jiemimoshengren.model.User;
import com.levelup.jiemimoshengren.utils.SystemInfoUtil;
import com.levelup.jiemimoshengren.utils.SystemInfoUtil.SizeUnit;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

/**
 * Created by smy on 2015/4/28.
 */
public class SmyApplication extends Application {
	private static SmyApplication singleton; // Application单例

	private User me; // 当前用户

	public static final SmyHXSDKHelper hxSdkHelper = new SmyHXSDKHelper();
	private RequestQueue requestQueue; // 网络请求队列
	
	private Map<String, User> contacts; //联系人列表

	@Override
	public void onCreate() {
		super.onCreate();
		singleton = this;
		init();
	}

	// 初始化配置
	private void init() {
		initVolley();
		initBaiduMap();
		initImageLoader();
		initHuanXinSdk();
	}

	// 配置volley
	private void initVolley() {
		this.requestQueue = Volley.newRequestQueue(this);
	}

	// 配置百度地图
	private void initBaiduMap() {
		// 模拟器不支持。暂时不使用
		// SDKInitializer.initialize(this);
	}

	// 配置ImageLoader
	private void initImageLoader() {
		// 初始化图片加载库
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheInMemory().displayer(new FadeInBitmapDisplayer(50))
				.bitmapConfig(Bitmap.Config.RGB_565)
				.imageScaleType(ImageScaleType.EXACTLY)
				// .showImageForEmptyUri(R.drawable.deathnote)
				// .showImageOnFail(R.drawable.deathnote)
				.build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this)
				.memoryCache(
						new UsingFreqLimitedMemoryCache((int) SystemInfoUtil
								.getAppMaxMemory(SizeUnit.KB)))
				.defaultDisplayImageOptions(defaultOptions).build();
		ImageLoader.getInstance().init(config);
	}

	// 配置环信SDK
	private void initHuanXinSdk() {
		hxSdkHelper.onInit(singleton);
	}

	/**
	 * 退出登录,清空数据
	 */
	public void logout(final EMCallBack emCallBack) {
		// 先调用sdk logout，在清理app中自己的数据
		hxSdkHelper.logout(emCallBack);
	}

	public static SmyApplication getSingleton() {
		return singleton;
	}

	/** 返回volley的请求requestQueue */
	public RequestQueue getRequestQueue() {
		if (this.requestQueue == null) {
			initVolley();
		}
		return this.requestQueue;
	}
	
	/**联系人列表的hxSdkHelper的存取实现*/
	/*public Map<String, User> getContactList() {
		return hxSdkHelper.getContactList();
	}
	public void setContactList(Map<String, User> contactList) {
		hxSdkHelper.setContactList(contactList);
	}*/

	public User getMe() {
		if(me!=null && me.getUsername()!=null && TextUtils.isEmpty(me.getUsername())){
			return me;
		}else{
			me =  hxSdkHelper.getMe();
			return me;
		}
	}

	public void setMe(User me) {
		this.me = me;
		hxSdkHelper.setMe(me);
	}

	public Map<String, User> getContacts() {
		if(contacts==null){
			contacts =  hxSdkHelper.getContactList();
		}
		return contacts;
	}

	public void setContacts(Map<String, User> contacts) {
		this.contacts = contacts;
		hxSdkHelper.setContactList(contacts);
	}
	
}
