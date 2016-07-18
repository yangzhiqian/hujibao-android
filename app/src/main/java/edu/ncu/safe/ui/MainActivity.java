package edu.ncu.safe.ui;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.io.File;

import edu.ncu.safe.R;
import edu.ncu.safe.View.MyDialog;
import edu.ncu.safe.domain.VersionInfo;
import edu.ncu.safe.service.UpdateAppService;
import edu.ncu.safe.ui.fragment.MainFragment;
import edu.ncu.safe.ui.fragment.MainMenuFragment;
import edu.ncu.safe.util.MyUtil;

public class MainActivity extends AppCompatActivity implements UpdateAppService.UpdateListener {
    private static final String TAG = "MainActivity";
    // 主界面控件
    private Toolbar toolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawerLayout;

    private MainFragment mainFragment;
    private MainMenuFragment menuFragment;
    UpdateAppService service;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder bind) {
            service = ((UpdateAppService.MyBind) bind).getInstance();
            service.setUpdateListener(MainActivity.this);
            service.update();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolBar();
        initViews();
        //设置出fragment
        FragmentManager fm = getSupportFragmentManager();
        mainFragment = new MainFragment();
        fm.beginTransaction().add(R.id.fl_container, mainFragment, "").commit();

        menuFragment = new MainMenuFragment();
        fm.beginTransaction().add(R.id.fl_menu, menuFragment, "").commit();
        toUpdate();
    }

    private void toUpdate(){
        // 检查是否有新版本信息，有则弹出升级对话框
        // startService(new Intent(getResources().getString(R.string.action_update)));
        // startService(new Intent(this, UpdateAppService.class));
        Intent intent = new Intent();
        intent.setClass(this, UpdateAppService.class);
        intent.setAction(getResources().getString(R.string.action_update));
        bindService(intent,connection,BIND_AUTO_CREATE);
    }


    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);// 隐藏标题
//        getSupportActionBar().setIcon(R.drawable.user);//设置图标
//        getSupportActionBar().setDisplayHomeAsUpEnabled(false);// 是否显示返回按钮
    }

    private void initViews() {
        drawerLayout = (DrawerLayout) findViewById(R.id.id_drawerlayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout, toolbar, R.string.ok, R.string.cancle);
        actionBarDrawerToggle.syncState();
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            //登录
            menuFragment.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }
    private ProgressDialog progressDialog;

    private void showUpdateDialog(final String version, final String description, final String url) {
        final MyDialog myDialog = new MyDialog(this);
        myDialog.setTitle(String.format(getResources().getString(R.string.dialog_title_update_note), version));
        myDialog.setMessage(description);
        myDialog.setYESText(getResources().getString(R.string.button_update_now));
        myDialog.setNOText(getResources().getString(R.string.button_update_later));
        myDialog.setPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setTitle(String.format(getResources().getString(R.string.progress_dialog_title), version));
                progressDialog.setCancelable(false);
                progressDialog.show();

                //下载apk
                service.downloadNewApk(url);
            }
        });
        myDialog.show();
    }

    @Override
    public void onNewVersionLoaded(VersionInfo newVersionInfo) {
        showUpdateDialog(newVersionInfo.getVerion(),newVersionInfo.getDescription(),newVersionInfo.getDownloadUrl());
    }

    @Override//该方法在子线程中执行
    public void onDownloadProgressChange(long loaded, long total) {
        progressDialog.setMax((int)total);
        progressDialog.setProgress((int)loaded);
    }

    @Override
    public void onNewApkDownloaded(File file) {
        MyUtil.install(this,file);
    }

    @Override
    public void onDownloadFailed(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}