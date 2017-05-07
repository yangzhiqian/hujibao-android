package edu.ncu.safe.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import edu.ncu.safe.R;
import edu.ncu.safe.base.activity.BackAppCompatActivity;
import edu.ncu.safe.domain.User;
import edu.ncu.safe.mvp.presenter.LoginPresenter;
import edu.ncu.safe.mvp.view.LoginMvpView;

public class LoginActivity extends BackAppCompatActivity implements LoginMvpView, OnClickListener {
    public static final int REQUEST_CODE_REGIST = 1;
    // UI
    private ScrollView scrollView;
    private EditText userName;
    private EditText pwd;
    private Button btn_longin;
    private View progressView;

    private LoginPresenter presenter;

    @Override
    protected CharSequence initTitle() {
        return getResources().getString(R.string.title_log_in);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_login;
    }

    protected void initViews(){
        scrollView = (ScrollView) findViewById(R.id.login_form);
        userName = (EditText) findViewById(R.id.uname);
        pwd = (EditText) findViewById(R.id.password);
        btn_longin = (Button) findViewById(R.id.btn_login);
        progressView = findViewById(R.id.login_progress);
        progressView = findViewById(R.id.login_progress);

        btn_longin.setOnClickListener(this);
        findViewById(R.id.btn_regist).setOnClickListener(this);
    }

    @Override
    protected void initCreate() {
        presenter = new LoginPresenter(this,getApplicationContext());
        presenter.init();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                presenter.login(userName,pwd);
                break;
            case R.id.btn_regist:
                toAntherAvitvityForResult(RegistActivity.class,REQUEST_CODE_REGIST);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_CODE_REGIST&&resultCode==RESULT_OK){
            String uName = data.getStringExtra("uName");
            userName.setText(uName);
            pwd.requestFocus();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void loginSucceed(User user) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        intent.putExtras(bundle);
        setResult(1, intent);
        makeToast(getString(R.string.toast_succeed_log_in));
        finish();
    }

    @Override
    public void loginFail(EditText view,String errorMessage) {
        btn_longin.setText(getString(R.string.button_log_in));
        btn_longin.setClickable(true);
        btn_longin.setEnabled(true);
        progressView.setVisibility(View.GONE);
        scrollView.setEnabled(true);
        if(view==null){
            makeToast(errorMessage);
            return;
        }
        view.setError(errorMessage);
        view.requestFocus();
    }

    @Override
    public void startLogin(String uName) {
        btn_longin.setText(getString(R.string.button_log_in_doing));
        btn_longin.setClickable(false);
        btn_longin.setEnabled(false);
        progressView.setVisibility(View.VISIBLE);
        scrollView.setEnabled(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }
}

