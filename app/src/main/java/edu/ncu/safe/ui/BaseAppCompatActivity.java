package edu.ncu.safe.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.Toast;

import edu.ncu.safe.R;

/**
 * Created by Mr_Yang on 2016/9/16.
 */
public class BaseAppCompatActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().getDecorView().setBackgroundResource(R.drawable.background);
    }

    protected void toAntherAvitvity(Class clazz) {
        Intent intent = new Intent();
        intent.setClass(this, clazz);
        startActivity(intent);
        overridePendingTransition(R.anim.activit3dtoleft_in, R.anim.activit3dtoleft_out);
    }

    protected void toAntherAvitvityForResult(Class clazz,int requestCode) {
        Intent intent = new Intent();
        intent.setClass(this, clazz);
        startActivityForResult(intent,requestCode);
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
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
