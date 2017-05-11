package edu.ncu.safe.base.bean;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Mr_Yang on 2016/5/28.<br/>
 * 手机加速中expandablelistview的二级控件bean抽象
 */
public class SystemQuickerItemInfo implements Parcelable{
    protected Bitmap icon;
    protected String title;
    protected String note;
    protected long cacheSize;
    protected boolean isChecked = true;

    public SystemQuickerItemInfo(Bitmap icon, String title, String note, long cacheSize, boolean isChecked) {
        this.icon = icon;
        this.title = title;
        this.note = note;
        this.cacheSize = cacheSize;
        this.isChecked = isChecked;
    }

    protected SystemQuickerItemInfo(Parcel in) {
        icon = in.readParcelable(Bitmap.class.getClassLoader());
        title = in.readString();
        note = in.readString();
        cacheSize = in.readLong();
        isChecked = in.readByte() != 0;
    }

    public static final Creator<SystemQuickerItemInfo> CREATOR = new Creator<SystemQuickerItemInfo>() {
        @Override
        public SystemQuickerItemInfo createFromParcel(Parcel in) {
            return new SystemQuickerItemInfo(in);
        }

        @Override
        public SystemQuickerItemInfo[] newArray(int size) {
            return new SystemQuickerItemInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(icon, flags);
        dest.writeString(title);
        dest.writeString(note);
        dest.writeLong(cacheSize);
        dest.writeByte((byte) (isChecked ? 1 : 0));
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(long cacheSize) {
        this.cacheSize = cacheSize;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
