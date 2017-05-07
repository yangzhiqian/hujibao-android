package edu.ncu.safe.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import edu.ncu.safe.R;
import edu.ncu.safe.base.activity.BackAppCompatActivity;
import edu.ncu.safe.customerview.CircleImageView;
import edu.ncu.safe.domain.User;
import edu.ncu.safe.mvp.presenter.RegistPresenter;
import edu.ncu.safe.mvp.view.RegistMvpView;

public class RegistActivity extends BackAppCompatActivity implements OnClickListener, RegistMvpView {
    private static final int REQUEST_CODE_GET_ICON = 1;
    // UI
    private ScrollView sv_form;
    private CircleImageView civ_icon;
    private EditText et_uName;
    private EditText et_pwd;
    private EditText et_pwdAgain;
    private EditText et_phone;
    private Button btn_regist;
    private View progressView;

    private RegistPresenter presenter;
    private String iconPath;

    @Override
    protected CharSequence initTitle() {
        return getResources().getString(R.string.title_regist);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_regist;
    }

    @Override
    protected void initViews() {
        sv_form = (ScrollView) findViewById(R.id.regist_form);
        progressView = findViewById(R.id.progress);

        civ_icon = (CircleImageView) findViewById(R.id.civ_icon);
        et_uName = (EditText) findViewById(R.id.et_uName);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        et_pwdAgain = (EditText) findViewById(R.id.et_pwdagain);
        et_phone = (EditText) findViewById(R.id.et_phone);
        btn_regist = (Button) findViewById(R.id.btn_regist);

        //设置监听
        civ_icon.setOnClickListener(this);
        btn_regist.setOnClickListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);
    }

    @Override
    protected void initCreate() {
        presenter = new RegistPresenter(this, getApplicationContext());
        presenter.init();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_regist:
                presenter.regist(et_uName, et_pwd, et_pwdAgain, et_phone, iconPath);
                break;
            case R.id.btn_login:
                finish();
                overridePendingTransition(R.anim.activit3dtoright_in, R.anim.activit3dtoright_out);
                break;
            case R.id.civ_icon:
                Intent intent = new Intent();
                /* 开启Pictures画面Type设定为image */
                intent.setType("image/*");
                /* 使用Intent.ACTION_GET_CONTENT这个Action */
                intent.setAction(Intent.ACTION_GET_CONTENT);
                /* 取得相片后返回本画面 */
                startActivityForResult(intent, REQUEST_CODE_GET_ICON);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor actualimagecursor = managedQuery(uri, proj, null, null, null);
            int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            actualimagecursor.moveToFirst();
            iconPath = actualimagecursor.getString(actual_image_column_index);

            Bitmap bitmap = BitmapFactory.decodeFile(iconPath);
            civ_icon.setImageBitmap(bitmap);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @Override
    public void onStartRegist() {
        btn_regist.setText(getString(R.string.button_regist_doing));
        btn_regist.setClickable(false);
        btn_regist.setEnabled(false);
        progressView.setVisibility(View.VISIBLE);
        sv_form.setEnabled(false);
    }

    @Override
    public void onRegistSucceed(User user) {
        makeToast("注册成功");
        Intent intent = new Intent();
        intent.putExtra("uName", user.getName());
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.activit3dtoright_in, R.anim.activit3dtoright_out);
    }

    @Override
    public void onRegistFail(EditText view, String errorMessage) {
        btn_regist.setText(getString(R.string.button_regist));
        btn_regist.setClickable(true);
        btn_regist.setEnabled(true);
        progressView.setVisibility(View.GONE);
        sv_form.setEnabled(true);
        if (view == null) {
            makeToast(errorMessage);
        } else {
            view.setError(errorMessage);
            view.requestFocus();
        }
    }
}

