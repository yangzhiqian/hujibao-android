package edu.ncu.safe.domain;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import edu.ncu.safe.base.bean.SystemQuickerItemInfo;
import edu.ncu.safe.util.BitmapUtil;

public class CacheInfo extends SystemQuickerItemInfo implements Parcelable{
    private String packageName;
    public CacheInfo(Parcel in) {
        super(in);
        this.packageName = in.readString();
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest,flags);
        dest.writeString(packageName);
    }
    public static final Creator<CacheInfo> CREATOR = new Creator<CacheInfo>() {
        @Override
        public CacheInfo createFromParcel(Parcel in) {
            return new CacheInfo(in);
        }

        @Override
        public CacheInfo[] newArray(int size) {
            return new CacheInfo[size];
        }
    };


    public CacheInfo(String packageName, Drawable icon, String applicationName, String note, long cacheSize) {
        super(BitmapUtil.drawableToBitmap(icon),applicationName,note,cacheSize,true);
        this.packageName = packageName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
