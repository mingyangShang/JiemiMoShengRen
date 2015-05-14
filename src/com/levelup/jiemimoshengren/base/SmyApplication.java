package com.levelup.jiemimoshengren.base;

import java.util.Map;

import android.app.Application;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.graphics.Bitmap;

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

	// 登录的用户名
	private final String PREF_USERNAME = "username";

	private User me; // 当前用户

	public static final SmyHXSDKHelper hxSdkHelper = new SmyHXSDKHelper();
	private RequestQueue requestQueue; // 网络请求队列

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

	/**
	 * 获取内存中好友user list
	 */
	public Map<String, User> getContactList() {
		return hxSdkHelper.getContactList();
	}

	/**
	 * 设置好友user list到内存中
	 */
	public void setContactList(Map<String, User> contactList) {
		hxSdkHelper.setContactList(contactList);
	}

	/**
	 * 获取当前登陆用户名
	 */
	public String getUserName() {
		return hxSdkHelper.getHXId();
	}

	/**
	 * 获取密码
	 */
	public String getPassword() {
		return hxSdkHelper.getPassword();
	}

	/**
	 * 设置用户名
	 */
	public void setUserName(String username) {
		hxSdkHelper.setHXId(username);
	}

	/**
	 * 设置密码 下面的实例代码 只是demo，实际的应用中需要加password 加密后存入 preference 环信sdk
	 * 内部的自动登录需要的密码，已经加密存储了
	 */
	public void setPassword(String pwd) {
		hxSdkHelper.setPassword(pwd);
	}

	public User getMe() {
		return me;
	}

	public void setMe(User me) {
		this.me = me;
	}
}
