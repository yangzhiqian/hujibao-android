package edu.ncu.safe.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import edu.ncu.safe.R;
import edu.ncu.safe.customerview.MyProgressBar;
import edu.ncu.safe.constant.Constant;
import edu.ncu.safe.engine.NetDataOperator;
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
     *
     * @param context 上下文
     * @param resId   资源id
     * @return bitmap对象
     */
    public static Bitmap readBitMap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        //下面两句可有效解决oom问题
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        //获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    /**
     * 根据大小计算图片缩放比例
     * @param options     调用
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
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
     * @return 返回获取的image对象，null代表加载失败
     */
    public static Bitmap loadLoaclImage(String path, int maxWidth, int maxHeight) {
        File f = new File(path);
        if (!f.exists()) {
            return null;
        }
        final BitmapFactory.Options options = new BitmapFactory.Options();
        if(maxHeight!=0&&maxWidth!=0) {
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            // 计算缩放比例
            options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
            //编码图片
            options.inJustDecodeBounds = false;
        }
        return BitmapFactory.decodeFile(path, options);
    }


    public static Bitmap getRequireBitmap(String url, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(url, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(url, options);
    }

    /**
     * 将bitmap对象保存在文件中
     *
     * @param path     文件保存的路径
     * @param fileName 文件名
     * @param bmp      要保存的bitmap 对象
     * @return ture表示保存成功
     * @throws FileNotFoundException
     */
    public static boolean saveBitmapToFile(String path, String fileName, Bitmap bmp) throws IOException {
        File file = new File(path, fileName);
        Log.i("TAG", path + "/" + fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        int quality = 100;
        OutputStream stream = new FileOutputStream(file);
        return bmp.compress(format, quality, stream);
    }

    /**
     * Drawable 对象转换成Bitmap对象
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        if(drawable==null){
            return null;
        }
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 缩放bitmap
     * @param bitmap
     * @param width
     * @param height
     * @return
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        return newbmp;
    }

    public static byte[] drawableToByteArray(Drawable drawable){
        Bitmap bitmap = drawableToBitmap(drawable);
        byte[] bytes = bitmapToByteArray(bitmap);
        bitmap.recycle();//释放资源
        return bytes;
    }
    public static byte[] bitmapToByteArray(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
        byte[] bytes = baos.toByteArray();
        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;

    }
}
