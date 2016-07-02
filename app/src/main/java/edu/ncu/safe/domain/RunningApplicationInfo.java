package edu.ncu.safe.domain;

import android.graphics.drawable.Drawable;

import edu.ncu.safe.myinterface.ChildItemData;

/**
 * Created by Mr_Yang on 2016/5/27.
 */
public class RunningApplicationInfo extends ChildItemData {
    public static final int PROCESS_TYPE_PRPCESS = 0;
    public static final int PROCESS_TYPE_SERVICE = 1;
    private String processName;//packName
    private String appName;
    private Drawable icon;
    private int pid;
    private int uid;
    private int memory;
    private int type = PROCESS_TYPE_PRPCESS;

    public RunningApplicationInfo() {
    }

    public RunningApplicationInfo(String processName, String appName, Drawable icon, int pid, int uid,int memory,int type) {
        this.processName = processName;
        this.appName = appName;
        this.icon = icon;
        this.pid = pid;
        this.uid = uid;
        this.memory = memory;
        this.type = type;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public Drawable getItemIcon() {
        return icon;
    }

    @Override
    public String getItemName() {
        return appName;
    }

    @Override
    public String getItemNote() {
        switch (type){
            case PROCESS_TYPE_PRPCESS:
                return "后台进程";
            case PROCESS_TYPE_SERVICE:
                return "后台服务";
        }
        return "位置进程";
    }

    @Override
    public long getItemSize() {
        return memory;
    }

}
