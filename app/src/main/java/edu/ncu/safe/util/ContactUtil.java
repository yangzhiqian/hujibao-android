package edu.ncu.safe.util;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

/**
 * Created by Mr_Yang on 2016/5/15.
 */
public class ContactUtil {
    /**
     * 给某人发短信，进入短信编辑状态
     * @param context   上下文
     * @param number    要发送给的目的号码
     */
    public static void sendMessageTo(Context context, String number) {
        Uri uri = Uri.parse("smsto:" + number);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        context.startActivity(intent);
    }

    /**
     * 给某人打电话，进入直接拨打状态
     * @param context   上下文
     * @param number    要拨打的号码
     */
    public static void callTo(Context context, String number) {
        Uri uri = Uri.parse("tel:" + number);
        Intent intent = new Intent(Intent.ACTION_CALL, uri);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context,"没有打电话的权限！",Toast.LENGTH_SHORT).show();
            return;
        }
        context.startActivity(intent);
    }
}
