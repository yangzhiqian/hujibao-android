package edu.ncu.safe.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

import edu.ncu.safe.R;
import edu.ncu.safe.constant.UrlHelper;
import edu.ncu.safe.util.MyLog;

/**
 * Created by Mr_Yang on 2016/6/25.<br/>
 * 检查app升级的的服务<br/>
 */
public class UpdateAppService extends IntentService {
    private static final String TAG = "UpdateAppService";
    private static final String ACTION_CHECKVERSION = "edu.ncu.safe.service.updateapp.checkversion";
    private static final String ACTION_LOADAPK = "edu.ncu.safe.service.updateapp.loadapk";


    public static final String ACTION_CHECKVERSION_FAILURE = "edu.ncu.safe.service.updateapp.checkversion.failure";
    public static final String ACTION_CHECKVERSION_SAME = "edu.ncu.safe.service.updateapp.loadapk.same";
    public static final String ACTION_CHECKVERSION_NEWVERSION = "edu.ncu.safe.service.updateapp.loadapk.newversion";

    public static final String ACTION_LOADAPK_PROGRESS = "edu.ncu.safe.service.updateapp.loadapk.progress";
    public static final String ACTION_LOADAPK_FAILURE = "edu.ncu.safe.service.updateapp.loadapk.failure";
    public static final String ACTION_LOADAPK_SUCCEED = "edu.ncu.safe.service.updateapp.loadapk.succeed";


    private static AtomicBoolean isChecking = new AtomicBoolean(false);
    private static AtomicBoolean isLoading = new AtomicBoolean(false);

    public UpdateAppService(String name) {
        super(name);
    }

    public UpdateAppService(){
        super(TAG);
    }

    public static void startCheck(Context context) {
        Intent intent = new Intent(ACTION_CHECKVERSION);
        intent.setClass(context,UpdateAppService.class);
        context.startService(intent);
    }

    public static void startLoadApk(Context context, VersionBean newVersion) {
        if (newVersion != null) {
            Intent intent = new Intent(ACTION_LOADAPK);
            intent.setClass(context,UpdateAppService.class);
            intent.putExtra("VersionBean", newVersion);
            context.startService(intent);
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case ACTION_CHECKVERSION:
                if (isChecking.compareAndSet(false, true)) {
                    check();
                    isChecking.set(false);
                }
                break;
            case ACTION_LOADAPK:
                if (isLoading.compareAndSet(false, true)) {
                    loadApk((VersionBean) intent.getSerializableExtra("VersionBean"));
                    isLoading.set(false);
                }
                break;
        }
    }


    private void check() {
        MyLog.i(TAG, "进入检查。。。");
        VersionBean latestVersion = getLatestVersion();
        String currentVersion = getCurrentVersion();
        if (latestVersion == null || currentVersion == null) {
            //获取版本信息失败
            MyLog.i(TAG, "获取版本信息失败。。。");
            getApplicationContext().sendBroadcast(new Intent(ACTION_CHECKVERSION_FAILURE));
            return;
        }

        if (currentVersion.equals(latestVersion.getVerion())) {
            //已经是最新版
            MyLog.i(TAG, "已经是最新版。。。");
            getApplicationContext().sendBroadcast(new Intent(ACTION_CHECKVERSION_SAME));
            return;
        }
        //检查到新版
        MyLog.i(TAG, "检查到新版。。。");
        Intent intent = new Intent(ACTION_CHECKVERSION_NEWVERSION);
        intent.putExtra("VersionBean", latestVersion);
        getApplicationContext().sendBroadcast(intent);
    }

    private void loadApk(final VersionBean newVersion) {
        MyLog.i(TAG, "进入下载。。。");
        try {
            String path = Environment.getExternalStorageDirectory() + "/hujibao.apk";
            URL url = new URL(newVersion.getDownloadUrl());
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

                Intent intent = new Intent(ACTION_LOADAPK_PROGRESS);
                intent.putExtra("totalLength", totalLength);
                intent.putExtra("progress", progress);
                getApplicationContext().sendBroadcast(intent);
                while ((len = is.read(buff)) != -1) {
                    fos.write(buff, 0, len);
                    progress += len;
                    intent.putExtra("progress", progress);
                    getApplicationContext().sendBroadcast(intent);
                }
                fos.flush();
                fos.close();
                is.close();
                MyLog.i(TAG, "下载成功");
                intent = new Intent(ACTION_LOADAPK_SUCCEED);
                intent.putExtra("path", path);
                getApplicationContext().sendBroadcast(intent);
            }
        } catch (IOException e) {
            e.printStackTrace();
            getApplicationContext().sendBroadcast(new Intent(ACTION_LOADAPK_FAILURE));
            MyLog.i(TAG, "下载失败");
        }
    }

    /**
     * 获取最新的版本信息
     *
     * @return 最新的版本信息，null表示获取失败
     */
    private VersionBean getLatestVersion() {
        //获取网络版本号
        StringBuffer sb = null;
        try {
            String path = UrlHelper.getVersionUrl(getApplicationContext());
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");

            InputStream is = conn.getInputStream();
            sb = new StringBuffer();
            String buffer;
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            while ((buffer = br.readLine()) != null) {
                sb.append(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return VersionBean.parseBean(sb.toString());
    }

    /**
     * 获取当前的版本信息
     *
     * @return 当前的版本号
     */
    private String getCurrentVersion() {
        try {
            return getApplicationContext().getPackageManager().getPackageInfo(
                    getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 版本信息的实体类
     */
    public static class VersionBean implements Serializable {
        private String verion;
        private String description;
        private String downloadUrl;

        public VersionBean() {
        }

        public VersionBean(String version, String description, String downloadUrl) {
            this.verion = version;
            this.description = description;
            this.downloadUrl = downloadUrl;
        }

        public String getVerion() {
            return verion;
        }

        public void setVerion(String verion) {
            this.verion = verion;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }

        public void setDownloadUrl(String downloadUrl) {
            this.downloadUrl = downloadUrl;
        }

        public static VersionBean parseBean(String json) {
            JSONObject obj;
            try {
                obj = new JSONObject(json);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
            String version = obj.optString("version", null);
            if (version == null) {
                return null;
            }
            String description = obj.optString("description", "作者很懒，什么都没写");
            String downloadUrl = obj.optString("downloadUrl", "");
            return new VersionBean(version, description, downloadUrl);
        }
    }

}
