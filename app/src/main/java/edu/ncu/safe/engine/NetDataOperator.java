package edu.ncu.safe.engine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import edu.ncu.safe.constant.UrlHelper;
import edu.ncu.safe.domain.User;
import edu.ncu.safe.external.ACache;
import edu.ncu.safe.external.okhttpprogress.ProgressHelper;
import edu.ncu.safe.external.okhttpprogress.ProgressRequestBody;
import edu.ncu.safe.external.okhttpprogress.ProgressResponseBody;
import edu.ncu.safe.external.okhttpprogress.UIProgressRequestListener;
import edu.ncu.safe.external.okhttpprogress.UIProgressResponseListener;
import edu.ncu.safe.util.BitmapUtil;
import edu.ncu.safe.util.MyUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Mr_Yang on 2016/7/4.
 */
public class NetDataOperator {
    private static final int TYPE_DATA_ERROR = 0;
    private static final int TYPE_DATA_SUCCEED = 1;
    public static final int TYPE_DELETE_ERROR = 2;
    public static final int TYPE_DELETE_SUCCEED = 3;
    public static final int TYPE_UPDATE_ERROR = 4;
    public static final int TYPE_UPDATE_SUCCEED = 5;
    private static final int TYPE_IMG_LOADING_ERROR = 6;
    private static final int TYPE_IMG_LOADING_SUCCEED = 7;
    private static final int TYPE_IMG_UPLOADING_ERROR = 8;
    private static final int TYPE_IMG_UPLOADING_SUCCEED = 9;
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private Context context;
    private OkHttpClient client;

    private static Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Object[] os = (Object[]) msg.obj;
            switch (msg.what) {
                case TYPE_DATA_ERROR:
                case TYPE_DELETE_ERROR:
                case TYPE_UPDATE_ERROR:
                    if (os[0] != null) {
                        ((OnResponseListener) (os[0])).onError((String) os[1]);
                    }
                    break;
                case TYPE_DATA_SUCCEED:
                case TYPE_DELETE_SUCCEED:
                case TYPE_UPDATE_SUCCEED:
                    if (os[0] != null) {
                        ((OnResponseListener) (os[0])).onResponse((String) os[1]);
                    }
                    break;
                case TYPE_IMG_LOADING_ERROR:
                    if (os[0] != null) {
                        ((OnImageLoadingListener) (os[0])).onFailure((String) os[1]);
                    }
                    break;
                case TYPE_IMG_LOADING_SUCCEED:
                    if (os[0] != null) {
                        ((OnImageLoadingListener) (os[0])).onResponse((Bitmap) os[1]);
                    }
                    break;
                case TYPE_IMG_UPLOADING_ERROR:
                    if (os[0] != null) {
                        ((OnImageUploadingListener) (os[0])).onFailure((String) os[1]);
                    }
                    break;
                case TYPE_IMG_UPLOADING_SUCCEED:
                    if (os[0] != null) {
                        ((OnImageUploadingListener) (os[0])).onResponse((String) os[1]);
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public NetDataOperator(Context context) {
        this.context = context.getApplicationContext();
        client = new OkHttpClient.Builder().build();
    }

    /**
     * 上传普通信息
     *
     * @param type      上传的类型
     * @param jsonArray 数据主体
     * @param listener  回调
     * @throws JSONException
     */
    public void storeBackUpNormalDatas(BACKUP_TYPE type, JSONArray jsonArray, final OnResponseListener listener) throws JSONException {
        User user = User.getUserFromSP(context);
        //验证登录信息
        if (user == null) {
            if (listener != null) {
                listener.onError("您还未登录！");
            }
            return;
        }
        JSONObject object = new JSONObject();
        object.put("token", user.getToken());
        object.accumulate("data", jsonArray);
        object.put("type", type.ordinal() + "");
        FormBody body = new FormBody.Builder().add("message", object.toString()).build();
        final Request request = new Request.Builder().url(UrlHelper.getStoreBackUpUrl(context)).post(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg = Message.obtain();
                msg.what = TYPE_UPDATE_ERROR;
                Object[] os = new Object[2];
                os[0] = listener;
                os[1] = e.getMessage();
                msg.obj = os;
                myHandler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message msg = Message.obtain();
                msg.what = TYPE_UPDATE_SUCCEED;
                Object[] os = new Object[2];
                os[0] = listener;
                os[1] = response.body().string();
                msg.obj = os;
                myHandler.sendMessage(msg);
            }
        });
    }

    public void storeBackUpImgDatas(String filePath, final OnImageUploadingListener listener) throws JSONException {
        File file = new File(filePath);
        if (file == null) {
            if (listener != null) {
                listener.onFailure("选择的图片不存在！");
            }
            return;
        }
        User user = User.getUserFromSP(context);
        if(user==null){
            if (listener != null) {
                listener.onFailure("您还未登录！");
            }
            return;
        }

        JSONObject object = new JSONObject();
        object.put("token", user.getToken());
        object.put("filename", file.getName());
        object.put("lastupdate", file.lastModified());
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder.addFormDataPart("img", file.getName(), RequestBody.create(MEDIA_TYPE_PNG, file));
        builder.addFormDataPart("message", object.toString());
        MultipartBody requestBody = builder.build();

        //这个是ui线程回调，可直接操作UI
        final UIProgressRequestListener uiProgressRequestListener = new UIProgressRequestListener() {
            @Override
            public void onUIRequestProgress(long bytesWrite, long contentLength, boolean done) {
                if(listener!=null){
                    listener.onUploadingProgressChanged((int)((100 * bytesWrite) / contentLength));
                }
            }
        };
        ProgressRequestBody progressRequestBody = ProgressHelper.addProgressRequestListener(requestBody, uiProgressRequestListener);
        //构建请求
        Request request = new Request.Builder()
                .url(UrlHelper.getStoreImgUrl(context))//地址
                .post(progressRequestBody)//添加请求体
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg = Message.obtain();
                msg.what = TYPE_IMG_UPLOADING_ERROR;
                Object[] os = new Object[2];
                os[0] = listener;
                os[1] = e.getMessage();
                msg.obj = os;
                myHandler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message msg = Message.obtain();
                msg.what = TYPE_IMG_UPLOADING_SUCCEED;
                Object[] os = new Object[2];
                os[0] = listener;
                os[1] = response.body().string();
                msg.obj = os;
                myHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 使用okhttp构件表单请求
     *
     * @param type   请求的类型
     * @param listener 请求的回调
     */
    public void loadBackUpDatas(BACKUP_TYPE type,int offset,int num, final OnResponseListener listener) {
        User user = User.getUserFromSP(context);
        //验证登录信息
        if (user == null) {
            if (listener != null) {
                listener.onError("您还未登录！");
            }
            return;
        }
        FormBody.Builder body = new FormBody.Builder();
        body.add("token", user.getToken());
        body.add("type",type.ordinal()+"");
        body.add("offset",offset+"");
        body.add("number",num+"");
        Request request = new Request.Builder()
                .url(UrlHelper.getLoadBackUpUrl(context))
                .post(body.build())
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg = Message.obtain();
                msg.what = TYPE_DATA_ERROR;
                Object[] os = new Object[2];
                os[0] = listener;
                os[1] = e.getMessage();
                msg.obj = os;
                myHandler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message msg = Message.obtain();
                msg.what = TYPE_DATA_SUCCEED;
                Object[] os = new Object[2];
                os[0] = listener;
                os[1] = response.body().string();
                msg.obj = os;
                myHandler.sendMessage(msg);
                return;
            }
        });
    }


    /**
     * 加载图片
     * 首先从缓存文件中获取，没有再从本地文件中获取，最后再从网络中获取
     *
     * @param filename 文件名称，缓存的名字或者是网络中文件的名称，如果是本地文件则代表文件全路径
     * @param type     图片的大小  {@link IMG_TYPE},表示获取的图片的大小
     * @param listener 获取进度的回调
     */
    public void loadImage(final String filename, final IMG_TYPE type, final OnImageLoadingListener listener) {
        //本地缓存中获取
        Bitmap bitmap = ACache.get(context).getAsBitmap(filename + "-" + type.ordinal());
        if (bitmap != null) {
            //从本地缓存中加载成功
            if (listener != null) {
                listener.onResponse(bitmap);
            }
            return;
        }
        //本地获取
        Point p = getAdaptedSizeByType(type, context);
        bitmap = BitmapUtil.loadLoaclImage(filename, p.x, p.y);
        if (bitmap != null) {
            //本地文件获取成功
            if (listener != null) {
                listener.onResponse(bitmap);
            }
            return;
        }

        //本地没有，网络上获取
        User user = User.getUserFromSP(context);
        if (user == null) {
            if (listener != null) {
                listener.onFailure("您还未登录！");
            }
            return;
        }
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("token", user.getToken())
                .add("filename", filename)
                .add("type", (type.ordinal() + 1) + "")
                .build();
        final Request request = new Request.Builder()
                .url(UrlHelper.getLoadImgUrl(context))
                .post(requestBodyPost)
                .build();

        final UIProgressResponseListener uiProgressResponseListener = new UIProgressResponseListener() {
            @Override
            public void onUIResponseProgress(long bytesRead, long contentLength, boolean done) {
                if (listener != null) {
                    listener.onLoadingProgressChanged(contentLength == -1 ? 0 : (int) (bytesRead * 100 / contentLength));
                }
            }
        };
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
                msg.what = TYPE_IMG_LOADING_ERROR;
                Object[] os = new Object[2];
                os[0] = listener;
                os[1] = e.getMessage();
                msg.obj = os;
                myHandler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Bitmap bmp = BitmapFactory.decodeStream(response.body().byteStream());
                Message msg = Message.obtain();
                msg.what = TYPE_IMG_LOADING_SUCCEED;
                Object[] os = new Object[2];
                os[0] = listener;
                os[1] = bmp;
                msg.obj = os;
                myHandler.sendMessage(msg);
                ACache.get(context).put(filename + "-" + type.ordinal(), bmp, ACache.TIME_DAY*7);//缓存七天
            }
        });
    }

    /**
     * 从服务器中删除某条已经备份的数据
     *
     * @param type
     * @param id
     * @param listener
     */
    public void deleteBackup(BACKUP_TYPE type, int id, final OnResponseListener listener) {
        User user = User.getUserFromSP(context);
        //验证登录信息
        if (user == null) {
            if (listener != null) {
                listener.onError("您还未登录！");
            }
            return;
        }
        FormBody body = new FormBody.Builder().add("token", user.getToken()).add("type", (type.ordinal() + 1) + "").add("id", id + "").build();
        Request request = new Request.Builder().url(UrlHelper.getDeleteBackUpUrl(context)).post(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg = Message.obtain();
                msg.what = TYPE_DELETE_ERROR;
                Object[] os = new Object[2];
                os[0] = listener;
                os[1] = e.getMessage();
                msg.obj = os;
                myHandler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message msg = Message.obtain();
                msg.what = TYPE_DATA_SUCCEED;
                Object[] os = new Object[2];
                os[0] = listener;
                os[1] = response.body().string();
                msg.obj = os;
                myHandler.sendMessage(msg);
            }
        });
    }


    private static Point getAdaptedSizeByType(IMG_TYPE type, Context context) {
        Point point = new Point(400, 600);
        if (type.ordinal() == IMG_TYPE.TYPE_ICON.ordinal()) {
            point.x = 100;
            point.y = 100;
        } else if (type.ordinal() == IMG_TYPE.TYPE_PREVIEW.ordinal()) {
            point.x = MyUtil.getPhoneWidthPx(context);
            point.y = MyUtil.getPhoneHeightPx(context);
        } else if (type.ordinal() == IMG_TYPE.TYPE_SHARPEST.ordinal()) {
            point.x = 0;
            point.y = 0;
        }
        return point;
    }


    public interface OnResponseListener {
        void onError(String errorMessage);
        void onResponse(String response);
    }

    public interface OnImageLoadingListener {
        void onFailure(String error);

        void onResponse(Bitmap bmp);

        void onLoadingProgressChanged(int percent);
    }
    public interface OnImageUploadingListener{
        void onFailure(String error);
        void onResponse(String response);
        void onUploadingProgressChanged(int percent);
    }

    public enum IMG_TYPE {
        TYPE_ICON, TYPE_PREVIEW, TYPE_SHARPEST
    }

    public enum BACKUP_TYPE {
        TYPE_PICURE("照片"), TYPE_SMS("短信"), TYPE_CONTACT("联系人");
        private String title;
        private  BACKUP_TYPE(String title){
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
