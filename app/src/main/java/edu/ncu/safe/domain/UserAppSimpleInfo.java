package edu.ncu.safe.domain;

import android.graphics.drawable.Drawable;

public class UserAppSimpleInfo {
	private int uid;
	private Drawable icon;
	private String packName;
	private String appName;

	public UserAppSimpleInfo() {
	}

	public UserAppSimpleInfo(int uid, Drawable icon, String packName, String appName) {
		this.uid = uid;
		this.icon = icon;
		this.packName = packName;
		this.appName = appName;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public String getPackName() {
		return packName;
	}

	public void setPackName(String packName) {
		this.packName = packName;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}
}
