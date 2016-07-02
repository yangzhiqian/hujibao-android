package edu.ncu.safe.domain;

import android.graphics.drawable.Drawable;

/**
 * Created by Mr_Yang on 2016/5/21.
 */
public class UserAppBaseInfo extends UserAppSimpleInfo{
    private boolean isPrivacy = false;
    private boolean isCost = false;
    private boolean isLocation = false;
    private boolean isWifi = false;

    public boolean isRunning() {
        return isRunning;
    }

    public void setIsRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    private boolean isRunning = false;
    private long runMemory = 0;
    private boolean isCheck = false;

    public UserAppBaseInfo() {
    }

    public UserAppBaseInfo(boolean isPrivacy, boolean isCost, boolean isLocation, boolean isWifi,boolean isRunning, long runMemory, boolean isCheck) {
        this.isPrivacy = isPrivacy;
        this.isCost = isCost;
        this.isLocation = isLocation;
        this.isWifi = isWifi;
        this.isRunning = isRunning;
        this.runMemory = runMemory;
        this.isCheck = isCheck;
    }

    public UserAppBaseInfo(int uid, Drawable icon, String packName, String appName, boolean isPrivacy, boolean isCost, boolean isLocation, boolean isWifi,boolean isRunning, long runMemory, boolean isCheck) {
        super(uid, icon, packName, appName);
        this.isPrivacy = isPrivacy;
        this.isCost = isCost;
        this.isLocation = isLocation;
        this.isWifi = isWifi;
        this.isRunning = isRunning;
        this.runMemory = runMemory;
        this.isCheck = isCheck;
    }

    public boolean isPrivacy() {
        return isPrivacy;
    }

    public void setIsPrivacy(boolean isPrivacy) {
        this.isPrivacy = isPrivacy;
    }

    public boolean isCost() {
        return isCost;
    }

    public void setIsCost(boolean isCost) {
        this.isCost = isCost;
    }

    public boolean isLocation() {
        return isLocation;
    }

    public void setIsLocation(boolean isLocation) {
        this.isLocation = isLocation;
    }

    public boolean isWifi() {
        return isWifi;
    }

    public void setIsWifi(boolean isWifi) {
        this.isWifi = isWifi;
    }

    public long getRunMemory() {
        return runMemory;
    }

    public void setRunMemory(long runMemory) {
        this.runMemory = runMemory;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setIsCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }
}
