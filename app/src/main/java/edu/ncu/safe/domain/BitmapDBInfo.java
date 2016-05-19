package edu.ncu.safe.domain;

import java.sql.Date;

public class BitmapDBInfo {
	private String uri;
	private String path;
	private Date date;
	
	public BitmapDBInfo(){}
	public BitmapDBInfo(String uri,String path,Date date){
		this.setUri(uri);
		this.setPath(path);
		this.setDate(date);
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	
	
}
