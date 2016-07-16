package edu.ncu.safe.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Mr_Yang on 2016/7/13.
 */
public class MyUtil {
    /**
     * 获取本机手机号码
     * @return
     */
    public static  String getPhoneNumber(Context context) {
        TelephonyManager manager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNumaber = manager.getLine1Number();
        if (phoneNumaber == null) {
            phoneNumaber = "";
        }
        return phoneNumaber;
    }

    /**
     * 显示一条notification
     * @param context   上下文
     * @param clz        跳转的类
     * @param id         唯一标示
     * @param resIcon    小图标的id
     * @param tickerText 精简版的弹出消息（用户没有下拉查看时显示）
     * @param title       标题
     * @param message     消息主体内容
     */
    public static void showNotification(Context context,Class clz,int id,int resIcon,String tickerText,String title,String message){
        // 在Android进行通知处理，首先需要重系统哪里获得通知管理器NotificationManager，它是一个系统Service。
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, clz), 0);
        Notification notify = new Notification.Builder(context)
                .setSmallIcon(resIcon)
                .setTicker(tickerText)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent).setNumber(1).build(); // 需要注意build()是在API
        // level16及之后增加的，API11可以使用getNotificatin()来替代
        notify.flags |= Notification.FLAG_AUTO_CANCEL; // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。
        manager.notify(id, notify);// 步骤4：通过通知管理器来发起通知。如果id不同，则每click，在status哪里增加一个提示
    }


    /**
     * 判断是否为电话号码
     * @param mobiles 电话号码
     * @return  true标示为电话号码
     */
    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }
}
