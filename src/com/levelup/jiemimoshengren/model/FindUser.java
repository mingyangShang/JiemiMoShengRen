package com.levelup.jiemimoshengren.model;

public class FindUser extends User {
	private double longitude;
	private double latitude;
	private double distanceFromMe;
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
	
	
}
