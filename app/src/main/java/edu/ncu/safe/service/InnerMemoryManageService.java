package edu.ncu.safe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import edu.ncu.safe.R;
import edu.ncu.safe.domain.RunningApplicationInfo;
import edu.ncu.safe.external.runningappinfo.ProcessManager;

/**
 * Created by Mr_Yang on 2016/5/27.
 * <p>
 * 该服务可以后台扫描手机外部存储垃圾文件以及清理工作
 * </p>
 * 垃圾扫描</br>
 * 可用{@link android.content.Context#startService Context.startService()}
 * action = {R.string.Action_Service_InnerMemoryRubbishScannStart}启动扫描软件内存垃圾</br>
 * 可用{@link android.content.Context#startService Context.startService()}
 * action = {R.string.Action_Service_InnerMemoryRubbishScannCancel}取消扫描软件内存垃圾</br>
 * 对于扫描结果，服务会以广播的形式发送给手机应用，开发者可以使注册广播接收器接收扫描结果</br>
 * <p>结果如下:
 * <ol>
 * <li>action ={R.string.Action_Broadcast_InnerMemoryRubbishScannBegin}服务开始扫描应用内存垃圾，一次启动，只会有一次该广播事件，携带数据为扫描的总任务数量  可用intent.getExtras().getInteger("TotalTask")获取
 * <li>action ={R.string.Action_Broadcast_InnerMemoryRubbishScannInDoing}服务开始扫描应用内存垃圾，一次启动，只会有一次该广播事件，表明该任务已经正在运行
 * <li>action ={R.string.Action_Broadcast_InnerMemoryRubbishScannProgressUpdated}服务扫描内存垃圾任务更新，一次启动，会有多次该广播事件，携带数据为当前扫描任务的index，扫描app的uid，扫描app的应用名  可用intent.getExtras().getInt("TaskIndex")、intent.getExtras().getInt("TaskAppUid")、intent.getExtras().getString("TaskAppName")获取
 * <li>action ={R.string.Action_Broadcast_InnerMemoryRubbishScannProgressEnd}服务扫描单个内存垃圾任务完成，一次启动，会有多次该广播事件，携带数据为扫描任务的结果 可用   CacheInfo info =  (CacheInfo) intent.getExtras().getSerializable("TaskProgressEnd");获得
 * <li>action ={R.string.Action_Broadcast_InnerMemoryRubbishScannEnd}服务取消或结束扫描应用内存垃圾，一次启动，会有一次该广播事件，携带数据为扫描的取消或结束时已经完成了的垃圾扫描的量（B为单位）  可用intent.getExtras().getLong("TaskEnd")获取
 * </ol>
 * <p>
 * * 可用{@link android.content.Context#startService Context.startService()}
 * action = {R.string.Action_Service_InnerMemoryRubbishCleanStart}清理垃圾（只是向内存申请足够大的内存以至于系统自己触发清理缓存事件，该服务是申请无限大内存，所以只会让系统把所有的app的缓存清除掉）</br>
 * 需要携带参数，可在bundle中putSerializable(List<String>()),添加要清除的进程名
 * 对于扫描结果，服务会以广播的形式发送给手机应用，开发者可以使注册广播接收器接收扫描结果</br>
 * <p>结果如下:
 * <ol>
 * <li>action ={R.string.Action_Broadcast_InnerMemoryRubbishCleanBegin}服务开始清除所有应用垃圾，一次启动，只会有一次该广播事件，携带了任务的总数量 可用 intent.getExtras.getInt("TotalTask")获取
 * <li>action ={R.string.Action_Broadcast_InnerMemoryRubbishCleanInDoing}事件已提交系统，等待系统执行结果，一次启动，只会有一次该广播事件，无数据携带，只是通知开始了事件
 * <li>action ={R.string.Action_Broadcast_InnerMemoryRubbishCleanProgressUpdated}单个任务开始，一次启动，会有多次该广播事件，携带数据为扫描任务的序号和名称 可用   intent.getExtras().getInt("TaskIndex")  intent.getExtras().getString("TaskAppName")获得
 * <li>action ={R.string.Action_Broadcast_InnerMemoryRubbishCleanProgressEnd}单个任务结束，一次启动，会有多次该广播事件,携带数据为扫描任务的名称及结果 可用   intent.getExtras().getString("TaskAppName"); intent.getExtras().getBoolean("TaskAppResult");获得
 * <li>action ={R.string.Action_Broadcast_InnerMemoryRubbishCleanEnd}任务完成，一次启动，只会有一次该广播事件，没带数据
 * </ol>
 */
public class InnerMemoryManageService extends Service {
    private AsyncTask scanTask;
    private AsyncTask cleanTask;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (getString(R.string.Action_Service_InnerMemoryRubbishScannStart).equals(action)) {
            runScanner();
        } else if (getString(R.string.Action_Service_InnerMemoryRubbishScannCancel).equals(action)) {
            cancelScan();
        } else if (getString(R.string.Action_Service_InnerMemoryRubbishCleanStart).equals(action)) {
            runClean((List<String>) intent.getExtras().getSerializable("CleanProcessNames"));
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public synchronized void runScanner() {
        if (scanTask != null) {
            Intent intent = new Intent(getString(R.string.Action_Broadcast_InnerMemoryRubbishScannInDoing));
            sendBroadcast(intent);
            return;
        }
        scanTask = new InnerMemoryScanTask().execute();
    }

    public synchronized void cancelScan() {
        if (scanTask != null) {
            scanTask.cancel(false);
        }
    }

    public void runClean(List<String> processNames) {

        if (cleanTask != null) {
            Intent intent = new Intent(getString(R.string.Action_Broadcast_InnerMemoryRubbishCleanInDoing));
            sendBroadcast(intent);
            return;
        }
        cleanTask = new MyCleanTask().execute(processNames);

    }

    class InnerMemoryScanTask extends AsyncTask<Void, Object, List<RunningApplicationInfo>> {
        public static final int TASKBEGIN = 0;//任务开始
        public static final int TASKPROGRESS = 1;//任务进度跟新
        public static final int TASKPROGRESSEND = 2;//单个任务完成
        public static final int TASKEND = 3;//所有任务结束
        public static final int TASKCANCEL = 4;//任务取消
        private boolean cancled = false;

        @Override
        protected List<RunningApplicationInfo> doInBackground(Void... params) {
            List<RunningApplicationInfo> infos = new ArrayList<RunningApplicationInfo>();//结果集
            PackageManager packageManager = getApplicationContext().getPackageManager();//pm
            ActivityManager activityManger = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = Build.VERSION.SDK_INT >= 21 ? ProcessManager.getRunningAppProcessInfo(getApplicationContext()) : activityManger.getRunningAppProcesses();
            publishProgress(TASKBEGIN, runningAppProcesses.size());//推送总量

            long size = 0;
            int index = 0;
            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
                if (cancled) {
                    publishProgress(TASKCANCEL, size);
                    return infos;
                }
                //获取进程的pid，uid,memory,processName
                String processName = runningAppProcessInfo.processName;
                int uid = runningAppProcessInfo.uid;
                int pid = runningAppProcessInfo.pid;
                publishProgress(TASKPROGRESS, ++index, uid, processName);//推送进度
                if (processName.contains(getPackageName())) {
                    continue;//取消自己的应用程序
                }
                String appName = processName;
                Drawable icon = null;
                int memory = activityManger.getProcessMemoryInfo(new int[]{pid})[0].getTotalPrivateDirty() * 1024;
                ApplicationInfo applicationInfo;
                int type = RunningApplicationInfo.PROCESS_TYPE_PROCESS;
                try {
                    applicationInfo = packageManager.getApplicationInfo(processName, 0);
                    if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                        continue;//系统应用
                    }
                    appName = applicationInfo.loadLabel(packageManager).toString();
                    icon = applicationInfo.loadIcon(packageManager);
                } catch (PackageManager.NameNotFoundException e) {
                    if (processName.indexOf(":") != -1) {// :服务的命名
                        try {
                            applicationInfo = packageManager.getApplicationInfo(processName.split(":")[0], 0);
                            icon = applicationInfo != null ? applicationInfo.loadIcon(packageManager) : null;
                            appName = processName;
                            type = RunningApplicationInfo.PROCESS_TYPE_SERVICE;
                            continue;
                        } catch (PackageManager.NameNotFoundException e1) {
                        }
                    }
                    appName = processName;
                } finally {
                    if(icon!=null) {
                        RunningApplicationInfo info = new RunningApplicationInfo(pid, uid, processName, icon, appName, type, memory);
                        publishProgress(TASKPROGRESSEND, info);
                        infos.add(info);
                        size += info.getCacheSize();
                    }
                }
            }
            publishProgress(TASKEND, size);
            return infos;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            cancled = true;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            switch ((int) values[0]) {
                case TASKBEGIN:
                    intent.setAction(getString(R.string.Action_Broadcast_InnerMemoryRubbishScannBegin));
                    bundle.putInt("TotalTask", (int) values[1]);
                    break;
                case TASKPROGRESS:
                    intent.setAction(getString(R.string.Action_Broadcast_InnerMemoryRubbishScannProgressUpdated));
                    bundle.putInt("TaskIndex", (Integer) values[1]);
                    bundle.putInt("TaskAppUid", (Integer) values[2]);
                    bundle.putString("TaskAppName", (String) values[3]);
                    break;
                case TASKPROGRESSEND:
                    intent.setAction(getString(R.string.Action_Broadcast_InnerMemoryRubbishScannProgressEnd));
                    bundle.putParcelable("TaskProgressEnd", (RunningApplicationInfo) values[1]);
                    break;
                case TASKCANCEL:
                case TASKEND:
                    intent.setAction(getString(R.string.Action_Broadcast_InnerMemoryRubbishScannEnd));
                    bundle.putLong("TaskEnd", (Long) values[1]);
                    scanTask = null;
                    break;
            }
            intent.putExtras(bundle);
            sendBroadcast(intent);
        }
    }


    class MyCleanTask extends AsyncTask<List<String>, Object, Void> {
        public static final int TASKBEGIN = 0;//任务开始
        public static final int TASKPROGRESS = 1;//任务进度跟新
        public static final int TASKPROGRESSEND = 2;//单个任务完成
        public static final int TASKEND = 3;//所有任务结束

        @Override
        protected Void doInBackground(List<String>... params) {
            List<String> processNames = params[0];
            publishProgress(TASKBEGIN, processNames.size());
            int index = 0;
            for (String processName : processNames) {
                publishProgress(TASKPROGRESS, ++index, processName);
                publishProgress(TASKPROGRESSEND, processName, killProcess(processName));
            }
            publishProgress(TASKEND);
            return null;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            switch ((Integer) values[0]) {
                case TASKBEGIN:
                    intent.setAction(getString(R.string.Action_Broadcast_InnerMemoryRubbishCleanBegin));
                    bundle.putInt("TotalTask", (Integer) values[1]);
                    break;
                case TASKPROGRESS:
                    intent.setAction(getString(R.string.Action_Broadcast_InnerMemoryRubbishCleanProgressUpdated));
                    bundle.putInt("TaskIndex", (Integer) values[1]);
                    bundle.putString("TaskAppName", (String) values[2]);
                    break;
                case TASKPROGRESSEND:
                    intent.setAction(getString(R.string.Action_Broadcast_InnerMemoryRubbishCleanProgressEnd));
                    bundle.putString("TaskAppName", (String) values[1]);
                    bundle.putBoolean("TaskAppResult", (Boolean) values[2]);
                    break;
                case TASKEND:
                    intent.setAction(getString(R.string.Action_Broadcast_InnerMemoryRubbishCleanEnd));
                    cleanTask = null;
                    break;
            }
            intent.putExtras(bundle);
            getApplicationContext().sendBroadcast(intent);//发送广播
        }

        public boolean killProcess(String processName) {
            String packageName = null;
            ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            try {
                if (processName.indexOf(":") == -1) {
                    packageName = processName;
                } else {
                    packageName = processName.split(":")[0];
                }
                manager.killBackgroundProcesses(packageName);
                Method forceStopPackage = manager.getClass()
                        .getDeclaredMethod("forceStopPackage", String.class);
                forceStopPackage.setAccessible(true);
                forceStopPackage.invoke(manager, packageName);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }
}
