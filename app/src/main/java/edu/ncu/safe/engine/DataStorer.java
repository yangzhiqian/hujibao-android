package edu.ncu.safe.engine;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.ncu.safe.R;
import edu.ncu.safe.customerview.MyProgressBar;
import edu.ncu.safe.domain.ContactsInfo;
import edu.ncu.safe.domain.SmsInfo;
import edu.ncu.safe.domainadapter.ContactsAdapter;
import edu.ncu.safe.domainadapter.ITarget;
import edu.ncu.safe.domainadapter.MessageAdapter;
import edu.ncu.safe.external.okhttpprogress.ProgressHelper;
import edu.ncu.safe.external.okhttpprogress.ProgressRequestBody;
import edu.ncu.safe.external.okhttpprogress.UIProgressRequestListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Mr_Yang on 2016/7/5.
 */
public class DataStorer {
    public static final int TYPE_IMG = 0;
    public static final int TYPE_MESSAGE = 1;
    public static final int TYPE_CONTACT = 2;

    private static final String TAG = "DataLoader";
    private static final int TYPE_DATA_ERROR = 0;
    private static final int TYPE_DATA_SUCCEED = 1;
    private static final int TYPE_IMG_ERROR = 2;
    private static final int TYPE_IMG_SUCCEED = 3;

    private Context context;
    private final OkHttpClient client = new OkHttpClient();
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

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
                        imgListener.onSucceed((String) msg.obj);
                    }
                    break;
            }
            super.handleMessage(msg);

        }
    };


    public DataStorer(Context context) {
        this.context = context;
    }

    public void storeImg(String filePath, String url, String token, final MyProgressBar mpb) {
        File file = new File(filePath);
        if (file == null) {
            Message msg = Message.obtain();
            msg.what = TYPE_IMG_ERROR;
            msg.obj = "选择的文件为空！";
            myHandler.sendMessage(msg);
            return;
        }

        JSONObject object = new JSONObject();
        try {
            object.put("token", token);
            object.put("filename", file.getName());
            object.put("lastupdate", file.lastModified());
        } catch (Exception e) {

        }
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder.addFormDataPart("img", file.getName(), RequestBody.create(MEDIA_TYPE_PNG, file));
        builder.addFormDataPart("message", object.toString());
        MultipartBody requestBody = builder.build();

        //这个是ui线程回调，可直接操作UI
        final UIProgressRequestListener uiProgressRequestListener = new UIProgressRequestListener() {
            @Override
            public void onUIRequestProgress(long bytesWrite, long contentLength, boolean done) {
                Log.e("TAG", "bytesWrite:" + bytesWrite);
                Log.e("TAG", "contentLength" + contentLength);
                Log.e("TAG", (100 * bytesWrite) / contentLength + " % done ");
                Log.e("TAG", "done:" + done);
                Log.e("TAG", "================================");
                //ui层回调
                mpb.setPercentSlow((100.0f * bytesWrite) / contentLength);
            }
        };
        ProgressRequestBody progressRequestBody = ProgressHelper.addProgressRequestListener(requestBody, uiProgressRequestListener);
        //构建请求
        Request request = new Request.Builder()
                .url(url)//地址
                .post(progressRequestBody)//添加请求体
                .build();
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
                Message msg = Message.obtain();
                msg.what = TYPE_IMG_SUCCEED;
                msg.obj = response.body().string();
                myHandler.sendMessage(msg);
            }
        });
    }

    public String storeData(String token, ITarget data){
        List<ITarget> datas = new ArrayList<ITarget>();
        datas.add(data);
        return storeData(token,datas);
    }
    public String storeData(String token, List<ITarget> datas) {
        try {
            String message =  getMessage(token,datas);
            Log.e("TAG",message);
            FormBody body = new FormBody.Builder().add("message",message).build();
            String url = context.getResources().getString(R.string.storebackup);
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

        } catch (Exception e) {
            return e.getMessage();
        }
        return null;
    }

    private String getMessage(String token, List<ITarget> datas) throws Exception {
        JSONObject object = new JSONObject();
        object.put("token", token);
        int type=-1;

        if (datas.get(0) instanceof MessageAdapter) {
            type = TYPE_MESSAGE;
            List<SmsInfo> smsInfos = new ArrayList<SmsInfo>();
            for (ITarget iTarget : datas) {
                smsInfos.add((MessageAdapter) iTarget);
            }
            Log.e("TAG", new Gson().toJson(smsInfos));
            JSONArray array = new JSONArray( new Gson().toJson(smsInfos));
            object.accumulate("data",array);

        } else if (datas.get(0) instanceof ContactsAdapter) {
            type = TYPE_CONTACT;
            List<ContactsInfo> contactsInfos = new ArrayList<ContactsInfo>();
            for (ITarget iTarget : datas) {
                contactsInfos.add((ContactsAdapter) iTarget);
            }
            Log.e("TAG", new Gson().toJson(contactsInfos));
            JSONArray array = new JSONArray( new Gson().toJson(contactsInfos));
            object.accumulate("data", array);
        } else {
            throw new RuntimeException("数据类型异常！");
        }
        object.put("type", type);
        return object.toString();
    }


    private OnImgUploadedListener imgListener;
    private OnDataUploadedListener dataListener;

    public void setOnImgUploadedListener(OnImgUploadedListener imgListener) {
        this.imgListener = imgListener;
    }

    public void setOnDataUploadedListener(OnDataUploadedListener dataListener) {
        this.dataListener = dataListener;
    }

    public interface OnDataUploadedListener {
        void onFailure(String error);

        void onResponse(String response);
    }

    public interface OnImgUploadedListener {
        void onFailure(String error);

        void onSucceed(String message);
    }
}


