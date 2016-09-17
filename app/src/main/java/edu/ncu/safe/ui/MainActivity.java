package edu.ncu.safe.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;

import edu.ncu.safe.R;
import edu.ncu.safe.mvp.presenter.UpdateAppPresenter;
import edu.ncu.safe.mvp.view.UpdateMvpView;
import edu.ncu.safe.ui.fragment.MainFragment;
import edu.ncu.safe.ui.fragment.MainMenuFragment;

public class MainActivity extends BaseAppCompatActivity  implements UpdateMvpView {
    private static final String TAG = "MainActivity";
    // 主界面控件
    private Toolbar toolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawerLayout;

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

        presenter = new UpdateAppPresenter(this);
        presenter.start();
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
        if(KeyEvent.KEYCODE_BACK == keyCode){
            long keyDownTime = System.currentTimeMillis();
            if(keyDownTime-lastBackKeyDownTime>2000){
                makeToast("再按一次退出");
                lastBackKeyDownTime = keyDownTime;
            }else{
                this.finish();
            }
        }
        return true;
    }
}