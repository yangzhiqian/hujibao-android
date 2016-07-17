package edu.ncu.safe.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import edu.ncu.safe.R;
import edu.ncu.safe.service.UpdateAppService;
import edu.ncu.safe.ui.fragment.MainFragment;
import edu.ncu.safe.ui.fragment.MainMenuFragment;

public class MainActivity extends AppCompatActivity{
    // 主界面控件
    private Toolbar toolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawerLayout;

    private MainFragment mainFragment;
    private MainMenuFragment menuFragment;

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

        // 检查是否有新版本信息，有则弹出升级对话框
       // startService(new Intent(getResources().getString(R.string.action_update)));
        startService(new Intent(this, UpdateAppService.class));
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
        if(requestCode==1){
            //登录
            menuFragment.onActivityResult(requestCode, resultCode, data);
        }
         super.onActivityResult(requestCode, resultCode, data);
    }
}