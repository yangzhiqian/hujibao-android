package edu.ncu.safe.domain;

import edu.ncu.safe.R;

public class MainMenuInfo {
	
	public static final int[] re = {
		R.drawable.share,R.drawable.appicon,R.drawable.appicon,R.drawable.appicon,R.drawable.appicon,R.drawable.appicon};
	public static final String[] titles={"代码分享","敬请期待","敬请期待","敬请期待","敬请期待","敬请期待"};
	public static final String[] anotations = {"","敬请期待","敬请期待","敬请期待","敬请期待","敬请期待"};
	public static final boolean[] hasdirection = {true,true,true,true,true,true};
	
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
