package edu.ncu.safe.engine;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import edu.ncu.safe.domain.ContactsInfo;
import edu.ncu.safe.domain.SmsInfo;
import edu.ncu.safe.domainadapter.ContactsAdapter;
import edu.ncu.safe.domainadapter.MessageAdapter;

/**
 * Created by Yang on 2016/10/29.
 */

public class PhoneContactCloudOperator extends AbstractCommBackUpDataOperator<ContactsInfo,ContactsAdapter> {
    public PhoneContactCloudOperator(Context context) {
        super(context, NetDataOperator.BACKUP_TYPE.TYPE_CONTACT);
    }

    @Override
    protected ContactsAdapter parseToDataAdapter(JSONObject item) {
        int id = item.optInt("cid",-1);
        String name = item.optString("name","name");
        String phoneNumber = item.optString("phoneNumber","phoneNumber");
        ContactsAdapter  info = new ContactsAdapter(new ContactsInfo(name, phoneNumber));
        info.setID(id);
        return info;
    }
//    private static Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            Object[] objects = (Object[]) msg.obj;
//            switch (msg.what){
//                case STORE_ERROR:
//                    if (objects[0] != null) {
//                        ((OnStoreDatasResponseListener<ContactsInfo>)objects[0]).onError((List<ContactsInfo>)objects[1], (String) objects[2]);
//                    }
//                    break;
//                case STORE_FAILURE:
//                    if (objects[0] != null) {
//                        ((OnStoreDatasResponseListener<ContactsInfo>)objects[0]).onFailure((ContactsInfo) objects[1], (String) objects[2]);
//                    }
//                    break;
//                case STORE_SUCCEED:
//                    if (objects[0] != null) {
//                        ((OnStoreDatasResponseListener<ContactsInfo>)objects[0]).onSucceed((ContactsInfo) objects[1], (String) objects[2]);
//                    }
//                    break;
//                case LOAD_ERROR:
//                    if (objects[0] != null) {
//                        ((OnLoadDatasResponseListener<MessageAdapter>)objects[0]).onFailure((String) objects[1]);
//                    }
//                    break;
//                case LOAD_SUCCEED:
//                    if (objects[0] != null) {
//                        ((OnLoadDatasResponseListener<MessageAdapter>)objects[0]).onDatasGet((List<MessageAdapter>) objects[1], (Integer) objects[2]);
//                    }
//                    break;
//                case DELETE_FAILURE:
//                    if (objects[0] != null) {
//                        ((OnDeleteDatasResponseListener)objects[0]).onFailure((int)objects[1],(String) objects[2]);
//                    }
//                    break;
//                case DELETE_SUCCEED:
//                    if (objects[0] != null) {
//                        ((OnDeleteDatasResponseListener)objects[0]).onSucceed((int)objects[1],(String) objects[2]);
//                    }
//                    break;
//            }
//            super.handleMessage(msg);
//        }
//
//    };
//
//    public PhoneContactCloudOperator(Context context) {
//        super(context, NetDataOperator.BACKUP_TYPE.TYPE_CONTACT);
//    }
//
//    @Override
//    public void storeDataToCloud(final ContactsInfo data, final OnStoreDatasResponseListener<ContactsInfo> listener) {
//        List<ContactsInfo> datas = new ArrayList<ContactsInfo>();
//        datas.add(data);
//        storeDatasToCloud(datas, listener);
//    }
//
//    @Override
//    public void storeDatasToCloud(final List<ContactsInfo> datas, final OnStoreDatasResponseListener<ContactsInfo> listener) {
//        executor.execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    JSONArray array = new JSONArray(new Gson().toJson(datas));
//                    operator.storeBackUpNormalDatas(type, array, new NetDataOperator.OnResponseListener() {
//                        @Override
//                        public void onFailure(String errorMessage) {
//                            Message message = Message.obtain();
//                            message.what = STORE_ERROR;
//                            Object[] os = new Object[3];
//                            os[0] = listener;
//                            os[1] = datas;
//                            os[2] = errorMessage;
//                            message.obj = os;
//                            handler.sendMessage(message);
//                        }
//
//                        @Override
//                        public void onResponse(String response) {
//                            try {
//                                JSONArray messages = new JSONObject(response).getJSONObject("message").getJSONArray("msg");
//                                JSONArray succeeds = new JSONObject(response).getJSONObject("message").getJSONArray("succeed");
//                                for (int i = 0; i < succeeds.length(); i++) {
//                                    Message msg = Message.obtain();
//                                    msg.what = (boolean) succeeds.get(i) ? STORE_SUCCEED : STORE_FAILURE;
//                                    Object[] os = new Object[3];
//                                    os[0] = listener;
//                                    os[1] = datas.get(i);
//                                    os[2] = messages.get(i);
//                                    msg.obj = os;
//                                    handler.sendMessage(msg);
//                                }
//                            } catch (JSONException e) {
//                                onFailure("服务器反馈数据异常！");
//                            }
//                        }
//                    });
//                } catch (JSONException e) {
//                    Message message = Message.obtain();
//                    message.what = STORE_ERROR;
//                    Object[] os = new Object[3];
//                    os[0] = listener;
//                    os[1] = datas;
//                    os[2] = "数据异常！";
//                    message.obj = os;
//                    handler.sendMessage(message);
//                }
//            }
//        });
//    }
//
//    @Override
//    public void loadCloudDatas(final int offset, final int size, final OnLoadDatasResponseListener listener) {
//        executor.execute(new Runnable() {
//            @Override
//            public void run() {
//                operator.loadBackUpDatas(type, offset, size, new NetDataOperator.OnResponseListener() {
//                    @Override
//                    public void onFailure(String errorMessage) {
//                        Message message = Message.obtain();
//                        message.what = LOAD_ERROR;
//                        Object[] os = new Object[2];
//                        os[0] = listener;
//                        os[1] = errorMessage;
//                        message.obj = os;
//                        handler.sendMessage(message);
//                    }
//
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            JSONObject object = new JSONObject(response);
//                            if (object.optBoolean("succeed", false)) {
//                                List<ContactsAdapter> infos = new ArrayList<ContactsAdapter>();
//                                JSONArray jsonArray = object.getJSONObject("message").getJSONArray("data");
//                                JSONObject item = null;
//                                ContactsAdapter info;
//                                for (int i = 0; i < jsonArray.length(); i++) {
//                                    item = jsonArray.getJSONObject(i);
//                                    int id = item.getInt("cid");
//                                    String name = item.getString("name");
//                                    String phoneNumber = item.getString("phoneNumber");
//                                    info = new ContactsAdapter(new ContactsInfo(name, phoneNumber));
//                                    info.setID(id);
//                                    infos.add(info);
//                                }
//                                Message message = Message.obtain();
//                                message.what = LOAD_SUCCEED;
//                                Object[] os = new Object[3];
//                                os[0] = listener;
//                                os[1] = infos;
//                                os[2] = size;
//                                message.obj = os;
//                                handler.sendMessage(message);
//                            } else {
//                                onFailure("查询异常 +" + object.optInt("code", -1));
//                            }
//                        } catch (JSONException e) {
//                            onFailure("服务器反馈数据异常！");
//                        }
//                    }
//                });
//            }
//        });
//    }
//
//    @Override
//    public void deleteDatasFromCloud(List<Integer> ids, OnDeleteDatasResponseListener<ContactsInfo> listener) {
//        for (Integer id : ids) {
//            deleteDataFromCloud(id, listener);
//        }
//    }
//
//    @Override
//    public void deleteDataFromCloud(final int id, final OnDeleteDatasResponseListener listener) {
//        operator.deleteBackup(type, id, new NetDataOperator.OnResponseListener() {
//            @Override
//            public void onFailure(String errorMessage) {
//                Message message = Message.obtain();
//                message.what = STORE_ERROR;
//                Object[] os = new Object[3];
//                os[0] = listener;
//                os[1] = id;
//                os[2] = errorMessage;
//                message.obj = os;
//                handler.sendMessage(message);
//            }
//
//            @Override
//            public void onResponse(String response) {
//                try {
//                    JSONObject object = new JSONObject(response);
//                    Message msg = Message.obtain();
//                    msg.what = object.optBoolean("succeed",false)?DELETE_SUCCEED:DELETE_FAILURE;
//                    Object[] os = new Object[3];
//                    os[0] = listener;
//                    os[1] = id;
//                    os[2] = object.optString("message",msg.what+"");
//                    msg.obj = os;
//                    handler.sendMessage(msg);
//                } catch (JSONException e) {
//                    onFailure("服务器数据异常！");
//                }
//            }
//        });
//    }
}
