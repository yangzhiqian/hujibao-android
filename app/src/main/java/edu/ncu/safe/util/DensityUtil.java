package edu.ncu.safe.util;

import android.content.Context;
import android.graphics.Point;
import android.view.WindowManager;

/**
 * Created by Mr_Yang on 2016/7/12.
 */
public class DensityUtil {

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int  getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Point outSize = new Point();
        wm.getDefaultDisplay().getSize(outSize);
        return outSize.y;
    }
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Point outSize = new Point();
        wm.getDefaultDisplay().getSize(outSize);
        return outSize.x;
    }

}
