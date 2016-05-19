package edu.ncu.safe.domain;

public class FlowsStatisticsDayItemInfo {
	private int date;
	private long update;
	private long download;
	public FlowsStatisticsDayItemInfo(){}
	public FlowsStatisticsDayItemInfo(int date, long update, long download) {
		super();
		this.date = date;
		this.update = update;
		this.download = download;
	}
	public int getDate() {
		return date;
	}
	public void setDate(int date) {
		this.date = date;
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
	
}
