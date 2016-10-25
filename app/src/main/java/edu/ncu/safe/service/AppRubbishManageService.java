package edu.ncu.safe.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.StatFs;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

import edu.ncu.safe.R;
import edu.ncu.safe.domain.CacheInfo;

/**
 * Created by Mr_Yang on 2016/5/27.
 * <p>
 * 该服务可以后台扫描手机外部存储垃圾文件以及清理工作
 * </p>
 * 垃圾扫描</br>
 * 可用{@link android.content.Context#startService Context.startService()}
 * action = {R.string.Action_Service_AppExternalRubbishScannStart}启动扫描软件垃圾</br>
 * 可用{@link android.content.Context#startService Context.startService()}
 * action = {R.string.Action_Service_AppExternalRubbishScannCancel}取消扫描软件垃圾</br>
 * 对于扫描结果，服务会以广播的形式发送给手机应用，开发者可以使注册广播接收器接收扫描结果</br>
 * <p>结果如下:
 * <ol>
 * <li>action ={R.string.Action_Broadcast_AppExternalRubbishScannBegin}服务开始扫描应用垃圾，一次启动，只会有一次该广播事件，携带数据为扫描的总任务数量  可用intent.getExtras().getInteger("TotalTask")获取
 * <li>action ={R.string.Action_Broadcast_AppExternalRubbishScannEnd}服务取消或结束扫描应用垃圾，一次启动，只会有一次该广播事件，携带数据为扫描的取消或结束时已经完成了的垃圾扫描的量（B为单位）  可用intent.getExtras().getLong("TaskCancel")获取
 * <li>action ={R.string.Action_Broadcast_AppExternalRubbishScannProgressUpdated}服务扫描任务更新，一次启动，会有多次该广播事件，携带数据为当前扫描任务的index，扫描app的uid，扫描app的应用名  可用intent.getExtras().getInt("TaskIndex")、intent.getExtras().getInt("TaskAppUid")、intent.getExtras().getString("TaskAppName")获取
 * <li>action ={R.string.Action_Broadcast_AppExternalRubbishScannProgressEnd}服务扫描单个任务完成，一次启动，会有多次该广播事件，携带数据为扫描任务的结果 可用   CacheInfo info =  (CacheInfo) intent.getExtras().getSerializable("TaskProgressEnd");获得
 * </ol>
 * <p>
 * * 可用{@link android.content.Context#startService Context.startService()}
 * action = {R.string.Action_Service_AppExternalRubbishCleanStart}清理垃圾（只是向内存申请足够大的内存以至于系统自己触发清理缓存事件，该服务是申请无限大内存，所以只会让系统把所有的app的缓存清除掉）</br>
 * 对于扫描结果，服务会以广播的形式发送给手机应用，开发者可以使注册广播接收器接收扫描结果</br>
 * <p>结果如下:
 * <ol>
 * <li>action ={R.string.Action_Service_AppExternalRubbishCleanBegin}服务开始清除所有应用垃圾，一次启动，只会有一次该广播事件，无数据携带，只是通知开始了事件
 * <li>action ={R.string.Action_Service_AppExternalRubbishCleanAwait}事件已提交系统，等待系统执行结果，一次启动，只会有一次该广播事件，无数据携带，只是通知开始了事件
 * <li>action ={R.string.Action_Service_AppExternalRubbishCleanEnd}任务完成，一次启动，只会有一次该广播事件，携带数据为扫描任务的结果 可用   boolean res =  intent.getExtras().getBoolean("CleanResult");获得
 * </ol>
 */
public class AppRubbishManageService extends Service {
    private Method getPackageSizeInfoMethod;
    private Method freeStorageAndNotifyMethod;
    private AsyncTask scanTask;
    private AsyncTask cleanTask;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            getPackageSizeInfoMethod = getPackageManager().getClass().getMethod(
                    "getPackageSizeInfo", String.class, IPackageStatsObserver.class);

            freeStorageAndNotifyMethod = getPackageManager().getClass().getMethod(
                    "freeStorageAndNotify", long.class, IPackageDataObserver.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "启动外存垃圾管理服务异常！", Toast.LENGTH_SHORT).show();
            stopSelf();//停止服务
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (getString(R.string.Action_Service_AppExternalRubbishScannStart).equals(action)) {
            scanRubbish();
        } else if (getString(R.string.Action_Service_AppExternalRubbishScannCancel).equals(action)) {
            cancelScann();
        } else if (getString(R.string.Action_Service_AppExternalRubbishCleanStart).equals(action)) {
            cleanRubbish();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 外存垃圾扫描异步任务
     */
    private class RubbishScan extends AsyncTask<Void, Object, List<CacheInfo>> {
        public static final int TASKBEGIN = 0;//任务开始
        public static final int TASKPROGRESS = 1;//任务进度跟新
        public static final int TASKPROGRESSEND = 2;//单个任务完成
        public static final int TASKEND = 3;//所有任务结束
        public static final int TASKCANCEL = 4;//任务取消

        private boolean cancled = false;
        private CountDownLatch countDownLatch;//用与阻塞
        private List<CacheInfo> cacheInfos;
        private List<ApplicationInfo> packages;
        private PackageManager packageManager;
        private long cacheSize = 0;

        @Override
        protected List<CacheInfo> doInBackground(Void... params) {
            cacheInfos = new ArrayList<CacheInfo>();
            packageManager = getPackageManager();
            packages = packageManager.getInstalledApplications(
                    PackageManager.GET_META_DATA);//获取手机上的已安装应用
            countDownLatch = new CountDownLatch(packages.size());
            publishProgress(TASKBEGIN, packages.size());//通知任务数量
            try {
                int task = 0;
                for (ApplicationInfo applicationInfo : packages) {
                    if (cancled) {
                        publishProgress(TASKCANCEL, cacheSize);
                        return cacheInfos;
                    }
                    publishProgress(TASKPROGRESS, ++task, applicationInfo.uid, applicationInfo.loadLabel(packageManager));//推送当前任务
                    //用反射获取垃圾信息
                    getPackageSizeInfoMethod.invoke(packageManager, applicationInfo.packageName,
                            new IPackageStatsObserver.Stub() {
                                @Override
                                public void onGetStatsCompleted(PackageStats packageStats, boolean succeeded)
                                        throws RemoteException {

                                    if (succeeded && packageStats.cacheSize > 0) {
                                        try {
                                            //获取成功，拿到信息
                                            String packageName = packageStats.packageName;
                                            ApplicationInfo info = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
                                            String appName = info.loadLabel(packageManager).toString();
                                            Drawable icon = info.loadIcon(packageManager);
                                            long cacheSize = packageStats.cacheSize;
                                            CacheInfo cacheInfo = new CacheInfo(packageName, icon, appName, packageName, cacheSize);
                                            publishProgress(TASKPROGRESSEND, cacheInfo);
                                            cacheInfos.add(cacheInfo);
                                            RubbishScan.this.cacheSize += packageStats.cacheSize;
                                        } catch (PackageManager.NameNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    synchronized (countDownLatch) {
                                        countDownLatch.countDown();
                                    }
                                }
                            }
                    );
                }
                countDownLatch.await();
            } catch (InvocationTargetException | InterruptedException | IllegalAccessException e) {
                e.printStackTrace();
            }
            publishProgress(TASKEND, cacheSize);
            return cacheInfos;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            switch ((Integer) values[0]) {
                case TASKBEGIN:
                    intent.setAction(getString(R.string.Action_Broadcast_AppExternalRubbishScannBegin));
                    bundle.putInt("TotalTask", (Integer) values[1]);
                    break;
                case TASKPROGRESS:
                    intent.setAction(getString(R.string.Action_Broadcast_AppExternalRubbishScannProgressUpdated));
                    bundle.putInt("TaskIndex", (Integer) values[1]);
                    bundle.putInt("TaskAppUid", (Integer) values[2]);
                    bundle.putString("TaskAppName", (String) values[3]);
                    break;
                case TASKPROGRESSEND:
                    intent.setAction(getString(R.string.Action_Broadcast_AppExternalRubbishScannProgressEnd));
                    bundle.putParcelable("TaskProgressEnd", (CacheInfo) values[1]);
                    break;
                case TASKEND:
                case TASKCANCEL:
                    intent.setAction(getString(R.string.Action_Broadcast_AppExternalRubbishScannEnd));
                    bundle.putLong("TaskEnd", (Long) values[1]);
                    scanTask = null;
                    break;
            }
            intent.putExtras(bundle);
            sendBroadcast(intent);//发送广播
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            cancled = true;
        }
    }

    private class RubbishClean extends AsyncTask<Void, Integer, Void> {
        public static final int TASKSTART = 0;//开始前
        public static final int TASKAWAIT = 1;//已经提交，阻塞执行
        public static final int TASKEND = 2;//执行结束
        private boolean res = false;

        @Override
        protected Void doInBackground(Void... params) {
            publishProgress(TASKSTART);
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            StatFs stat = new StatFs(Environment.getDataDirectory().getAbsolutePath());
            try {
                freeStorageAndNotifyMethod.invoke(getPackageManager(),
                        (long) stat.getBlockCount() * (long) stat.getBlockSize(),
                        new IPackageDataObserver.Stub() {
                            @Override
                            public void onRemoveCompleted(String packageName, boolean succeeded)
                                    throws RemoteException {
                                res = succeeded;
                                countDownLatch.countDown();
                            }
                        }
                );
                publishProgress(TASKAWAIT);
                countDownLatch.await();

            } catch (InvocationTargetException | InterruptedException | IllegalAccessException e) {
                e.printStackTrace();
                res = false;
            }
            publishProgress(TASKEND);
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            switch (values[0]) {
                case TASKSTART:
                    intent.setAction(getString(R.string.Action_Broadcast_AppExternalRubbishCleanBegin));
                    break;
                case TASKAWAIT:
                    intent.setAction(getString(R.string.Action_Broadcast_AppExternalRubbishCleanAwait));
                    break;
                case TASKEND:
                    intent.setAction(getString(R.string.Action_Broadcast_AppExternalRubbishCleanEnd));
                    bundle.putBoolean("CleanResult", res);
                    cleanTask = null;
                    break;
            }
            intent.putExtras(bundle);
            getApplicationContext().sendBroadcast(intent);
        }
    }

    public synchronized void scanRubbish() {
        if (scanTask != null) {
            Intent intent = new Intent(getString(R.string.Action_Broadcast_AppExternalRubbishScannInDoing));
            getApplicationContext().sendBroadcast(intent);
            return;
        }
        scanTask = new RubbishScan().execute();
    }

    public synchronized void cancelScann() {
        if (scanTask != null) {
            scanTask.cancel(false);
        }
    }

    public synchronized void cleanRubbish() {
        if (cleanTask != null) {
            Intent intent = new Intent(getString(R.string.Action_Broadcast_AppExternalRubbishCleanInDoing));
            getApplicationContext().sendBroadcast(intent);
            return;
        }
        cleanTask = new RubbishClean().execute();

    }
}
