package com.levelup.jiemimoshengren.model;

import android.os.Parcel;
import android.os.Parcelable;

public class FindUser extends User implements Parcelable{
	private double longitude;
	private double latitude;
	private double distanceFromMe;
	
	public FindUser(){}
	public FindUser(User user){
		this.username = user.getUsername();
		this.nick = user.getNick();
		this.sign = user.getSign();
		this.imgUrl = user.getImgUrl();
		this.isFemale = user.isFemale();
	}
	public FindUser(Parcel parcel){
		this.username = parcel.readString();
		this.nick = parcel.readString();
		this.sign = parcel.readString();
		this.isFemale = parcel.readString().equals(User.SEX_FEMALE)?true:false;
		this.sign = parcel.readString();
		this.latitude = parcel.readDouble();
		this.longitude = parcel.readDouble();
	}
	
	
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getDistanceFromMe() {
		return distanceFromMe;
	}
	public void setDistanceFromMe(double distanceFromMe) {
		this.distanceFromMe = distanceFromMe;
	}
	
	 public int describeContents() {
         return 0;
     }

     public void writeToParcel(Parcel out, int flags) {
    	 out.writeString(getUsername());
    	 out.writeString(getNick());
    	 out.writeString(getImgUrl());
    	 out.writeString(isFemale()?User.SEX_FEMALE:User.SEX_MALE);
    	 out.writeString(getSign());
    	 out.writeDouble(latitude);
         out.writeDouble(longitude);
     }

     public static final Parcelable.Creator<FindUser> CREATOR
             = new Parcelable.Creator<FindUser>() {
         public FindUser createFromParcel(Parcel in) {
             return new FindUser(in);
         }

         public FindUser[] newArray(int size) {
             return new FindUser[size];
         }
     };
}
