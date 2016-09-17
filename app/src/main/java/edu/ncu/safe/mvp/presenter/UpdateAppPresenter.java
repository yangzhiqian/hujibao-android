package edu.ncu.safe.mvp.presenter;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.View;
import android.widget.Toast;

import java.io.File;

import edu.ncu.safe.R;
import edu.ncu.safe.customerview.MyDialog;
import edu.ncu.safe.domain.VersionInfo;
import edu.ncu.safe.mvp.view.UpdateMvpView;
import edu.ncu.safe.service.UpdateAppService;
import edu.ncu.safe.util.MyUtil;

/**
 * Created by Mr_Yang on 2016/9/16.
 */
public class UpdateAppPresenter implements UpdateAppService.UpdateListener {
    private UpdateMvpView view;
    private UpdateAppService service;
    private Context context;
    private ProgressDialog progressDialog;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder bind) {
            service = ((UpdateAppService.MyBind) bind).getInstance();
            service.setUpdateListener(UpdateAppPresenter.this);
            service.update();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public UpdateAppPresenter(UpdateMvpView view) {
        this.view = view;
        context = (Context)view;
    }

    public void start(){
        // 检查是否有新版本信息，有则弹出升级对话框
        Intent intent = new Intent();
        intent.setClass(context, UpdateAppService.class);
        intent.setAction(context.getResources().getString(R.string.action_update));
        context.bindService(intent,connection,context.BIND_AUTO_CREATE);
    }

    /**
     * 用于注销服务
     */
    public void destory(){
        context.unbindService(connection);
    }

    @Override
    public void onNewVersionLoaded(VersionInfo newVersionInfo) {
        showUpdateDialog(newVersionInfo.getVerion(),newVersionInfo.getDescription(),newVersionInfo.getDownloadUrl());
    }

    //该方法在子线程中执行
    @Override
    public void onDownloadProgressChange(long loaded, long total) {
        progressDialog.setMax((int)total);
        progressDialog.setProgress((int)loaded);
    }

    @Override
    public void onNewApkDownloaded(File file) {
        MyUtil.install(context,file);
    }

    @Override
    public void onDownloadFailed(String message) {
        progressDialog.dismiss();
        Toast.makeText(context.getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }


    /**
     * 用户用户选择是否更新
     * @param version          新的版本号
     * @param description      新版本描述信息
     * @param url              新版本下载地址
     */
    private void showUpdateDialog(final String version, final String description, final String url) {
        final MyDialog myDialog = new MyDialog(context);
        myDialog.setTitle(String.format(context.getResources().getString(R.string.dialog_title_update_note), version));
        myDialog.setMessage(description);
        myDialog.setYESText(context.getResources().getString(R.string.button_update_now));
        myDialog.setNOText(context.getResources().getString(R.string.button_update_later));
        myDialog.setPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
                progressDialog = new ProgressDialog(context);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setTitle(String.format(context.getResources().getString(R.string.progress_dialog_title), version));
                progressDialog.setCancelable(false);
                progressDialog.show();

                //下载apk
                service.downloadNewApk(url);
            }
        });
        myDialog.show();
    }
}
