package edu.ncu.safe.engine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.File;
import java.io.IOException;

import edu.ncu.safe.R;
import edu.ncu.safe.customerview.MyProgressBar;
import edu.ncu.safe.domain.User;
import edu.ncu.safe.external.ACache;
import edu.ncu.safe.external.okhttpprogress.ProgressResponseBody;
import edu.ncu.safe.external.okhttpprogress.UIProgressResponseListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Mr_Yang on 2016/7/4.
 */
public class DataLoader {
    private static final String TAG = "DataLoader";
    public static final int TYPE_SMALL = 0;
    public static final int TYPE_MIDDLE = 1;
    public static final int TYPE_BIG = 2;

    private static final int TYPE_DATA_ERROR = 0;
    private static final int TYPE_DATA_SUCCEED = 1;
    private static final int TYPE_IMG_ERROR = 2;
    private static final int TYPE_IMG_SUCCEED = 3;

    private Context context;
    private OkHttpClient client;

    private Handler myHandler = new Handler(Looper.getMainLooper()) {


        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case TYPE_DATA_ERROR:
                    if (dataListener != null) {
                        dataListener.onFailure((String) msg.obj);
                    }
                    break;
                case TYPE_DATA_SUCCEED:
                    if (dataListener != null) {
                        dataListener.onResponse((String) msg.obj);
                    }
                    break;
                case TYPE_IMG_ERROR:
                    if (imgListener != null) {
                        imgListener.onFailure((String) msg.obj);
                    }
                    break;
                case TYPE_IMG_SUCCEED:
                    if (imgListener != null) {
                        imgListener.onResponse((Bitmap) msg.obj);
                    }
                    break;
            }
            super.handleMessage(msg);

        }
    };

    public DataLoader(Context context) {
        this.context = context;
        client = new OkHttpClient.Builder().build();
    }

    public void loadServerJson(String url, String[] valuesNames, String[] values) {
        FormBody.Builder body = new FormBody.Builder();
        for (int i = 0; i < valuesNames.length; i++) {
            body.add(valuesNames[i], values[i]);
        }
        Request request = new Request.Builder()
                .url(url)
                .post(body.build())
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg = Message.obtain();
                msg.what = TYPE_DATA_ERROR;
                msg.obj = e.getMessage();
                myHandler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message msg = Message.obtain();
                msg.what = TYPE_DATA_SUCCEED;
                msg.obj = response.body().string();
                myHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 加载（内存缓存->文件缓存->网络）图片
     * @param fileName      文件名或文件路径，如果下载的图片来自本地，则表示文件全路径，如果文件来自网络，则表示文件名
     * @param type          图片类型   0代表小图标，1代表预览图片（400x600,由服务器决定,2代表原图）
     * @param mpb           加载进度
     */
    public  void loadImage(final String fileName,final int type ,final MyProgressBar mpb,OnImageObtainedListener listener){
        //实例化监听器，监听器运行的线程为主线程
        setOnImageObtainedListener(listener);
        String url = context.getResources().getString(R.string.loadimg);
        loadImg(url, fileName, type, mpb);
    }

    public void loadImg(String url,String filename, int type, final MyProgressBar mpb) {
        //本地缓存中获取
        Bitmap bitmap = ACache.get(context).getAsBitmap(filename + "-" + type);
        if(bitmap!=null){
            Message msg = Message.obtain();
            msg.what = TYPE_IMG_SUCCEED;
            msg.obj = bitmap;
            myHandler.sendMessage(msg);
            return;
        }
        //本地获取
        int width = 100;
        int height = 100;
        if(type==TYPE_MIDDLE){
            width = 400;
            height=600;
        }
        bitmap = loadLoaclImage(filename, width,height);
        if (bitmap != null) {
            Message msg = Message.obtain();
            msg.what = TYPE_IMG_SUCCEED;
            msg.obj = bitmap;
            myHandler.sendMessage(msg);
            return;
        }

        //本地没有，网络上获取
        //获取用户信息
        User user = User.getUserFromSP(context);
        if(user==null){
            Message msg = Message.obtain();
            msg.what = TYPE_IMG_ERROR;
            msg.obj = "您还未登录";
            myHandler.sendMessage(msg);
            return;
        }
        final UIProgressResponseListener uiProgressResponseListener = new UIProgressResponseListener() {
            @Override
            public void onUIResponseProgress(long bytesRead, long contentLength, boolean done) {
                if (mpb != null) {
                    if (contentLength == -1) {
                        mpb.setPercentSlow(0);
                    }else {
                        mpb.setPercentSlow(((100.0f * bytesRead) / contentLength));
                    }
                }
            }
        };
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("token", user.getToken())
                .add("filename", filename)
                .add("type", type + "")
                .build();
        final Request request = new Request.Builder()
                .url(url)
                .post(requestBodyPost)
                .build();


//添加拦截器，自定义ResponseBody，添加下载进度
        client = new OkHttpClient.Builder().addInterceptor(
                new Interceptor() {
                    @Override
                    public Response intercept(Interceptor.Chain chain) throws IOException {
                        Response originalResponse = chain.proceed(chain.request());
                        return originalResponse.newBuilder().body(
                                new ProgressResponseBody(originalResponse.body(), uiProgressResponseListener))
                                .build();
                    }
                }).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg = Message.obtain();
                msg.what = TYPE_IMG_ERROR;
                msg.obj = e.getMessage();
                myHandler.sendMessage(msg);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Bitmap bmp = BitmapFactory.decodeStream(response.body().byteStream());
                Message msg = Message.obtain();
                msg.what = TYPE_IMG_SUCCEED;
                msg.obj = bmp;
                myHandler.sendMessage(msg);
            }
        });


    }

    private Bitmap loadLoaclImage(String path, int maxWidth, int maxHeight) {
        File f = new File(path);
        if (!f.exists()) {
            return null;
        }
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    public void deleteBackup(String token,int type,int id){
        FormBody body = new FormBody.Builder().add("token", token).add("type", type + "").add("id", id + "").build();
        String url = context.getResources().getString(R.string.deletebackup);
        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg = Message.obtain();
                msg.what = TYPE_DATA_ERROR;
                msg.obj = e.getMessage();
                myHandler.sendMessage(msg);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message msg = Message.obtain();
                msg.what = TYPE_DATA_SUCCEED;
                msg.obj = response.body().string();
                myHandler.sendMessage(msg);
            }
        });
    }

    private int calculateInSampleSize(
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


    private OnDataObtainedListener dataListener;
    private OnImageObtainedListener imgListener;

    public void setOnImageObtainedListener(OnImageObtainedListener listener) {
        this.imgListener = listener;
    }

    public void setOnDataObtainListener(OnDataObtainedListener listener) {
        this.dataListener = listener;
    }

    public interface OnDataObtainedListener {
        void onFailure(String error);

        void onResponse(String response);
    }

    public interface OnImageObtainedListener {
        void onFailure(String error);

        void onResponse(Bitmap bmp);
    }
}
