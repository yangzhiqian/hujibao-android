package edu.ncu.safe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import edu.ncu.safe.R;
import edu.ncu.safe.domain.RunningApplicationInfo;

/**
 * Created by Mr_Yang on 2016/5/27.
 */
public class InnerMemoryCleanService extends Service {
    private Context context;
    private MyBinder binder;
    private OnMemoryDataGetListener listener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        binder = new MyBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public class MyBinder extends Binder {
        public InnerMemoryCleanService getService() {
            return InnerMemoryCleanService.this;
        }
    }

    public interface OnMemoryDataGetListener {
        public void onMemoryScanTaskStart(int sumTask);

        public void onMemoryScanProgressChanged(String tastName, int progress);

        public void onMemoryScanTaskEnded(List<RunningApplicationInfo> infos);

        public void onMemoryCleanTaskStart(int sumTask);

        public void onMemoryCleanProgressChanged(String tastName, int progress);

        public void onMemoryCleanTaskEnded(int res);
    }

    public void setOnMemoryDataGetListener(OnMemoryDataGetListener listener) {
        this.listener = listener;
    }

    public void runScanner() {
        new InnerMemoryScanTask().execute();
    }

    class InnerMemoryScanTask extends AsyncTask<Void, Object, List<RunningApplicationInfo>> {

        @Override
        protected List<RunningApplicationInfo> doInBackground(Void... params) {
            List<RunningApplicationInfo> infos = new ArrayList<RunningApplicationInfo>();

            PackageManager packageManager = context.getPackageManager();
            ActivityManager activityManger = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManger.getRunningAppProcesses();
            publishProgress(runningAppProcesses.size());//推送总量
            RunningApplicationInfo info;

            int index = 0;
            ApplicationInfo applicationInfo;
            String processName;
            Drawable icon;
            String appName;
            int uid;
            int pid;
            int memory;

            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
                processName = runningAppProcessInfo.processName;
                publishProgress(processName, ++index);//推送进度
                if (processName.contains(getPackageName())) {
                    continue;
                }
                uid = runningAppProcessInfo.uid;
                pid = runningAppProcessInfo.pid;
                memory = activityManger.getProcessMemoryInfo(new int[]{pid})[0].getTotalPrivateDirty() * 1024;
                try {
                    applicationInfo = packageManager.getApplicationInfo(processName, 0);
                    if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                        continue;//系统应用
                    }
                    appName = applicationInfo.loadLabel(packageManager).toString();
                    icon = applicationInfo.loadIcon(packageManager);
                    info = new RunningApplicationInfo(processName, appName, icon, pid, uid, memory, RunningApplicationInfo.PROCESS_TYPE_PRPCESS);
                    infos.add(info);
                } catch (PackageManager.NameNotFoundException e) {
                    // :服务的命名
                    if (processName.indexOf(":") != -1) {
                        try {
                            applicationInfo = packageManager.getApplicationInfo(processName.split(":")[0], 0);
                            icon = applicationInfo != null ? applicationInfo.loadIcon(packageManager) : context.getResources().getDrawable(R.drawable.ic_launcher);
                            appName = processName;
                            info = new RunningApplicationInfo(processName, appName, icon, pid, uid, memory, RunningApplicationInfo.PROCESS_TYPE_SERVICE);
                            infos.add(info);
                            continue;
                        } catch (PackageManager.NameNotFoundException e1) {
                            icon = context.getResources().getDrawable(R.drawable.ic_launcher);
                        }
                    } else {
                        icon = context.getResources().getDrawable(R.drawable.ic_launcher);
                    }
                    appName = processName;
                    info = new RunningApplicationInfo(processName, appName, icon, pid, uid, memory, RunningApplicationInfo.PROCESS_TYPE_SERVICE);
                    infos.add(info);
                }
            }
            return infos;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
            if (listener != null) {
                if (values.length == 1) {
                    listener.onMemoryScanTaskStart((Integer) values[0]);
                }
                if (values.length == 2) {
                    listener.onMemoryScanProgressChanged((String) values[0], (Integer) values[1]);
                }
            }
        }

        @Override
        protected void onPostExecute(List<RunningApplicationInfo> runningApplicationInfos) {
            super.onPostExecute(runningApplicationInfos);
            if (listener != null) {
                listener.onMemoryScanTaskEnded(runningApplicationInfos);
            }
        }
    }

    public void runClean(List<String> processNames){
        new MyCleanTask().execute(processNames);
    }
    class MyCleanTask extends AsyncTask<List<String>, Object, Integer> {

        @Override
        protected Integer doInBackground(List<String>... params) {
            List<String> processNames = params[0];
            publishProgress(processNames.size());
            int fails = 0;
            int index = 0;
            for (String processName : processNames) {
                publishProgress(processName, ++index);
                if (!killProcess(processName)) {
                    fails++;
                }
            }
            return fails;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
            if (listener != null) {
                if (values.length == 1) {
                    listener.onMemoryCleanTaskStart((Integer) values[0]);
                }
                if(values.length==2){
                    listener.onMemoryCleanProgressChanged((String)values[0],(Integer)values[1]);
                }
            }
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if(listener!=null){
                listener.onMemoryCleanTaskEnded(integer);
            }
        }

        public boolean killProcess(String processName) {
            String packageName = null;
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
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
