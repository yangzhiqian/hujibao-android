package edu.ncu.safe.myadapter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import edu.ncu.safe.R;

/**
 * Created by Mr_Yang on 2016/7/12.
 */
public class MyAppCompatActivity extends AppCompatActivity {
    protected Toolbar toolbar;

    protected void initToolBar(String title) {
        ((TextView)findViewById(R.id.tv_title)).setText(title);
        toolbar = (Toolbar) findViewById(R.id.id_toolbar);
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

    protected void toAntherAvitvity(Class clazz) {
        Intent intent = new Intent();
        intent.setClass(this, clazz);
        startActivity(intent);
        overridePendingTransition(R.anim.activit3dtoleft_in, R.anim.activit3dtoleft_out);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(KeyEvent.KEYCODE_BACK == keyCode){
            this.finish();
            overridePendingTransition(R.anim.activit3dtoright_in, R.anim.activit3dtoright_out);
        }
        return super.onKeyDown(keyCode, event);
    }
    protected void makeToast(String message){
        Toast.makeText(MyAppCompatActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
