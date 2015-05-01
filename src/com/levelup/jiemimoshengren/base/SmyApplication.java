package com.levelup.jiemimoshengren.base;

import android.app.Application;
import android.graphics.Bitmap;

import com.levelup.jiemimoshengren.R;
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
public class SmyApplication extends Application{
    private static SmyApplication singleton;
    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        init();
    }

    //初始化配置
    private void init() {
        initBaiduMap();
        initImageLoader();
    }

    //配置百度地图
    private void initBaiduMap() {
    	//模拟器不支持。暂时不使用
        //SDKInitializer.initialize(this);
    }
    
    //配置ImageLoader
    private void initImageLoader(){
    	//初始化图片加载库
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
			.cacheInMemory()
			.displayer(new FadeInBitmapDisplayer(50))
			.bitmapConfig(Bitmap.Config.RGB_565)
			.imageScaleType(ImageScaleType.EXACTLY)
//			.showImageForEmptyUri(R.drawable.deathnote)
//			.showImageOnFail(R.drawable.deathnote)
			.build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
			.memoryCache(new UsingFreqLimitedMemoryCache((int) SystemInfoUtil.getAppMaxMemory(SizeUnit.KB)))
			.defaultDisplayImageOptions(defaultOptions)
			.build();
		ImageLoader.getInstance().init(config);
    }


    public static Application getSingleton(){
        return singleton;
    }


}
