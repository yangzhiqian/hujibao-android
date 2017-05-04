package edu.ncu.safe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import edu.ncu.safe.MyApplication;
import edu.ncu.safe.R;
import edu.ncu.safe.db.dao.CommunicationDatabase;
import edu.ncu.safe.domain.InterceptionInfo;
import edu.ncu.safe.engine.PhoneContactsOperator;
import edu.ncu.safe.engine.InterceptionJudger;
import edu.ncu.safe.engine.LocationFinder;
import edu.ncu.safe.ui.PhoneLostProtectActivity;

public class SmsReceiver extends BroadcastReceiver {

	private Context context;
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		// 获取信息数据.可能包含多条短信
		Object[] objs = (Object[]) intent.getExtras().get("pdus");
		for (Object obj : objs) {
			SmsMessage message = SmsMessage.createFromPdu((byte[]) obj);//一条短信
			if(dealWhiteByPhoneLostProtector(message)){//手机防盗已经处理的，其他的就不用处理了
				abortBroadcast();//不让短信下传
				continue;
			}
			if(dealWithCommunicationProtector(message)){//通话卫士的短信拦截
				//被拦截下了
				abortBroadcast();//不让短信下传
				continue;
			}
		}
	}

	/**
	 * 通话卫士的短信拦截
	 * @param message
	 * @return
	 */
	private boolean dealWithCommunicationProtector(SmsMessage message){
		CommunicationDatabase db = new CommunicationDatabase(context);
		String address = message.getOriginatingAddress();//短信号码
		String body = message.getMessageBody();//短信内容
		long time = System.currentTimeMillis();//当前时间
		String name = new PhoneContactsOperator(context).getContactName(address);
		int type = db.queryNumberType(address);//号码类型

		if(address.startsWith("+86")){
			address = address.substring(3);
		}

		InterceptionJudger judger = new InterceptionJudger(context);
		if(judger.isShouldSmsIntercepte(address)){
			//需要拦截
			InterceptionInfo info = new InterceptionInfo(-1, name, address, time, body, type);
			db.insertOneInterceptionMSGInfo(info);
			return true;
		}
		return false;
	}

	/**
	 * 手机防盗的监听
	 * @param message
	 */
	private boolean  dealWhiteByPhoneLostProtector(SmsMessage message){
		SharedPreferences sp = MyApplication.getSharedPreferences();
		final String number = message.getOriginatingAddress();// 获取发信人地址
		final String safeNumber = sp.getString(MyApplication.SP_STRING_SAFE_PHONE_NUMBER, "");
		boolean isInProtecting  = sp.getBoolean(MyApplication.SP_BOOLEAN_IS_IN_PROTECTING,false);
		if("".equals(safeNumber) || !number.equals(safeNumber)|| !isInProtecting){
			//未设置安全号码或者不是安全号码的短信或则手机没有开启保护，不处理
			return false;
		}
		String[] body = message.getMessageBody().split(" ");
		if(body.length<1){
			//空信息
			return false;
		}
		int id = matchOrder(body[0]);
		boolean isAdmin = isDeviceAdmin();//获取软件是否有设备管理权限
//		{"#*delete*#","#*lock*#","#*ring*#","#*pwd*#","#*location*#"};
		switch(id){
			case 0://重置
				if(sp.getBoolean(MyApplication.SP_BOOLEAN_IS_REMOTE_DELETE,false)){
					//开启了可以重置手机
					if(isAdmin){
						//手机恢复出厂设置
						DevicePolicyManager manager = (DevicePolicyManager) context
								.getSystemService(Context.DEVICE_POLICY_SERVICE);
						manager.wipeData(0);// 重置手机
						sendMessageNumber(context.getString(R.string.phone_lost_message_body_delete_ok), safeNumber);
					}else{
						sendMessageNumber(context.getString(R.string.error_no_device_admin), safeNumber);
					}
				}else{
					sendMessageNumber(context.getString(R.string.phone_lost_message_body_delete_error), safeNumber);
				}
				return true;
			case 1://锁频
				if(sp.getBoolean(MyApplication.SP_BOOLEAN_IS_REMOTE_LOCK,false)){
					//开启了可以锁住手机
					if(isAdmin){
						//锁住
						DevicePolicyManager manager = (DevicePolicyManager) context
								.getSystemService(Context.DEVICE_POLICY_SERVICE);
						String pwd = body.length > 1?body[1]:MyApplication.PHONE_LOST_DEFAULT_PWD;
						manager.resetPassword(pwd, 0);
						manager.lockNow();
						sendMessageNumber(context.getResources().getString(R.string.phone_lost_message_body_lock_ok) + pwd, safeNumber);
					}else{
						sendMessageNumber(context.getResources().getString(R.string.error_no_device_admin), safeNumber);
					}
				}else{
					sendMessageNumber(context.getResources().getString(R.string.phone_lost_message_body_lock_error), safeNumber);
				}
				return true;
			case 2://响铃
				if(sp.getBoolean(MyApplication.SP_BOOLEAN_IS_RING, false)){
					MediaPlayer player = MediaPlayer.create(context, R.raw.ring);
					player.setVolume(1.0f, 1.0f);
					player.start();
				}else{
					sendMessageNumber(context.getResources().getString(R.string.phone_lost_message_body_ring_error), safeNumber);
				}
				return true;
			case 3://修改密码
				if(sp.getBoolean(MyApplication.SP_BOOLEAN_IS_REMOTE_CHANGE_LOCK_PWD, false)){
					if(isAdmin){
						DevicePolicyManager manager = (DevicePolicyManager) context
								.getSystemService(Context.DEVICE_POLICY_SERVICE);
						String pwd = body.length > 1?body[1]:MyApplication.PHONE_LOST_DEFAULT_PWD;
						manager.resetPassword(pwd, 0);
						manager.lockNow();
						sendMessageNumber(context.getResources().getString(R.string.phone_lost_message_body_change_pwd_ok)+ pwd, safeNumber);
					}else{
						sendMessageNumber(context.getResources().getString(R.string.error_no_device_admin), safeNumber);
					}
				}else{
					sendMessageNumber(context.getResources().getString(R.string.phone_lost_message_body_change_pwd_error), safeNumber);
				}
				return true;
			case 4://定位
				if(sp.getBoolean(MyApplication.SP_BOOLEAN_IS_LOCATION, false)){

					LocationFinder finder = LocationFinder.getInstance(context);
					LocationFinder.LocationFinderListener listener = new LocationFinder.LocationFinderListener() {
						@Override
						public void onFail(String error) {
							sendMessageNumber(error,number);
						}

						@Override
						public void onStartLoacate(String type) {
							sendMessageNumber(context.getResources().getString(R.string.message_location_start_find),number);
						}

						@Override
						public void onLastLocationObtained(Location location) {
							if(location!=null) {
								String str = "\n经度:"+location.getLongitude()+"\n纬度:"+location.getLatitude()+"\n更新时间:"+ new SimpleDateFormat("yy/MM/dd HH:mm:ss").format(new Date(System.currentTimeMillis()));;
								sendMessageNumber(context.getResources().getString(R.string.message_location_last_location_obtained)+str, number);
							}
						}

						@Override
						public void onLocationObtained(Location location) {
							String str = "\n经度:"+location.getLongitude()+"\n纬度:"+location.getLatitude()+"\n更新时间:"+ new SimpleDateFormat("yy/MM/dd HH:mm:ss").format(new Date(System.currentTimeMillis()));;
							sendMessageNumber(context.getResources().getString(R.string.message_location_location_obtained)+str, number);
						}
					};
					finder.addLocationFinderListener(listener);
					finder.start();


				}else{
					sendMessageNumber(context.getResources().getString(R.string.phone_lost_message_body_location_error), safeNumber);
				}
				return true;
		}
		return false;
	}

	private int matchOrder(String order) {
		for(int i=0;i<PhoneLostProtectActivity.ORDERS.length;i++){
			if(order.equals(PhoneLostProtectActivity.ORDERS[i])){
				return i;
			}
		}
		return -1;
	}

	/**
	 * 直接调用短信接口发短信
	 */
	private void sendMessageNumber( String message,String number) {
		// 获取短信管理器
		SmsManager smsManager = SmsManager.getDefault();
		// 拆分短信内容（手机短信长度限制）
		List<String> divideContents = smsManager.divideMessage(message);
		for (String text : divideContents) {
			smsManager.sendTextMessage(number, null, text, null,
					null);
		}
	}
	private boolean isDeviceAdmin() {
		DevicePolicyManager manager = (DevicePolicyManager) context
				.getSystemService(Context.DEVICE_POLICY_SERVICE);
		ComponentName name = new ComponentName(context, AdminReceiver.class);
		return manager.isAdminActive(name);
	}
}
