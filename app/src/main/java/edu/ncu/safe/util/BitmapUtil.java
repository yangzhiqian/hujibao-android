package edu.ncu.safe.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;

/**
 * Created by Mr_Yang on 2016/5/15.
 */
public class BitmapUtil {

    /**
     * 以最省内存的方式读取本地资源的图片
     * 在用imageview的设置图片时尽量不要用 setImageBitmap或setImageResource或BitmapFactory.decodeResource来设置一张大图
     * 因为这些函数在完成decode后，最终都是通过java层的createBitmap来完成的，需要消耗更多内存。
     * 导致出现oom
     * @param context  上下文
     * @param resId    资源id
     * @return    bitmap对象
     */
    public static Bitmap readBitMap(Context context, int resId){
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        //下面两句可有效解决oom问题
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        //获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is,null,opt);
    }
}
