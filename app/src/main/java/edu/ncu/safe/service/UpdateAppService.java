package edu.ncu.safe.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import edu.ncu.safe.R;
import edu.ncu.safe.domain.VersionInfo;
import edu.ncu.safe.engine.LoadLatestVersionInfo;
import edu.ncu.safe.util.MyLog;

/**
 * Created by Mr_Yang on 2016/6/25.
 */
public class UpdateAppService extends Service{
    private static final String TAG = "UpdateAppService";

    private static final int NEWVERSIONLOADED = 1;
    private static final int NEWAPKLOADED = 2;
    private static final int NEWAPKFAIL = 3;

    private Thread versionThread;
    private Thread loadAppThread;
    private Handler handler = new MyHandler(this);

    private static class MyHandler extends  Handler{
        private WeakReference<UpdateAppService> servie;
        public MyHandler(UpdateAppService service){
            this.servie = new WeakReference<UpdateAppService>(service);
        }

        public void handleMessage(android.os.Message msg) {
            UpdateAppService updateAppService = servie.get();
            if(updateAppService==null || updateAppService.listener==null){
                return;
            }
            switch (msg.what){
                case NEWVERSIONLOADED:
                    updateAppService.listener.onNewVersionLoaded((VersionInfo) msg.obj);
                    break;
                case NEWAPKFAIL:
                    updateAppService.listener.onDownloadFailed(updateAppService.getResources().getString(R.string.toast_error_download));
                    break;
                case NEWAPKLOADED:
                    updateAppService.listener.onNewApkDownloaded((File) msg.obj);
                    break;
            }
        };
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBind();
    }

    @Override
    public boolean onUnbind(Intent intent) {

        return super.onUnbind(intent);
    }

    public void update(){
        if(versionThread==null){
            versionThread = new Thread(){
                @Override
                public void run() {
                    try {
                        //获取网络上最新版本信息
                        VersionInfo versionInfo = new LoadLatestVersionInfo(getApplicationContext()).getVersionInfo();
                        //获取本机版本号
                        String currentVersion = getApplicationContext().getPackageManager().getPackageInfo(
                                getPackageName(), 0).versionName;
                        MyLog.i(TAG, "获取版本信息成功");
                        if (!currentVersion.equals(versionInfo.getVerion())) {
                            // 版本号不同，有新的版本信息
                            MyLog.i(TAG, "版本号不同，有新的版本信息");
                            Message message = Message.obtain();
                            message.obj = versionInfo;
                            message.what = NEWVERSIONLOADED;
                            handler.sendMessage(message);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        MyLog.i(TAG, "获取版本信息失败:" + e.getMessage());
                        return;
                    }finally {
                        versionThread = null;
                    }
                }
            };
            versionThread.start();
        }
    }


    public void downloadNewApk(final String apkUrl){
        if(loadAppThread==null){
            loadAppThread = new Thread(){
                public void run() {
                    MyLog.i(TAG, "进入下载。。。");
                    try {
                        String path = Environment.getExternalStorageDirectory() + "/hujibao.apk";
                        URL url = new URL(apkUrl);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setConnectTimeout(5000);
                        conn.setRequestMethod("GET");

                        if (conn.getResponseCode() == 200) {
                            MyLog.i("tag", "下载新版本连接成功");
                            InputStream is = conn.getInputStream();
                            int totalLength = conn.getContentLength();
                            int len;
                            int progress = 0;
                            byte[] buff = new byte[1024];
                            File file = new File(path);
                            FileOutputStream fos = new FileOutputStream(file);

                            while ((len = is.read(buff)) != -1) {
                                fos.write(buff, 0, len);
                                progress+=len;
                                listener.onDownloadProgressChange(progress,totalLength);
                            }
                            fos.flush();
                            fos.close();
                            is.close();
                            Message message = Message.obtain();
                            message.obj = file;
                            message.what = NEWAPKLOADED;
                            handler.sendMessage(message);
                            return;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    MyLog.i(TAG, "下载失败");
                    handler.sendEmptyMessage(NEWAPKFAIL);
                }
            };
            loadAppThread.start();
        }
    }


    private UpdateListener listener;
    public void setUpdateListener(UpdateListener listener){
        this.listener = listener;
    }
    public interface UpdateListener{
        void onNewVersionLoaded(VersionInfo newVersionInfo);
        void onDownloadProgressChange(long loaded,long total);
        void onNewApkDownloaded(File file);
        void onDownloadFailed(String message);
    }

    public class MyBind extends Binder{
        public UpdateAppService getInstance(){
            return UpdateAppService.this;
        }
    }

}
