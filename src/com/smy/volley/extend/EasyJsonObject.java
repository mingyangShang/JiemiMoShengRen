package com.smy.volley.extend;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**自定义JsonObject，主要是为了不再使用JsonObject的时候都捕捉异常*/
public class EasyJsonObject extends JSONObject {
	public JSONObject jsonObject;
	public EasyJsonObject(JSONObject json){
		if(json==null){
			throw new IllegalArgumentException("the json parameter can not be null");
		}
		this.jsonObject = json;
	}
	public EasyJsonObject(){
		this.jsonObject = new JSONObject();
	}
	
	public String getString(final String key){
		try {
			return this.jsonObject.getString(key);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	public boolean getBoolean(final String key){
		try {
			return this.jsonObject.getBoolean(key);
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
	}
	public int getInt(final String key){
		try {
			return this.jsonObject.getInt(key);
		} catch (JSONException e) {
			e.printStackTrace();
			return -1;
		}
	}
	public double getDouble(final String key){
		try {
			return this.jsonObject.getDouble(key);
		} catch (JSONException e) {
			e.printStackTrace();
			return -1.0;
		}
	}
	public EasyJsonObject getStringAsJSONObject(final String key){
		try {
			return new EasyJsonObject(new JSONObject(jsonObject.getString(key)));
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	public JSONArray getStringAsJSONArray(final String key){
		try {
			return new JSONArray(jsonObject.getString(key));
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public EasyJsonObject put(final String key,final String value){
		try {
			this.jsonObject.put(key, value);
			return this;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	public EasyJsonObject put(final String key,final double value){
		try {
			this.jsonObject.put(key, value);
			return this;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	public EasyJsonObject put(final String key,final boolean value){
		try {
			this.jsonObject.put(key, value);
			return this;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	public EasyJsonObject put(final String key,final int value){
		try {
			this.jsonObject.put(key, value);
			return this;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public String toString(){
		return this.jsonObject.toString();
	}

}
