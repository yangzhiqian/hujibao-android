package edu.ncu.safe.domain;

/**
 * Created by Mr_Yang on 2016/5/17.
 */
public class InterceptionInfo{
    private int id;
    private String name;
    private String number;
    private long interceptionTime;
    private String messageBody;
    private int numberType;

    public InterceptionInfo() {
    }
    public InterceptionInfo(int id, String name, String number, long interceptionTime, String messageBody, int numberType) {

        this.id = id;
        this.name = name;
        this.number = number;
        this.interceptionTime = interceptionTime;
        this.messageBody = messageBody;
        this.numberType = numberType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public long getInterceptionTime() {
        return interceptionTime;
    }

    public void setInterceptionTime(long interceptionTime) {
        this.interceptionTime = interceptionTime;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public int getNumberType() {
        return numberType;
    }

    public void setNumberType(int numberType) {
        this.numberType = numberType;
    }

    public SmsInfo toSmsInfo(){
        return new SmsInfo(number,interceptionTime,SmsInfo.IN,messageBody);
    }
}
