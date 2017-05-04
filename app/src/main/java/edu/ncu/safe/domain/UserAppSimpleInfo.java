package edu.ncu.safe.domain;

import android.graphics.drawable.Drawable;

/**
 * 简单版的用户app信息</br>
 * <p>实体信息如下：
 * <ol>
 *     <li>uid:用户软件user id</li>
 *     <li>icon :Drawable，用户软件的图标</></li>
 *     <li>packName:用户软件的包名</li>
 *     <li>appName: 用户软件的简易名称</li>
 * </ol>
 */
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
