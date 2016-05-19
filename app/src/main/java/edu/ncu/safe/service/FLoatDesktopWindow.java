package edu.ncu.safe.service;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import edu.ncu.safe.domain.TotalFlowsData;
import edu.ncu.safe.engine.LoadFlowsDataFromTrafficStats;
import edu.ncu.safe.engine.MyWindowManager;

public class FLoatDesktopWindow extends Service {

	protected static final String TAG = "FLoatDesktopWindow";

	private LoadFlowsDataFromTrafficStats trafficStats;
	private TotalFlowsData preTotalFlowsData;

	private MyWindowManager manager;

	private String packname;
	private boolean appPreState;

	private PendingIntent intentUpdate;
	private AlarmManager amUpdate;

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			try{
			TotalFlowsData currentInfo = trafficStats.getTotalFlowsData();
			int type = currentInfo.getNetType();
			long update = currentInfo.getUpdate()
					- preTotalFlowsData.getUpdate();
			long download = currentInfo.getDownload()
					- preTotalFlowsData.getDownload();
			manager.setData(type, update, download);
			preTotalFlowsData = currentInfo;
			}catch(Exception e){
				start();
			}
		};
	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(intent == null){
			start();
			return Service.START_STICKY;// 自动重启
		}
		String action = intent.getAction();
		//Log.i(TAG, action);
		
		// 执行更新操作
		if ("edu.ncu.safe.service.updateFloatWindod".equals(action)) {
			boolean appNowState = isRunning(this, packname);
			if (appPreState != appNowState) {
				stop();
				start();
				Log.i(TAG, "restart");
			}
			//Log.i(TAG, "update");
			appPreState = appNowState;
			handler.sendEmptyMessage(0);
		}
		// 执行开启操作
		if ("edu.ncu.safe.service.showFloatWindod".equals(action)) {
			// 开启浮动窗口
			start();
		}
		if ("edu.ncu.safe.service.stopFloatWindod".equals(action)) {
			stop();
		}
		return Service.START_STICKY;// 自动重启
	}

	private void init() {
		Log.i(TAG, "init");
		
		trafficStats = new LoadFlowsDataFromTrafficStats(this);
		preTotalFlowsData = trafficStats.getTotalFlowsData();
		manager = new MyWindowManager(this);
		packname = this.getApplication().getApplicationInfo().packageName;
		appPreState = isRunning(this, packname);
		
		// 开启定时更新
		Intent intent = new Intent(this, FLoatDesktopWindow.class);
		intent.setAction("edu.ncu.safe.service.updateFloatWindod");
		intentUpdate = PendingIntent.getService(this, 0, intent, 0);
		
		amUpdate = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
	}

	private void start() {
		Log.i(TAG, "start");
		// 开启浮动窗口
		init();
		manager.showView();
		
		long firstime = SystemClock.elapsedRealtime();
		// 1秒一个周期，不停的发送广播
		amUpdate.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstime,
				1000, intentUpdate);
	}
	private void stop() {
		Log.i(TAG, "stop");
		if (manager.isShow()) {
			manager.dismiss();
		}
		amUpdate.cancel(intentUpdate);
	}
	
	@Override
	public void onDestroy() {
		stop();
		super.onDestroy();
	}


	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * 判断指定包名的进程是否运行
	 * 
	 * @param context
	 * @param packageName
	 *            指定包名
	 * @return 是否运行
	 */
	public static boolean isRunning(Context context, String packageName) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
		for (RunningAppProcessInfo rapi : infos) {
			if (rapi.processName.equals(packageName))
				return true;
		}
		return false;
	}
}
