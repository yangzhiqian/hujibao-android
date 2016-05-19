package edu.ncu.safe.domain;

import edu.ncu.safe.R;

public class MainMenuInfo {
	
	public static final int[] re = {R.drawable.ic_launcher,R.drawable.ic_launcher,
		R.drawable.ic_launcher,R.drawable.ic_launcher,R.drawable.ic_launcher};
	public static final String[] titles={"WiFi网络环境:检查0次","安全扫码(从未使用)",
		"通讯录优化(从未使用)","骚扰号码识别(未启用)","保修查询"};
	public static final String[] anotations = {"无安全问题","网购、支付前扫一扫,防止上当","多台手机同步,优化拨号效率","",""};
	public static final boolean[] hasdirection = {false,true,true,true,true};
	
	private int imgID;
	private String title;
	private String anotation;
	private boolean hasDirection;
	public int getImgID() {
		return imgID;
	}
	public void setImgID(int imgID) {
		this.imgID = imgID;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAnotation() {
		return anotation;
	}
	public void setAnotation(String anotation) {
		this.anotation = anotation;
	}
	public boolean isHasDirection() {
		return hasDirection;
	}
	public void setHasDirection(boolean hasDirection) {
		this.hasDirection = hasDirection;
	}
}
