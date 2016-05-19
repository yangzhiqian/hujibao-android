package edu.ncu.safe.engine;

import android.content.Context;
import android.content.SharedPreferences;

import edu.ncu.safe.domain.InterceptionSetInfo;

/**
 * Created by Mr_Yang on 2016/5/18.
 */
public class InterceptionSetOperation {
    public static final String INTERCEPTIONSET = "interceptionset";

    public static final String MODE = "mode";//0,1,2,3，4
    public static final String ISNIGHTMODE = "isnightmode";
    public static final String BEGINHOUE = "beginhour";
    public static final String BEGINMINUTE = "beginminute";
    public static final String ENDHOUR = "endhour";
    public static final String ENDMINUTE = "endminute";
    public static final String ISSUN = "issun";
    public static final String ISMON = "issun";
    public static final String ISTUR = "issun";
    public static final String ISWEN = "issun";
    public static final String ISTHU = "issun";
    public static final String ISFRI = "issun";
    public static final String ISSTA = "issun";
    public static final String NIGHTMODE_MODE = "nightmodemode";


    private SharedPreferences sp;

    public InterceptionSetOperation(Context context) {
        sp = context.getSharedPreferences(INTERCEPTIONSET, Context.MODE_MULTI_PROCESS);
    }
    public InterceptionSetInfo getInterceptionSetInfo() {
        int mode = sp.getInt(MODE, 0);
        boolean isNightMode = sp.getBoolean(ISNIGHTMODE, false);
        int beginHour = sp.getInt(BEGINHOUE, 23);
        int beginMinute = sp.getInt(BEGINMINUTE, 0);
        int endHour = sp.getInt(ENDHOUR, 6);
        int ednMinute = sp.getInt(ENDMINUTE, 0);
        boolean[] weekEnable = {sp.getBoolean(ISSUN, false),
                sp.getBoolean(ISMON, true),
                sp.getBoolean(ISTUR, true),
                sp.getBoolean(ISWEN, true),
                sp.getBoolean(ISTHU, true),
                sp.getBoolean(ISFRI, true),
                sp.getBoolean(ISSTA, false)};
        int nightMode = sp.getInt(NIGHTMODE_MODE, 2);
        return new InterceptionSetInfo(mode, isNightMode, beginHour, beginMinute, endHour, beginMinute, weekEnable, nightMode);
    }

    public void commitInterceptionSetInfo(InterceptionSetInfo info) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(MODE, info.getMode());
        editor.putBoolean(ISNIGHTMODE, info.isNightMode());
        editor.putInt(BEGINHOUE, info.getBeginHour());
        editor.putInt(BEGINMINUTE, info.getBeginMinute());
        editor.putInt(ENDHOUR, info.getEndHour());
        editor.putInt(ENDMINUTE, info.getEndMinute());

        editor.putBoolean(ISSUN, info.getWeekEnable()[0]);
        editor.putBoolean(ISMON, info.getWeekEnable()[1]);
        editor.putBoolean(ISTUR, info.getWeekEnable()[2]);
        editor.putBoolean(ISWEN, info.getWeekEnable()[3]);
        editor.putBoolean(ISTHU, info.getWeekEnable()[4]);
        editor.putBoolean(ISFRI, info.getWeekEnable()[5]);
        editor.putBoolean(ISSTA, info.getWeekEnable()[6]);

        editor.putInt(NIGHTMODE_MODE, info.getNightMode());
        editor.apply();
    }
}
