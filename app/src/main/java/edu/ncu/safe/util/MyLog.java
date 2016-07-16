package edu.ncu.safe.util;

import android.util.Log;

/**
 * Created by Mr_Yang on 2016/5/15.
 */
public class MyLog {
    public static final int LEVEL = 6;
    public static final int LEVEL_V = 0;
    public static final int LEVEL_D = 1;
    public static final int LEVEL_I = 2;
    public static final int LEVEL_W = 3;
    public static final int LEVEL_E = 4;

    public static void v(String TAG,String message){
        if(LEVEL>=LEVEL_V)
            Log.v(TAG,message);
    }

    public static void d(String TAG,String message){
        if(LEVEL>=LEVEL_D)
            Log.d(TAG, message);
    }

    public static void i(String TAG,String message){
        if(LEVEL>=LEVEL_I)
            Log.i(TAG, message);
    }

    public static void w(String TAG,String message){
        if(LEVEL>=LEVEL_W)
            Log.w(TAG, message);
    }

    public static void e(String TAG,String message){
        if(LEVEL>=LEVEL_E)
            Log.e(TAG, message);
    }

}
