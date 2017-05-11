package edu.ncu.safe.domain;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import edu.ncu.safe.base.bean.SystemQuickerItemInfo;
import edu.ncu.safe.util.BitmapUtil;

/**
 * Created by Mr_Yang on 2016/5/27.
 */
public class RunningApplicationInfo extends SystemQuickerItemInfo implements Parcelable{
    public static final int PROCESS_TYPE_PROCESS = 0;
    public static final int PROCESS_TYPE_SERVICE = 1;
    private int pid;
    private int uid;
    private String processName;//packName

    public RunningApplicationInfo(int pid, int uid,String processName,  Drawable icon,String appName,int type,long cacheSize) {
        super(BitmapUtil.drawableToBitmap(icon),appName,getNoteByType(type),cacheSize,true);
        this.pid = pid;
        this.uid = uid ;
        this.processName = processName;
    }

    public RunningApplicationInfo(Parcel in) {
        super(in);
        this.pid = in.readInt();
        this.uid = in.readInt();
        this.processName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest,flags);
        dest.writeInt(pid);
        dest.writeInt(uid);
        dest.writeString(processName);
    }
    public static final Creator<RunningApplicationInfo> CREATOR = new Creator<RunningApplicationInfo>() {
        @Override
        public RunningApplicationInfo createFromParcel(Parcel in) {
            return new RunningApplicationInfo(in);
        }

        @Override
        public RunningApplicationInfo[] newArray(int size) {
            return new RunningApplicationInfo[size];
        }
    };



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

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    private static String getNoteByType(int type){
        switch (type){
            case PROCESS_TYPE_PROCESS:
                return "后台进程";
            case PROCESS_TYPE_SERVICE:
                return "后台服务";
        }
        return "未知进程";
    }

}
