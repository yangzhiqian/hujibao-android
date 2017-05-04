package edu.ncu.safe;

import android.app.Application;
import android.content.SharedPreferences;
import android.view.SurfaceView;

/**
 * Created by Mr_Yang on 2016/7/13.
 */
public class MyApplication extends Application {
    private static final String SHAREDPREFERENCES_NAME="appconfig";
    public static final String SP_BOOLEAN_IS_FIRST_RNTER_APP = "IsFirstEnterApp";
    public static final String SP_BOOLEAN_IS_IN_PROTECTING = "IsInProtecting";
    public static final String SP_LONG_TOTAL_FLOWS = "TotalFlows";
    public static final String SP_BOOLEAN_HAS_PWD = "HasPwd";
    public static final String SP_STRING_PWD = "Pwd";
    public static final String SP_STRING_USER = "User";
    public static final String SP_BOOLEAN_IS_SMS_CHANGE_MESSAGE = "IsSmsChangeMessage";
    public static final String SP_BOOLEAN_IS_REMOTE_DELETE = "IsRemoteDelete";
    public static final String SP_BOOLEAN_IS_REMOTE_LOCK = "IsRemoteLock";
    public static final String SP_BOOLEAN_IS_RING = "IsRing";
    public static final String SP_BOOLEAN_IS_REMOTE_CHANGE_LOCK_PWD = "IsRemoteChangeLockPwd";
    public static final String SP_BOOLEAN_IS_LOCATION = "IsLocation";
    public static final String SP_STRING_USER_PHONE_NUMBER = "UserPhoneNumber";
    public static final String SP_STRING_SAFE_PHONE_NUMBER = "SafePhoneNumber";
    public static final String PHONE_LOST_DEFAULT_PWD = "123456";
    public static final String SP_LONG_DB_OFFSET = "DBOffset";
    public static final String SP_INT_OFFSET_UPDATE = "OffsetUpdate";
    public static final String SP_LONG_WARNING_FLOWS = "WarningFlows";
    public static final String SP_INT_FLOWS_WARNINGTYPE = "FlowsWarningType";


    private static SharedPreferences sp;

    @Override
    public void onCreate() {
        super.onCreate();
        sp = getSharedPreferences(SHAREDPREFERENCES_NAME,MODE_PRIVATE);
    }

    public static SharedPreferences getSharedPreferences(){
        return sp;
    }
}
