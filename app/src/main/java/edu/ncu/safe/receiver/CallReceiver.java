package edu.ncu.safe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import edu.ncu.safe.db.dao.CommunicationDatabase;
import edu.ncu.safe.domain.InterceptionInfo;
import edu.ncu.safe.engine.InterceptionJudger;
import edu.ncu.safe.engine.ContactsService;

/**
 * Created by Mr_Yang on 2016/5/18.
 */
public class CallReceiver extends BroadcastReceiver {
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        String action = intent.getAction();
        System.out.println(action);
        Toast.makeText(context, action, Toast.LENGTH_SHORT).show();
        //呼入电话
        if (action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            String number = intent.getStringExtra(
                    TelephonyManager.EXTRA_INCOMING_NUMBER);
            TelephonyManager telephony =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            int state = telephony.getCallState();
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    //等待接电话
                    if (dealWithCommunicationProtector(number)) {
                        abortBroadcast();
                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    //电话挂断
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //通话中
                    break;
            }
        }
    }

    private boolean dealWithCommunicationProtector(String number) {
        InterceptionJudger judger = new InterceptionJudger(context);
        if (judger.isShouldPhoneIntercepte(number)) {
            //需要被拦截
            CommunicationDatabase db = new CommunicationDatabase(context);
            ContactsService contacts = new ContactsService(context);

            String name = contacts.getContactName(number);
            int type = db.queryNumberType(number);
            long time = System.currentTimeMillis();

            InterceptionInfo info = new InterceptionInfo(-1, name, number, time, null, type);
            db.insertOneInterceptionPhoneInfo(info);
            return true;
        }
        return false;
    }

//    private void stopCall() {
//        try {
//            //Android的设计将ServiceManager隐藏了，所以只能使用反射机制获得。
//            Method method = Class.forName("android.os.ServiceManager").getMethod("getService", String.class);
//            IBinder binder = (IBinder) method.invoke(null, new Object[]{"phone"});
//            //获得系统电话服务
//            ITelephony telephoney = ITelephony.Stub.asInterface(binder);
//            telephoney.endCall();//挂断电话
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
////
//    public void endPhone() {
//        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        try {
//            Method getITelephonyMethod = TelephonyManager.class.getDeclaredMethod("getITelephony", (Class[]) null);
//            getITelephonyMethod.setAccessible(true);
//            ITelephony iTelephony = (ITelephony) getITelephonyMethod.invoke(manager,
//
//                    (Object[]) null);
//            // 挂断电话
//            iTelephony.endCall();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}