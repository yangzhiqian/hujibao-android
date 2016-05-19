package edu.ncu.safe.domain;

public class WhiteBlackNumberInfo {
	private String number;
	private String note;
	private boolean isSms;
	private boolean isPhoneCall;
	public WhiteBlackNumberInfo(String number, String note, boolean isSms,
			boolean isPhoneCall) {
		super();
		this.number = number;
		this.note = note;
		this.isSms = isSms;
		this.isPhoneCall = isPhoneCall;
	}
	public WhiteBlackNumberInfo() {
		// TODO Auto-generated constructor stub
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public boolean isSms() {
		return isSms;
	}
	public void setSms(boolean isSms) {
		this.isSms = isSms;
	}
	public boolean isPhoneCall() {
		return isPhoneCall;
	}
	public void setPhoneCall(boolean isPhoneCall) {
		this.isPhoneCall = isPhoneCall;
	}
	
}
