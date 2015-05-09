package com.levelup.jiemimoshengren.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import com.baidu.mapapi.model.LatLng;

public class LocationTool {
	/**
	 * 得到所在位置，采用的是安卓原带api
	 * @param context
	 * @return 表示位置的Location对象
	 */
	static public Location getLocation(Context context) {
		LocationManager locMan = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		Location location = locMan
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (location == null) {
			location = locMan
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		return location;
	}

	/**
	 * 获得表示自己位置的位置对象
	 * 
	 * @param location
	 * @return 表示自己位置的位置对象
	 */
	public static LatLng getGeoByLocation(Location location) {
		LatLng gp = null;
		try {
			if (location != null) {
				double geoLatitude = location.getLatitude();
				double geoLongitude = location.getLongitude();
				gp = new  LatLng(geoLatitude, geoLongitude);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return gp;
	}

	/**
	 * 得到自己位置信息的Address对象
	 * 
	 * @param cntext
	 * @param gp
	 * @return
	 */
	/*static public Address getAddressbyGeoPoint(Context cntext, GeoPoint gp) {
		Address result = null;
		try {
			if (gp != null) {
				Geocoder gc = new Geocoder(cntext, Locale.CHINA);

				double geoLatitude = (int) gp.getLatitudeE6() / 1E6;
				double geoLongitude = (int) gp.getLongitudeE6() / 1E6;

				List<Address> lstAddress = gc.getFromLocation(geoLatitude,
						geoLongitude, 1);
				if (lstAddress.size() > 0) {
					result = lstAddress.get(0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}*/

	/**
	 * 获得当前的GeoPoint坐标
	 * @param context
	 * @return
	 */
	/*public static GeoPoint getCurrentGeo(Context context){
		 Location location = getLocation(context);
		 if(location==null){
			 return null;
		 }
		 GeoPoint point = getGeoByLocation(location);
		 if(point==null){
			 return null;
		 }
		 return point;
	}*/
	
	/**
	 * 得到当前所在城市
	 */
/*	public static String getCurrentCity(Context context){
		 Location location = getLocation(context);
		 if(location==null){
			 return "";
		 }
		 GeoPoint point = getGeoByLocation(location);
		 if(point==null){
			 return "";
		 }
		 Address address = getAddressbyGeoPoint(context, point);
		 if(address==null){
			 return "";
		 }
		 String cityname = address.getLocality();
		 if(cityname==null || TextUtils.isEmpty(cityname)){
			 return "";
		 }
		 return cityname.substring(0, cityname.length()-1);
	}*/
	
/*	*//**
	 * 标记出兴趣点
	 * @param locatable
	 * @param city
	 * @param marker
	 *//*
	public static void markPoi(Locatable locatable,long longitude,long latitude){
		locatable.showMarkable(longitude, latitude);
	}
	
	*//**
	 * 设置地图中心为当前的位置
	 * @param locatable
	 *//*
	public static void setCurrentAsCenter(Locatable locatable){
		locatable.setCurrentPosition();
	}
	
	public static void controllLocateStart(Locatable locatable){
		locatable.start();
	}
	public static void controllLocateStop(Locatable locatable){
		locatable.pause();
	}
	public static void controllLocateDestory(Locatable locatable){
		locatable.destory();
	}
	
	*//**
	 * 初始化管理地理位置信息的类
	 * @param locatable 实现了locatable的类
	 * @param context 上下文对象
	 *//*
	public static void initLocationClass(Locatable locatable,Context context){
		locatable.init(context);
	}*/
	
}
