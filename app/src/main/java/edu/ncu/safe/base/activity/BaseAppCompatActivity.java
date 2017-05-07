package edu.ncu.safe.base.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.widget.Toast;

import edu.ncu.safe.R;

/**
 * Created by Mr_Yang on 2016/9/16.<br/>
 * 基类activity,统一样式，包括统一背景、跳转和按回退按钮的处理
 */
public abstract class BaseAppCompatActivity extends AppCompatActivity {
    protected Toolbar toolbar;

    final protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().getDecorView().setBackgroundResource(R.drawable.background);
        setContentView(initLayout());
        toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        initToolBar();
        initViews();
        initCreate();
    }

    protected abstract int initLayout();

    protected abstract void initToolBar();

    protected abstract void initViews();

    protected abstract void initCreate();

    protected void toAntherAvitvity(Class clazz) {
        Intent intent = new Intent();
        intent.setClass(this, clazz);
        startActivity(intent);
        overridePendingTransition(R.anim.activit3dtoleft_in, R.anim.activit3dtoleft_out);
    }

    protected void toAntherAvitvityForResult(Class clazz, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(this, clazz);
        startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.activit3dtoleft_in, R.anim.activit3dtoleft_out);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            this.finish();
            overridePendingTransition(R.anim.activit3dtoright_in, R.anim.activit3dtoright_out);
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void makeToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
