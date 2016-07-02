package edu.ncu.safe.domain;

import android.graphics.drawable.Drawable;

import edu.ncu.safe.myinterface.ChildItemData;

public class CacheInfo extends ChildItemData {

    private long mCacheSize;
    private String mPackageName, mApplicationName;
    private Drawable mIcon;

    public CacheInfo(String packageName, String applicationName, Drawable icon, long cacheSize) {
        mCacheSize = cacheSize;
        mPackageName = packageName;
        mApplicationName = applicationName;
        mIcon = icon;
    }

    public Drawable getApplicationIcon() {
        return mIcon;
    }

    public String getApplicationName() {
        return mApplicationName;
    }

    public long getCacheSize() {
        return mCacheSize;
    }

    public String getPackageName() {
        return mPackageName;
    }

    @Override
    public Drawable getItemIcon() {
        return mIcon;
    }

    @Override
    public String getItemName() {
        return mApplicationName;
    }

    @Override
    public String getItemNote() {
        return "note";
    }
    @Override
    public long getItemSize() {
        return mCacheSize;
    }
}
