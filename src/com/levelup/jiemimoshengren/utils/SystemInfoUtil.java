package com.levelup.jiemimoshengren.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.util.Log;

import com.levelup.jiemimoshengren.log.SmyRuntimeException;

public class SystemInfoUtil {

	/** the unit of memory size */
	public  static enum SizeUnit {
		B,KB,MB;
	}
	
	/**
	 * 判断是否有sd卡
	 * @return true有，false无
	 */
	public static boolean isSDExists() {
		return android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}

	/**
	 * 取得sd卡总空间
	 * @return sd卡总空间(MB)
	 */
	public static long getSDAllSize(SizeUnit unit) {
		if (isSDExists()) {
			File path = Environment.getExternalStorageDirectory();
			StatFs sf = new StatFs(path.getPath());
			// 获取单个数据块的大小(Byte)
			long blockSize = sf.getBlockSize();
			// 获取所有数据块数
			long allBlocks = sf.getBlockCount();
			// return (allBlocks * blockSize)/1024; //单位KB
			return numberToSize(allBlocks*blockSize/1024, unit);
		} else {
			return 0L;
		}
	}

	/**
	 * 取得sd卡可用空间
	 * @return sd卡可用空间(MB)
	 */
	public static long getSDAvailableSize(SizeUnit unit) {
		if (isSDExists()) {
			File path = Environment.getExternalStorageDirectory();
			StatFs sf = new StatFs(path.getPath());
			// 获取单个数据块的大小(Byte)
			long blockSize = sf.getBlockSize();
			// 空闲的数据块的数量
			long freeBlocks = sf.getAvailableBlocks();
			// return freeBlocks * blockSize; //单位Byte
			// return (freeBlocks * blockSize)/1024; //单位KB
			return numberToSize(freeBlocks*blockSize/1024, unit);
		} else {
			return 0L;
		}
	}

	/**
	 * 获取手机内存大小
	 * @param application Application of this app
	 * @return 
	 */
	public static long getTotalMemory(Application application,SizeUnit unit) {
		String str1 = "/proc/meminfo";// 系统内存信息文件
		String str2;
		String[] arrayOfString;
		long initial_memory = 0;
		try {
			FileReader localFileReader = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(
					localFileReader, 8192);
			str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小

			arrayOfString = str2.split("\\s+");
			for (String num : arrayOfString) {
				Log.i(str2, num + "\t");
			}

			initial_memory = Integer.valueOf(arrayOfString[1]).intValue();// 获得系统总内存，单位是KB
			localBufferedReader.close();
		} catch (IOException e) {}
		return numberToSize(initial_memory, unit);
	}

	/**
	 * 获取当前可用内存大小
	 * @param application Application of this app
	 * @return
	 */
	public static long getAvailMemory(Application application,SizeUnit unit) {
		ActivityManager am = (ActivityManager) application
				.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo mi = new MemoryInfo();
		am.getMemoryInfo(mi);
		return numberToSize(mi.availMem/1024, unit);
	}
	
	/**
	 * @return the max memory size for the app
	 */
	public static long getAppMaxMemory(SizeUnit unit){
		return numberToSize(Runtime.getRuntime().maxMemory()/1024, unit);
	}
	
	/**
	 * @return the avaliable memory size for the app
	 */
	public static long getAppTotalMemory(SizeUnit unit){
		return numberToSize(Runtime.getRuntime().totalMemory()/1024, unit);
	}
	
	/**
	 * @return the current total memory size for the app
	 */
	public static long getAppAvailMemory(SizeUnit unit){
	     //应用程序已获得内存中未使用内存  
		return numberToSize(Runtime.getRuntime().freeMemory()/1024, unit);
	}
	
	/**
	 * @param number number by KB
	 * @param unit unit to transfrom 
	 * @return the size for specified unit
	 */
	public static long numberToSize(long number,SizeUnit unit){
		switch(unit){
		case B:
			System.out.println("case B");
			return number*1024;
		case KB:
			return number;
		case MB:
			return number/1024;
		default:
			throw new SmyRuntimeException("the unit can only in B,KB,MB");
		}
	}
	

	/**
	 * 获取系统存储相片的Uri
	 * 
	 * @return 系统存储相片的Uri
	 */
	public static Uri getDCIMUri() {
		return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	}
	
}
