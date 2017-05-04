package edu.ncu.safe.engine;

import android.content.Context;
import android.os.Message;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import edu.ncu.safe.domainadapter.ITarget;

/**
 * Created by Yang on 2016/11/3.
 */

public abstract class AbstractCommBackUpDataOperator<DATA, DATAADAPTER extends ITarget> extends BackUpDataOperator<DATA, DATAADAPTER> {
    public static final int STORE_ERROR = 0;
    public static final int STORE_FAILURE = 1;
    public static final int STORE_SUCCEED = 2;
    public static final int LOAD_ERROR = 3;
    public static final int LOAD_SUCCEED = 4;
    public static final int DELETE_FAILURE = 5;
    public static final int DELETE_SUCCEED = 6;

    protected AbstractCommBackUpDataOperator(Context context, NetDataOperator.BACKUP_TYPE type) {
        super(context, type);
    }

    @Override
    public boolean handleCallBack(Message msg) {
        Object[] objects = (Object[]) msg.obj;
        switch (msg.what) {
            case STORE_ERROR:
                if (objects[0] != null) {
                    ((OnStoreDatasResponseListener<DATA>) objects[0]).onError((List<DATA>) objects[1], (String) objects[2]);
                }
                return true;
            case STORE_FAILURE:
                if (objects[0] != null) {
                    ((OnStoreDatasResponseListener<DATA>) objects[0]).onFailure((DATA) objects[1], (String) objects[2]);
                }
                return true;
            case STORE_SUCCEED:
                if (objects[0] != null) {
                    ((OnStoreDatasResponseListener<DATA>) objects[0]).onSucceed((DATA) objects[1], (String) objects[2]);
                }
                return true;
            case LOAD_ERROR:
                if (objects[0] != null) {
                    ((OnLoadDatasResponseListener<DATAADAPTER>) objects[0]).onFailure((String) objects[1]);
                }
                return true;
            case LOAD_SUCCEED:
                if (objects[0] != null) {
                    ((OnLoadDatasResponseListener<DATAADAPTER>) objects[0]).onDatasGet((List<DATAADAPTER>) objects[1], (Integer) objects[2]);
                }
                return true;
            case DELETE_FAILURE:
                if (objects[0] != null) {
                    ((OnDeleteDatasResponseListener) objects[0]).onFailure((int) objects[1], (String) objects[2]);
                }
                return true;
            case DELETE_SUCCEED:
                if (objects[0] != null) {
                    ((OnDeleteDatasResponseListener) objects[0]).onSucceed((int) objects[1], (String) objects[2]);
                }
                return true;
        }
        return false;
    }

    @Override
    public void storeDataToCloud(final DATA data, final OnStoreDatasResponseListener<DATA> listener) {
        List<DATA> datas = new ArrayList<DATA>();
        datas.add(data);
        storeDatasToCloud(datas, listener);
    }

    @Override
    public void storeDatasToCloud(final List<DATA> datas, final OnStoreDatasResponseListener<DATA> listener) {
        executor.execute(new Runnable() {
                             @Override
                             public void run() {
                                 try {
                                     JSONArray array = new JSONArray(new Gson().toJson(datas));
                                     operator.storeBackUpNormalDatas(type, array, new NetDataOperator.OnResponseListener() {
                                         @Override
                                         public void onError(String errorMessage) {
                                             Message message = Message.obtain();
                                             message.what = STORE_ERROR;
                                             Object[] os = new Object[3];
                                             os[0] = listener;
                                             os[1] = datas;
                                             os[2] = errorMessage;
                                             message.obj = os;
                                             handler.sendMessage(message);
                                         }
                                         @Override
                                         public void onResponse(String response) {
                                             try {
                                                 JSONObject object = new JSONObject(response);
                                                 int code = object.optInt("code", -1);
                                                 boolean succeed = object.optBoolean("succeed", false);
                                                 String message = object.optString("message", "错误信息");
                                                 if (succeed) {
                                                     object = new JSONObject(message);
                                                     int number = object.optInt("number", 0);
                                                     JSONArray messages = object.getJSONArray("msg");
                                                     JSONArray succeeds = object.getJSONArray("succeed");
                                                     for (int i = 0; i < number; i++) {
                                                         Message msg = Message.obtain();
                                                         msg.what = (boolean) succeeds.get(i) ? STORE_SUCCEED : STORE_FAILURE;
                                                         Object[] os = new Object[3];
                                                         os[0] = listener;
                                                         os[1] = datas.get(i);
                                                         os[2] = messages.get(i);
                                                         msg.obj = os;
                                                         handler.sendMessage(msg);
                                                     }
                                                 } else {
                                                     onError(message);
                                                 }
                                             } catch (JSONException e) {
                                                 onError( "服务器反馈数据异常！");
                                             }
                                         }
                                     });
                                 } catch (JSONException e) {
                                     Message message = Message.obtain();
                                     message.what = STORE_ERROR;
                                     Object[] os = new Object[3];
                                     os[0] = listener;
                                     os[1] = datas;
                                     os[2] = "数据有误！";
                                     message.obj = os;
                                     handler.sendMessage(message);
                                 }
                             }
                         }
        );
    }

    @Override
    public void loadCloudDatas(final int offset, final int size, final OnLoadDatasResponseListener<DATAADAPTER> listener) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                operator.loadBackUpDatas(type, offset, size, new NetDataOperator.OnResponseListener() {
                    @Override
                    public void onError(String errorMessage) {
                        Message message = Message.obtain();
                        message.what = LOAD_ERROR;
                        Object[] os = new Object[2];
                        os[0] = listener;
                        os[1] = errorMessage;
                        message.obj = os;
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.optBoolean("succeed", false)) {
                                List<DATAADAPTER> infos = new ArrayList<DATAADAPTER>();
                                JSONArray jsonArray = object.getJSONObject("message").getJSONArray("data");
                                JSONObject item = null;
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    infos.add(parseToDataAdapter(jsonArray.getJSONObject(i)));
                                }
                                Message message = Message.obtain();
                                message.what = LOAD_SUCCEED;
                                Object[] os = new Object[3];
                                os[0] = listener;
                                os[1] = infos;
                                os[2] = size;
                                message.obj = os;
                                handler.sendMessage(message);
                            } else {
                                onError("查询异常 +" + object.optInt("code", -1));
                            }
                        } catch (JSONException e) {
                            onError("服务器反馈数据异常！");
                        }
                    }
                });
            }
        });
    }


    @Override
    public void deleteDatasFromCloud(final List<Integer> ids, final OnDeleteDatasResponseListener listener) {
        for (Integer id : ids) {
            deleteDataFromCloud(id, listener);
        }
    }

    @Override
    public void deleteDataFromCloud(final int id, final OnDeleteDatasResponseListener listener) {
        operator.deleteBackup(type, id, new NetDataOperator.OnResponseListener() {
            @Override
            public void onError(String errorMessage) {
                Message message = Message.obtain();
                message.what = STORE_ERROR;
                Object[] os = new Object[3];
                os[0] = listener;
                os[1] = id;
                os[2] = errorMessage;
                message.obj = os;
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    Message msg = Message.obtain();
                    msg.what = object.optBoolean("succeed", false) ? DELETE_SUCCEED : DELETE_FAILURE;
                    Object[] os = new Object[3];
                    os[0] = listener;
                    os[1] = id;
                    os[2] = object.optString("message", msg.what + "");
                    msg.obj = os;
                    handler.sendMessage(msg);
                } catch (JSONException e) {
                    onError("服务器数据异常！");
                }
            }
        });
    }

    protected abstract DATAADAPTER parseToDataAdapter(JSONObject item);
}
