package edu.ncu.safe.domain;

import android.graphics.drawable.Drawable;

/**
 * Created by Mr_Yang on 2016/5/25.
 */
public class UserAppNetInfo extends UserAppSimpleInfo{
    private long flows;
    private boolean isGPRS;
    private boolean isWIFI;

    public UserAppNetInfo() {
    }

    public UserAppNetInfo(int uid, Drawable icon, String packName, String appName, long flows, boolean isGPRS, boolean isWIFI) {
        super(uid, icon, packName, appName);
        this.flows = flows;
        this.isGPRS = isGPRS;
        this.isWIFI = isWIFI;
    }

    public long getFlows() {
        return flows;
    }

    public void setFlows(long flows) {
        this.flows = flows;
    }

    public boolean isGPRS() {
        return isGPRS;
    }

    public void setIsGPRS(boolean isGPRS) {
        this.isGPRS = isGPRS;
    }

    public boolean isWIFI() {
        return isWIFI;
    }

    public void setIsWIFI(boolean isWIFI) {
        this.isWIFI = isWIFI;
    }
}
