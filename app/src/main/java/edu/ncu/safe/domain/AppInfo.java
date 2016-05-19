package edu.ncu.safe.domain;

import android.content.pm.PermissionInfo;
import android.graphics.drawable.Drawable;

public class AppInfo {
	private int uid;
	private String packName;
	private String appName;
	private Drawable icon;
	private boolean isSystemApp;
	private PermissionInfo[] permissionInfos;
	public AppInfo(int uid, String packName, String appName, Drawable icon,
			PermissionInfo[] permissionInfos,boolean isSystemApp) {
		super();
		this.uid = uid;
		this.packName = packName;
		this.appName = appName;
		this.icon = icon;
		this.permissionInfos = permissionInfos;
		this.isSystemApp = isSystemApp;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
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
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public PermissionInfo[] getPermissionInfos() {
		return permissionInfos;
	}
	public void setPermissionInfos(PermissionInfo[] permissionInfos) {
		this.permissionInfos = permissionInfos;
	}
	public boolean isSystemApp() {
		return isSystemApp;
	}
	public void setSystemApp(boolean isSystemApp) {
		this.isSystemApp = isSystemApp;
	}
}
