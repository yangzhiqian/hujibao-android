package edu.ncu.safe.domain;

public class FlowsStatisticsAppItemInfo {
	private int uid;
	private String appName;
	private long update;
	private long download;
	public FlowsStatisticsAppItemInfo(){
		
	}
	public FlowsStatisticsAppItemInfo(int uid,String appName, long update,
			long download) {
		this.uid = uid;
		this.appName = appName;
		this.update = update;
		this.download = download;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public long getUpdate() {
		return update;
	}
	public void setUpdate(long update) {
		this.update = update;
	}
	public long getDownload() {
		return download;
	}
	public void setDownload(long download) {
		this.download = download;
	}

	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	

}
