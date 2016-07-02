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
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.StatFs;
import android.support.annotation.Nullable;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import edu.ncu.safe.domain.CacheInfo;

/**
 * Created by Mr_Yang on 2016/5/27.
 */
public class AppRubbishCleanService extends Service {
    private Context context;
    private MyBinder binder;
    private OnRubbishDataChangedListener listener;

    private Method getPackageSizeInfoMethod;
    private Method freeStorageAndNotifyMethod;
    private long mCacheSize = 0;

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
        try {
            getPackageSizeInfoMethod = getPackageManager().getClass().getMethod(
                    "getPackageSizeInfo", String.class, IPackageStatsObserver.class);

            freeStorageAndNotifyMethod = getPackageManager().getClass().getMethod(
                    "freeStorageAndNotify", long.class, IPackageDataObserver.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public class MyBinder extends Binder {
        public AppRubbishCleanService getService() {
            return AppRubbishCleanService.this;
        }
    }

    public interface OnRubbishDataChangedListener {
        public void onRubbishTaskScanStart(int sumTask);

        public void onRubbishScanProgressChanged(String tastName, int progress);

        public void onRubbishScanTaskEnded(List<CacheInfo> infos);

        public void onRubbishTaskCleanStart(int sumTask);

        public void onRubbishCleanProgressChanged(String tastName);

        public void onRubbishCleanTaskEnded(long size);

        public void onRubbishTaskScanRootStart(int sumTask);

        public void onRubbishScanRootProgressChanged(String tastName, int progress);

        public void onRubbishScanRootTaskEnded(List<CacheInfo> infos);

        public void onRubbishTaskCleanRootStart(int sumTask);

        public void onRubbishCleanRootProgressChanged(String tastName, long size, int progress);

        public void onRubbishCleanRootTaskEnded(long size);


    }

    public void setOnRubbishDataChangedListener(OnRubbishDataChangedListener listener) {
        this.listener = listener;
    }

    private class RubbishScan extends AsyncTask<Void, Object, List<CacheInfo>> {
        private CountDownLatch countDownLatch;
        private List<CacheInfo> apps;
        private List<ApplicationInfo> packages;
        private PackageManager packageManager;

        @Override
        protected List<CacheInfo> doInBackground(Void... params) {
            apps = new ArrayList<CacheInfo>();
            packageManager = getPackageManager();
            packages = packageManager.getInstalledApplications(
                    PackageManager.GET_META_DATA);
            countDownLatch = new CountDownLatch(packages.size());
            publishProgress(packages.size());
            try {
                int task = 0;
                for (ApplicationInfo applicationInfo : packages) {
                    publishProgress(applicationInfo.loadLabel(packageManager), ++task);
                    getPackageSizeInfoMethod.invoke(packageManager, applicationInfo.packageName,
                            new IPackageStatsObserver.Stub() {
                                @Override
                                public void onGetStatsCompleted(PackageStats packageStats, boolean succeeded)
                                        throws RemoteException {
                                    synchronized (apps) {
                                        if (succeeded && packageStats.cacheSize > 0) {
                                            try {
                                                String packageName = packageStats.packageName;
                                                ApplicationInfo info = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
                                                String appName = info.loadLabel(packageManager).toString();
                                                Drawable icon = info.loadIcon(packageManager);
                                                long cacheSize = packageStats.cacheSize;
                                                CacheInfo cacheInfo = new CacheInfo(packageName, appName, icon, cacheSize);
                                                apps.add(cacheInfo);
                                                mCacheSize += packageStats.cacheSize;
                                            } catch (PackageManager.NameNotFoundException e) {
                                                e.printStackTrace();
                                            }
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
            return apps;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
            if (listener != null) {
                if (values.length == 1) {
                    listener.onRubbishTaskScanStart((Integer) values[0]);
                }
                if (values.length == 2) {
                    listener.onRubbishScanProgressChanged((String) values[0], (Integer) values[1]);
                }
            }
        }

        @Override
        protected void onPostExecute(List<CacheInfo> infos) {
            super.onPostExecute(infos);
            if (listener != null) {
                listener.onRubbishScanTaskEnded(infos);
            }
        }
    }

    private class RubbishClean extends AsyncTask<Void, String, Long> {
        @Override
        protected Long doInBackground(Void... params) {
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            StatFs stat = new StatFs(Environment.getDataDirectory().getAbsolutePath());
            try {
                freeStorageAndNotifyMethod.invoke(getPackageManager(),
                        (long) stat.getBlockCount() * (long) stat.getBlockSize(),
                        new IPackageDataObserver.Stub() {
                            @Override
                            public void onRemoveCompleted(String packageName, boolean succeeded)
                                    throws RemoteException {
                                publishProgress(packageName);
                                countDownLatch.countDown();
                            }
                        }
                );
                countDownLatch.await();
            } catch (InvocationTargetException | InterruptedException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return mCacheSize;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            if (listener != null) {
                listener.onRubbishCleanProgressChanged(values[0]);
            }
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Long result) {
            mCacheSize = 0;
            if (listener != null) {
                listener.onRubbishCleanTaskEnded(result);
            }
        }
    }

    public void scanRubbish() {
        new RubbishScan().execute();
    }

    public void cleanRubbish() {
        new RubbishClean().execute();
    }


    private class RubbishScanRoot extends AsyncTask<Void, Object, List<CacheInfo>> {
        int tasks = 0;

        @Override
        protected List<CacheInfo> doInBackground(Void... params) {
            List<CacheInfo> infos = new ArrayList<CacheInfo>();
            PackageManager packageManager = getPackageManager();
            List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
            //推送任务量
            publishProgress(installedPackages.size());
            for (PackageInfo packageInfo : installedPackages) {
                //推送进度
                publishProgress(packageInfo.packageName, ++tasks);
                try {
                    Context cxt = createPackageContext(packageInfo.packageName, Context.CONTEXT_IGNORE_SECURITY);
                    String appName = cxt.getApplicationInfo().loadLabel(packageManager).toString();
                    Drawable icon = cxt.getApplicationInfo().loadIcon(packageManager);
                    long cacheSize = getCacheSize(cxt);
                    CacheInfo info = new CacheInfo(packageInfo.packageName, appName, icon, cacheSize);
                    infos.add(info);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
            return infos;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
            if (listener != null) {
                if (values.length == 1) {
                    listener.onRubbishTaskScanRootStart((Integer) values[0]);
                }
                if (values.length == 2) {
                    listener.onRubbishScanRootProgressChanged((String) values[0], (Integer) values[1]);
                }
            }
        }

        @Override
        protected void onPostExecute(List<CacheInfo> infos) {
            super.onPostExecute(infos);
            if (listener != null) {
                listener.onRubbishScanRootTaskEnded(infos);
            }
        }
    }

    private class RubbishCleanRoot extends AsyncTask<List<CacheInfo>, Object, Long> {

        @Override
        protected Long doInBackground(List<CacheInfo>... params) {
            publishProgress(params[0].size());
            long totalSize = 0;
            int index = 0;
            for (CacheInfo info : params[0]) {
                long size = deleteCache(info.getPackageName());
                totalSize += size;
                publishProgress(info.getApplicationName(), size, ++index);
            }
            return totalSize;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
            if (listener != null) {
                if (values.length == 1) {
                    listener.onRubbishTaskCleanRootStart((Integer) values[0]);
                }
                if (values.length == 3) {
                    listener.onRubbishCleanRootProgressChanged((String) values[0], (Long) values[1], (Integer) values[2]);
                }
            }
        }

        @Override
        protected void onPostExecute(Long size) {
            super.onPostExecute(size);
            if (listener != null) {
                listener.onRubbishCleanRootTaskEnded(size);
            }
        }
    }


    private long getCacheSize(Context cxt) {
        long size = 0;
        List<File> files = getCacheFiles(cxt);
        for (File file : files) {
            size += getDirSize(file);
        }
        return size;
    }


    private List<File> getCacheFiles(Context cxt) {
        List<File> files = new ArrayList<File>();
        files.add(cxt.getFilesDir());
        files.add(cxt.getCacheDir());
        files.add(cxt.getExternalCacheDir());

        File dabaseFile = cxt.getFilesDir().getParentFile();
        for (File f : dabaseFile.listFiles()) {
            if (f.getName().equals("database")) {
                for (File file : f.listFiles()) {
                    if (file.getName().startsWith("webview.db") ||
                            file.getName().startsWith("webviewCache.db")) {
                        files.add(file);
                    }
                }
                break;
            }
        }
        return files;
    }

    private long deleteCache(String packName) {
        try {
            Context cxt = createPackageContext(packName, Context.CONTEXT_IGNORE_SECURITY);
            long size = 0;
            List<File> files = getCacheFiles(cxt);
            for (File file : files) {
                if (file.isDirectory()) {
                    size += deleteFile(file);
                } else {
                    size += file.length();
                    file.delete();
                }
            }
            return size;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private long deleteFile(File file) {
        if (file == null) {
            return 0;
        }
        long size = 0;
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                size += deleteFile(f);
            }
            file.delete();
        } else {
            size += file.length();
            file.delete();
        }
        return size;
    }

    public long getDirSize(File file) {
        if (file == null) {
            return 0;
        }
        //判断文件是否存在
        if (file.exists()) {
            //如果是目录则递归计算其内容的总大小
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                long size = 0;
                for (File f : children)
                    size += getDirSize(f);
                return size;
            } else {
                return file.length();
            }
        } else {
            return 0;
        }
    }
}
