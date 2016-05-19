package edu.ncu.safe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.sax.StartElementListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import edu.ncu.safe.service.FLoatDesktopWindow;
import edu.ncu.safe.ui.PhoneLostProtectActivity;

public class PhoneRestartReceiver extends BroadcastReceiver {
	private static final String TAG = "PhoneRestartReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "PhoneRestartReceiver");
		//开启流量服务
		startFlowsRecorderService(context);
		//检测手机号码
		checkThePhoneUser(context);
		//开启浮动窗口
		showFloatWindow(context);
	}

	private void startFlowsRecorderService(Context context) {
		Intent flowsService = new Intent();
		flowsService.setAction("edu.ncu.myservice.flowsrecorder");
		context.startService(flowsService);
	}
	
	private void showFloatWindow(Context context){
		Intent intent = new Intent();
		intent.setAction("edu.ncu.safe.service.showFloatWindod");
		context.startService(intent);
	}

	private void checkThePhoneUser(Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				PhoneLostProtectActivity.SHAREPERFERENCESNAME,
				context.MODE_PRIVATE);
		boolean isInProtecting = sp.getBoolean(
				PhoneLostProtectActivity.ISINPROTECTING, false);
		if (!isInProtecting) {
			return;
		}

		TelephonyManager manager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String phoneNumber = manager.getLine1Number();
		if (phoneNumber == null || "".equals(phoneNumber)) {
			return;
		}

		String userNumber = sp.getString(
				PhoneLostProtectActivity.USERPHONENUMBER, null);
		if (userNumber == null) {
			return;
		}

		if (userNumber.equals(phoneNumber)) {
			return;
		}

		String safeNumber = sp.getString(
				PhoneLostProtectActivity.SAFEPHONENUMBER, null);
		if (safeNumber == null) {
			return;
		}

		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(safeNumber, null, userNumber + "的手机正在被"
				+ phoneNumber + "的号码使用,原机主可能已经被盗！", null, null);
	}
}
