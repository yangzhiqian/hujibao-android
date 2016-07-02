package edu.ncu.safe.engine;

import android.content.Context;

import java.util.Calendar;

import edu.ncu.safe.db.dao.CommunicationDatabase;
import edu.ncu.safe.domain.InterceptionSetInfo;
import edu.ncu.safe.domain.WhiteBlackNumberInfo;

/**
 * Created by Mr_Yang on 2016/5/18.
 */
public class InterceptionJudger {
    private String number;

    private Context context;
    private CommunicationDatabase db;
    private ContactsService contacts;
    private InterceptionSetInfo setInfo;

    public InterceptionJudger(Context context) {
        this.context = context;
        db = new CommunicationDatabase(context);
        contacts = new ContactsService(context);
        setInfo = new InterceptionSetOperation(context).getInterceptionSetInfo();
    }

    /**
     * 判断短信是否需要进行拦截
     *
     * @return true代表需要拦截
     */
    public boolean isShouldSmsIntercepte(String number) {
        this.number = number;
        if (isInNightMode()) {
            //处在夜间免打扰下
            if (isModeSms(setInfo.getNightMode())) {
                //免打扰模式下的模式开启了相关的拦截
                return true;
            }
        }
        return isModeSms(setInfo.getMode());
    }

    /**
     * 判断时候要进行拦截
     *
     * @return true代表需要拦截
     */
    public boolean isShouldPhoneIntercepte(String number) {
        this.number = number;
        if (isInNightMode()) {
            //处在夜间免打扰下
            if (isModePhone(setInfo.getNightMode())) {
                //免打扰模式下的模式开启了相关的拦截
                return true;
            }
        }
        return isModePhone(setInfo.getMode());
    }

    /**
     * 判断是否处于夜间免打扰模式，包括对配置文件里的ISNIGHTMODE的判断、时间的判断，星期的判断
     *
     * @return
     */
    private boolean isInNightMode() {
        if (setInfo.isNightMode()) {
            //开启了夜间免打扰模式
            int bh = setInfo.getBeginHour();
            int bm = setInfo.getBeginMinute();
            int eh = setInfo.getEndHour();
            int em = setInfo.getEndMinute();
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR);
            int minute = calendar.get(Calendar.MINUTE);
            if (hour > bh || hour < eh || (hour == bh && minute >= bm) || (hour == eh && minute <= em)) {
                //在免打扰时间内(时分)
                int week = calendar.get(Calendar.DAY_OF_WEEK);
                //是否在设定的星期内是则返回true，否则返回false
                return setInfo.getWeekEnable()[week-Calendar.SUNDAY];
            }
        }
        return false;
    }

    //sms判断
    private boolean isModeSms(int mode) {
        switch (mode) {
            case 0:
                return isMode0Sms();
            case 1:
                return isMode1Sms();
            case 2:
                return isMode2Sms();
            case 3:
                return isMode3Sms();
            case 4:
                return isMode4Sms();
        }
        return false;
    }

    //模式五  //全部拦截
    private boolean isMode4Sms() {
        return true;
    }

    //模式四   只接受不联系人和白名单的号码和来信
    private boolean isMode3Sms() {
        if (contacts.getContactName(number) != null) {
            //在联系人列表中找到该号码，不需要拦截
            return false;
        }
        WhiteBlackNumberInfo info = db.queryNumberInWhiteList(number);
        if (info != null && info.isSms()) {
            //在白名单中 并且有可以享受不被拦截的权限
            return false;
        }
        return true;
    }

    //模式三//只有白名单可以接收来电和短信
    private boolean isMode2Sms() {
        WhiteBlackNumberInfo info = db.queryNumberInWhiteList(number);
        if (info != null && info.isSms()) {
            //在白名单中 并且有可以享受不被拦截的权限
            return false;
        }
        return true;
    }

    //模式二//只拦截黑名单
    private boolean isMode1Sms() {
        WhiteBlackNumberInfo info = db.queryNumberInBlackList(number);
        if (info != null && info.isSms()) {
            //在黑名单中 并且设置了拦截短信的权限
            return true;
        }
        return false;
    }

    //模式一//不拦截
    private boolean isMode0Sms() {
        return false;
    }


    private boolean isModePhone(int mode) {
        switch (mode) {
            case 0:
                return isMode0Phone();
            case 1:
                return isMode1Phone();
            case 2:
                return isMode2Phone();
            case 3:
                return isMode3Phone();
            case 4:
                return isMode4Phone();
        }
        return false;
    }

    //模式五  //全部拦截
    private boolean isMode4Phone() {
        return true;
    }

    //模式四   只接受不联系人和白名单的号码和来信
    private boolean isMode3Phone() {
        if (contacts.getContactName(number) != null) {
            //在联系人列表中找到该号码，不需要拦截
            return false;
        }
        WhiteBlackNumberInfo info = db.queryNumberInWhiteList(number);
        if (info != null && info.isPhoneCall()) {
            //在白名单中 并且有可以享受不被拦截的权限
            return false;
        }
        return true;
    }

    //模式三//只有白名单可以接收来电和短信
    private boolean isMode2Phone() {
        WhiteBlackNumberInfo info = db.queryNumberInWhiteList(number);
        if (info != null && info.isPhoneCall()) {
            //在白名单中 并且有可以享受不被拦截的权限
            return false;
        }
        return true;
    }

    //模式二//只拦截黑名单
    private boolean isMode1Phone() {
        WhiteBlackNumberInfo info = db.queryNumberInBlackList(number);
        if (info != null && info.isPhoneCall()) {
            //在黑名单中 并且设置了拦截短信的权限
            return true;
        }
        return false;
    }

    //模式一//不拦截
    private boolean isMode0Phone() {
        return false;
    }


    private boolean matchWeek(int week, int modeWeek) {
        switch (week) {
            case Calendar.SUNDAY:
                if ((modeWeek & 1) > 0) {
                    return true;
                }
                break;
            case Calendar.MONDAY:
                if ((modeWeek & 2) > 0) {
                    return true;
                }
                break;
            case Calendar.TUESDAY:
                if ((modeWeek & 4) > 0) {
                    return true;
                }
                break;
            case Calendar.WEDNESDAY:
                if ((modeWeek & 8) > 0) {
                    return true;
                }
                break;
            case Calendar.THURSDAY:
                if ((modeWeek & 16) > 0) {
                    return true;
                }
                break;
            case Calendar.FRIDAY:
                if ((modeWeek & 32) > 0) {
                    return true;
                }
                break;
            case Calendar.SATURDAY:
                if ((modeWeek & 64) > 0) {
                    return true;
                }
                break;
        }
        return false;
    }
}
