package edu.ncu.safe.domain;

public class UserAppInfo {
	private String appName;
	private int uid;
	public UserAppInfo(String appName, int uid) {
		super();
		this.appName = appName;
		this.uid = uid;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	
}
