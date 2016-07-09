package edu.ncu.safe.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

import java.io.InputStream;

import edu.ncu.safe.R;
import edu.ncu.safe.View.MyProgressBar;
import edu.ncu.safe.engine.DataLoader;
import edu.ncu.safe.external.ACache;

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

    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }



    /**
     * 加载指定在本地的图片
     *
     * @param path      图片路径全名
     * @param maxWidth  压缩后的指定的最大的宽度，当ImageView 宽过大时起作用，<=0表示不启用限制
     * @param maxHeight 压缩后的指定的最大的高度，当ImageView 高过大时起作用，<=0表示不启用限制
     * @param view      要显示的地方，用于测量宽高
     * @return 返回获取的image对象，null代表加载失败
     */
    public static Bitmap loadLoaclImageToImageView(String path, int maxWidth, int maxHeight, ImageView view) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        // Calculate inSampleSize
        view.measure(0, 0);
        int width = view.getMeasuredWidth();
        int height = view.getMeasuredHeight();

        if (width > maxWidth && maxWidth > 0) {
            width = maxWidth;
        }
        if (height > maxHeight && maxHeight > 0) {
            height = maxHeight;
        }
        options.inSampleSize = calculateInSampleSize(options, width, height);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * 加载（内存缓存->文件缓存->网络）图片
     * @param context   上下文
     * @param token       用户网络下载时的用户标示
     * @param fileName      文件名或文件路径，如果下载的图片来自本地，则表示文件全路径，如果文件来自网络，则表示文件名
     * @param type          图片类型   0代表小图标，1代表预览图片（400x600,由服务器决定,2代表原图）
     * @param view           如果用于加载到imageview上显示，可指定该参数，异步加载完成后会自动显示，可以为null
     * @param mpb           加载进度
     */
    public static void loadImageToImageView(final Context context,String token,final String fileName,final int type ,final ImageView view,final MyProgressBar mpb){
        //实例化加载工具
        DataLoader loader = new DataLoader(context);
        //实例化监听器，监听器运行的线程为主线程
        loader.setOnImageObtainedListener(new DataLoader.OnImageObtainedListener() {
            @Override
            public void onFailure(String error) {
                if(mpb!=null){
                    mpb.setVisibility(View.GONE);
                }
            }
            @Override
            public void onResponse(Bitmap bmp) {
                //缓存
                ACache.get(context).put(fileName+"-"+ type, bmp, ACache.TIME_DAY * 7);//缓存七天
                if(view!=null) {
                    view.setImageBitmap(bmp);
                }
                if(mpb!=null){
                    mpb.setVisibility(View.GONE);
                }
            }
        });
        String url = context.getResources().getString(R.string.loadimg);
        loader.loadImg(url,token,fileName,type,mpb);
    }

    public static Bitmap getRequireBitmap(String url, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(url,options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(url,options);
    }
}
