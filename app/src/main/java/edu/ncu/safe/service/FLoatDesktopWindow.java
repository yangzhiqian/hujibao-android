package edu.ncu.safe.service;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import edu.ncu.safe.R;
import edu.ncu.safe.domain.TotalFlowsData;
import edu.ncu.safe.engine.LoadFlowsDataFromTrafficStats;
import edu.ncu.safe.engine.MyWindowManager;
import edu.ncu.safe.util.MyLog;

public class FLoatDesktopWindow extends Service {

    protected static final String TAG = "FLoatDesktopWindow";

    private LoadFlowsDataFromTrafficStats trafficStats;
    private TotalFlowsData preTotalFlowsData;

    private MyWindowManager manager;

    private String packname;
    private boolean appPreState;
    private Timer timer;

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            try {
                TotalFlowsData currentInfo = trafficStats.getTotalFlowsData();
                int type = currentInfo.getNetType();
                long update = currentInfo.getUpdate()
                        - preTotalFlowsData.getUpdate();
                long download = currentInfo.getDownload()
                        - preTotalFlowsData.getDownload();
                manager.setData(type, update, download);
                preTotalFlowsData = currentInfo;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ;
    };

    public static void startService(Context context) {
        Intent floatWindowIntent = new Intent();
        floatWindowIntent.setClass(context, FLoatDesktopWindow.class);
        floatWindowIntent.setAction(context.getResources().getString(R.string.action_float_window_show));
        context.startService(floatWindowIntent);
    }

    public static void stopService(Context context) {
        Intent floatWindowIntent = new Intent();
        floatWindowIntent.setClass(context, FLoatDesktopWindow.class);
        floatWindowIntent.setAction(context.getResources().getString(R.string.action_float_window_dismiss));
        context.startService(floatWindowIntent);
    }

    public static void update(Context context) {
        Intent floatWindowIntent = new Intent();
        floatWindowIntent.setClass(context, FLoatDesktopWindow.class);
        floatWindowIntent.setAction(context.getResources().getString(R.string.action_float_window_update));
        context.startService(floatWindowIntent);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            MyLog.i(TAG, "intent==null");
            start();
            return Service.START_STICKY;// 自动重启
        }
        String action = intent.getAction();
        MyLog.i(TAG, "action:::::::" + action);
        // 执行更新操作
        if (getResources().getString(R.string.action_float_window_update).equals(action)) {
            boolean appNowState = isRunning(this, packname);
            if (appPreState != appNowState) {
                stop();
                start();
                MyLog.i(TAG, "restart");
            }
            appPreState = appNowState;
            handler.sendEmptyMessage(0);
        }
        // 执行开启操作
        if (getResources().getString(R.string.action_float_window_show).equals(action)) {
            // 开启浮动窗口
            start();
        }
        if (getResources().getString(R.string.action_float_window_dismiss).equals(action)) {
            stop();
        }
        return Service.START_STICKY;// 自动重启
    }

    private void init() {
        MyLog.i(TAG, "init");
        if (trafficStats == null) {
            trafficStats = new LoadFlowsDataFromTrafficStats(this);
        }
        preTotalFlowsData = trafficStats.getTotalFlowsData();
        if (manager == null) {
            manager = new MyWindowManager(this);
        }
        packname = this.getApplication().getApplicationInfo().packageName;
        appPreState = isRunning(this, packname);
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
    }

    private void start() {
        MyLog.i(TAG, "start");
        // 开启浮动窗口
        init();
        manager.showView();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                update(getApplicationContext());
            }
        }, 1000, 1000);
    }

    private void stop() {
        MyLog.i(TAG, "stop");
        if (manager != null && manager.isShow()) {
            manager.dismiss();
        }
        if (timer != null) {
            timer.cancel();
        }
        timer = null;
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
     * @param packageName 指定包名
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
