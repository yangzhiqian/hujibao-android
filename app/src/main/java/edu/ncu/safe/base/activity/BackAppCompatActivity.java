package edu.ncu.safe.base.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import edu.ncu.safe.R;

/**
 * Created by Mr_Yang on 2016/7/12.<br/>
 * 统一样式，继承BaseAppCompatActivity，统一toolbar
 */
public abstract  class BackAppCompatActivity extends BaseAppCompatActivity {
    protected void initToolBar() {
        setToolBarTitle(initTitle());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);// 隐藏标题
//        getSupportActionBar().setIcon(R.drawable.user);//设置图标
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);// 是否显示返回按钮
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.activit3dtoright_in,
                        R.anim.activit3dtoright_out);
            }
        });
    }

    protected abstract  CharSequence initTitle();

    protected  void setToolBarTitle(CharSequence title){
        ((TextView)findViewById(R.id.tv_title)).setText(title);
    }
    protected CharSequence getToolBarTitle(){
        return ((TextView)findViewById(R.id.tv_title)).getText();
    }
}
