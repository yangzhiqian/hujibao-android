package edu.ncu.safe.engine;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import edu.ncu.safe.domain.ImageInfo;
import edu.ncu.safe.domainadapter.ImageAdapter;

/**
 * Created by Yang on 2016/11/2.
 */

public class PhonePhotoCloudOperator extends AbstractCommBackUpDataOperator<ImageInfo,ImageAdapter> {
    public static final int STORE_PROGRESS = 7;


    public  PhonePhotoCloudOperator(Context context) {
        super(context, NetDataOperator.BACKUP_TYPE.TYPE_PICURE);
    }

    @Override
    public boolean handleCallBack(Message msg) {
        Object[] objects = (Object[]) msg.obj;
        switch (msg.what){
            case STORE_PROGRESS:
                if(objects[0]!=null) {
                    ((OnStoreDatasResponseListener<ImageInfo>)objects[0]).onProgressUpdated((ImageInfo) objects[1], (Integer) objects[2]);
                }
                return true;
        }
        return super.handleCallBack(msg);
    }

    @Override
    public void storeDataToCloud(final ImageInfo data, final OnStoreDatasResponseListener<ImageInfo> listener) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    operator.storeBackUpImgDatas(data.getPath(), new NetDataOperator.OnImageUploadingListener() {
                        @Override
                        public void onFailure(String error) {
                            Message msg = Message.obtain();
                            msg.what = STORE_FAILURE;
                            Object[] os = new Object[3];
                            os[0] = listener;
                            os[1] = data;
                            os[2] = error;
                            msg.obj = os;
                            handler.sendMessage(msg);
                        }

                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject object = new JSONObject(response);
                                Message msg = Message.obtain();
                                Object[] os = new Object[3];
                                os[0] = listener;
                                os[1] = data;
                                os[2] = object.opt("message");
                                if (object.optBoolean("succeed", false)) {
                                    msg.what = STORE_SUCCEED;
                                } else {
                                    msg.what = STORE_FAILURE;
                                    os[2] += (object.optInt("code",-1)+"");
                                }
                                msg.obj = os;
                                handler.sendMessage(msg);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                onFailure("服务器反馈数据错误!");
                            }
                        }

                        @Override
                        public void onUploadingProgressChanged(int percent) {
                            Message msg = Message.obtain();
                            msg.what = STORE_PROGRESS;
                            Object[] os = new Object[3];
                            os[0] = listener;
                            os[1] = data;
                            os[2] = percent;
                            msg.obj = os;
                            handler.sendMessage(msg);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected ImageAdapter parseToDataAdapter(JSONObject item) {
        int id = item.optInt("pid",-1);
        long lastModified = item.optLong("lastModified",System.currentTimeMillis());
        String name = item.optString("name","PictureName");
        int size = item.optInt("size",-1);
        ImageAdapter info = new ImageAdapter(new ImageInfo(name,name,lastModified,size));
        info.setID(id);
        return info;
    }
}
