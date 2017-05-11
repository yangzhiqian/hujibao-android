package edu.ncu.safe.domain;

import android.graphics.drawable.Drawable;

import java.util.List;

import edu.ncu.safe.base.bean.UserAppSimpleInfo;

public class UserAppInfo extends UserAppSimpleInfo {
	private List<PermissionInfo> permissionInfos ;
	public UserAppInfo(int uid, Drawable icon, String packName, String appName, List<PermissionInfo> permissionInfos) {
		super(uid, icon, packName, appName);
		this.permissionInfos = permissionInfos;
	}

	public List<PermissionInfo> getPermissionInfos() {
		return permissionInfos;
	}
	public void setPermissionInfos(List<PermissionInfo> permissionInfos) {
		this.permissionInfos = permissionInfos;
	}

	/**
	 * 获取app的权限名称
	 * @return
     */
	public String[] getArrayPermission(){
		String[] perms = new String[permissionInfos.size()];
		for(int i=0;i<perms.length;i++){
			perms[i] = permissionInfos.get(i).getPermissionName();
		}
		return perms;
	}


	public static class PermissionInfo{
		private String permissionName;
		private String peimissionLabel;
		private String permissionDescription;
		private String groupLabel;
		private boolean flag;

		public PermissionInfo() {
		}

		public PermissionInfo(String permissionName, String peimissionLabel, String permissionDescription, String groupLabel, boolean flag) {
			this.permissionName = permissionName;
			this.peimissionLabel = peimissionLabel;
			this.permissionDescription = permissionDescription;
			this.groupLabel = groupLabel;
			this.flag = flag;
		}

		public String getPermissionName() {
			return permissionName;
		}

		public void setPermissionName(String permissionName) {
			this.permissionName = permissionName;
		}

		public String getPeimissionLabel() {
			return peimissionLabel;
		}

		public void setPeimissionLabel(String peimissionLabel) {
			this.peimissionLabel = peimissionLabel;
		}

		public String getPermissionDescription() {
			return permissionDescription;
		}

		public void setPermissionDescription(String permissionDescription) {
			this.permissionDescription = permissionDescription;
		}
		public String getGroupLabel() {
			return groupLabel;
		}

		public void setGroupLabel(String groupLabel) {
			this.groupLabel = groupLabel;
		}

		public boolean isFlag() {
			return flag;
		}

		public void setFlag(boolean flag) {
			this.flag = flag;
		}
	}
}
