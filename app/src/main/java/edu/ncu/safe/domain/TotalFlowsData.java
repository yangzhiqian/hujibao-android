package edu.ncu.safe.domain;

public class TotalFlowsData {
	private int netType;
	private long update;
	private long download;
	public TotalFlowsData(int netType, long update, long download) {
		super();
		this.netType = netType;
		this.update = update;
		this.download = download;
	}
	public int getNetType() {
		return netType;
	}
	public void setNetType(int netType) {
		this.netType = netType;
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
