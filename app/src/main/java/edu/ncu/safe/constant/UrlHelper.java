package edu.ncu.safe.constant;

import android.content.ContentValues;
import android.content.Context;

import edu.ncu.safe.R;

/**
 * Created by Yang on 2016/11/1.
 */

public class UrlHelper {
    public static String getLoginUrl(Context context) {
        return context.getString(R.string.login);
    }

    public static String getRegistUrl(Context context) {
        return context.getString(R.string.regist);
    }

    public static String getLogoutUrl(Context context) {
        return context.getString(R.string.logout);
    }

    public static String getLoadIconUrl(Context context) {
        return context.getString(R.string.loadicon);
    }

    public static String getLoadBackUpUrl(Context context) {
        return context.getString(R.string.loadbackup);
    }

    public static String getLoadImgUrl(Context context) {
        return context.getString(R.string.loadimg);
    }

    public static String getStoreImgUrl(Context context) {
        return context.getString(R.string.storeimg);
    }

    public static String getStoreBackUpUrl(Context context) {
        return context.getString(R.string.storebackup);
    }
    public static String getDeleteBackUpUrl(Context context){
        return context.getString(R.string.deletebackup);
    }
    public static String getProtocolUrl(Context context){
        return context.getString(R.string.protocol);
    }
    public static String getVersionUrl(Context context){
        return context.getString(R.string.version);
    }
}
