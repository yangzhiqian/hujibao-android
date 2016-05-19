package edu.ncu.safe.domain;

public class VersionInfo {
	private String verion;
	private String description;
	private String downloadUrl;
	
	public VersionInfo(){}
	
	public VersionInfo(String version,String description,String downloadUrl){
		this.verion = version;
		this.description = description;
		this.downloadUrl = downloadUrl;
	}
	
	public String getVerion() {
		return verion;
	}
	public void setVerion(String verion) {
		this.verion = verion;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDownloadUrl() {
		return downloadUrl;
	}
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}
}
