package edu.ncu.safe.service;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import edu.ncu.safe.domain.VersionInfo;
import edu.ncu.safe.engine.DownLoadFile;
import edu.ncu.safe.engine.LoadLatestVersionInfo;

/**
 * Created by Mr_Yang on 2016/6/25.
 */
public class UpdateAppService extends Service{
    private static final String TAG = "UpdateAppService";

    private static final int SHOWUPDATEDIALOG = 0;
    private ProgressDialog progressDialog;

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case SHOWUPDATEDIALOG:
                    showUpdateDialog((VersionInfo) msg.obj);
                    break;
                default:
                    break;
            }
        };
    };
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread() {
            @Override
            public void run() {
                VersionInfo versionInfo;
                try {
                    versionInfo = new LoadLatestVersionInfo(getApplicationContext()).getVersionInfo();
                    Log.i(TAG,"获取版本信息成功");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "获取版本信息失败:" + e.getMessage());
                    return;
                }
                String currentVersion;
                try {
                    currentVersion = getApplicationContext().getPackageManager().getPackageInfo(
                            getPackageName(), 0).versionName;
                    Log.i(TAG, "获取本地版本成功");
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                    Log.i(TAG, "获取本地版本信息失败:"+e.getMessage());
                    return;
                }

                if (!currentVersion.equals(versionInfo.getVerion())) {
                    // 版本号不同，有新的版本信息
                    Log.i(TAG, "版本号不同，有新的版本信息");
                    Message msg = Message.obtain();
                    msg.obj=versionInfo;
                    msg.what = SHOWUPDATEDIALOG;
                    handler.sendMessage(msg);
                } else {
                    // 版本号相同，无须更新
                    Log.i(TAG, "版本号相同，无须更新");
                }
            }
        }.start();
        return super.onStartCommand(intent, flags, startId);
    }

    private void showUpdateDialog(final VersionInfo versionInfo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("新版本:" + versionInfo.getVerion());
        builder.setMessage(versionInfo.getDescription());

        builder.setPositiveButton("立即升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                progressDialog = new ProgressDialog(getApplicationContext());
                progressDialog
                        .setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setTitle("正在下载护机宝" + versionInfo.getVerion());
                progressDialog.setCancelable(false);
                progressDialog.show();
                new Thread() {
                    @Override
                    public void run() {
                        Log.i(TAG, "进入下载。。。");
                        try {
                            String path = Environment
                                    .getExternalStorageDirectory()
                                    + "/360safe.apk";
                            File file = DownLoadFile.downloadFile(
                                    versionInfo.getDownloadUrl(), path,
                                    progressDialog);
                            if (file == null) {
                                Log.i(TAG, "file is null");
                                return;
                            }
                            Log.i(TAG, "下载完成,准备安装");
                            install(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.i(TAG, "下载失败");
                        }
                    }
                }.start();
            }
        });
        builder.setNegativeButton("稍后升级", null);
        builder.show();
    }

    //安装某个文件软件
    private void install(File file) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        startActivity(intent);
    }
}
