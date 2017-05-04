package edu.ncu.safe.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;

import java.io.File;

import edu.ncu.safe.R;
import edu.ncu.safe.customerview.MyDialog;
import edu.ncu.safe.mvp.presenter.UpdateAppPresenter;
import edu.ncu.safe.mvp.view.UpdateMvpView;
import edu.ncu.safe.service.UpdateAppService;
import edu.ncu.safe.ui.fragment.MainFragment;
import edu.ncu.safe.ui.fragment.MainMenuFragment;
import edu.ncu.safe.util.MyUtil;
import okhttp3.internal.Util;

public class MainActivity extends BaseAppCompatActivity implements UpdateMvpView {
    private static final String TAG = "MainActivity";
    // 主界面控件
    private Toolbar toolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawerLayout;
    private ProgressDialog progressDialog;

    private MainFragment mainFragment;
    private MainMenuFragment menuFragment;

    private UpdateAppPresenter presenter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

        //设置出fragment
        FragmentManager fm = getSupportFragmentManager();
        mainFragment = new MainFragment();
        fm.beginTransaction().add(R.id.fl_container, mainFragment, "mainFragment").commit();
        menuFragment = new MainMenuFragment();
        fm.beginTransaction().add(R.id.fl_menu, menuFragment, "menuFragment").commit();

        presenter = new UpdateAppPresenter(this, this);
        presenter.startCheck();
    }


    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);// 隐藏标题
//        getSupportActionBar().setIcon(R.drawable.user);//设置图标
//        getSupportActionBar().setDisplayHomeAsUpEnabled(false);// 是否显示返回按钮
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
        presenter.destory();
    }


    private long lastBackKeyDownTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            long keyDownTime = System.currentTimeMillis();
            if (keyDownTime - lastBackKeyDownTime > 2000) {
                makeToast("再按一次退出");
                lastBackKeyDownTime = keyDownTime;
            } else {
                this.finish();
            }
        }
        return true;
    }

    @Override
    public void checkFailure() {
        makeToast("检查最新版本失败");
    }

    @Override
    public void checkSame() {
        makeToast("已经是最新版");
    }

    @Override
    public void checkNewVersion(final UpdateAppService.VersionBean newVersion) {
        final MyDialog myDialog = new MyDialog(this);
        myDialog.setTitle(String.format(getResources().getString(R.string.dialog_title_update_note), newVersion.getVerion()));
        myDialog.setMessage(newVersion.getDescription());
        myDialog.setYESText(this.getResources().getString(R.string.button_update_now));
        myDialog.setNOText(this.getResources().getString(R.string.button_update_later));
        myDialog.setPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setTitle(String.format(getResources().getString(R.string.progress_dialog_title), newVersion.getVerion()));
                progressDialog.setCancelable(false);
                progressDialog.show();

                //下载apk
                presenter.startLoad(newVersion);
            }
        });
        myDialog.show();
    }

    @Override
    public void loadFailure() {
        progressDialog.dismiss();
        makeToast("下载失败");

    }

    @Override
    public void loadProgress(int progress, int total) {
        progressDialog.setMax(total);
        progressDialog.setProgress(progress);
    }

    @Override
    public void loadSucceed(String path) {
        progressDialog.dismiss();
        makeToast("下载完成");
        MyUtil.install(this,new File(path));
    }
}