package com.levelup.jiemimoshengren.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import android.app.backup.FileBackupHelper;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Base64;

/** 对文件的相关操作 */
public class FileUtil {
	/**向服务器上传图片*/
	public static boolean uploadFile(final File file, final String RequestURL) {
		final int TIME_OUT = 10 * 10000000; //超时时间
		String BOUNDARY = UUID.randomUUID().toString(); 
		String PREFIX = "--", LINE_END = "\r\n";
		String CONTENT_TYPE = "multipart/form-data"; 
		try {
			URL url = new URL(RequestURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			conn.setDoInput(true); 
			conn.setDoOutput(true); 
			conn.setUseCaches(false); 
			conn.setRequestMethod("POST"); 
			conn.setRequestProperty("Charset", "utf-8");
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="+ BOUNDARY);
			if (file != null) {
				OutputStream outputSteam = conn.getOutputStream();
				DataOutputStream dos = new DataOutputStream(outputSteam);
				StringBuffer sb = new StringBuffer();
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINE_END);
				sb.append("Content-Disposition:form-data; name=\"icon\"; filename=\""
						+ file.getName() + "\"" + LINE_END);
				sb.append("Content-Type:image/png; charset=" + "utf-8"
						+ LINE_END);
				sb.append(LINE_END);
				dos.write(sb.toString().getBytes());
				InputStream is = new FileInputStream(file);
				byte[] bytes = new byte[1024];
				int len = 0;
				while ((len = is.read(bytes)) != -1) {
					dos.write(bytes, 0, len);
				}
				is.close();
				dos.write(LINE_END.getBytes());
				byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
						.getBytes();
				dos.write(end_data);
				dos.flush();
				int res = conn.getResponseCode();
				if (res == 200) {
					InputStream input = conn.getInputStream();
					StringBuffer sb1 = new StringBuffer();
					int ss;
					while ((ss = input.read()) != -1) {
						sb1.append((char) ss);
					}
					return true;
				} else {
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**上传文件*/
	public static boolean uploadFile(final String filename,final String uploadUrl){
		File file =  new File(filename);
		if(!file.exists()){ //文件不存在，抛出异常
			throw new IllegalArgumentException("上传的文件不存在");
		}
		return uploadFile(file, uploadUrl);
	}
	public static boolean uploadFile(final String path,final String filename,final String uploadUrl){
		File file = new File(path, filename);
		if(!file.exists()){
			throw new IllegalArgumentException("上传的文件不存在:"+file.getAbsolutePath());
		}
		return uploadFile(file, uploadUrl);
	}
	
	/**Base64压缩图片*/
	public static String base64EncodeImg(Bitmap bmp){
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 40, bos); //参数100表示不压缩
		byte[] bytes = bos.toByteArray();
		return Base64.encodeToString(bytes, Base64.DEFAULT);
	}

}
