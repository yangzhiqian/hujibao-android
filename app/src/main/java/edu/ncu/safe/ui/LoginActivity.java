package edu.ncu.safe.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import edu.ncu.safe.R;
import edu.ncu.safe.domain.User;
import edu.ncu.safe.util.MD5Encoding;

public class LoginActivity extends Activity {

    private static final int REQUEST_READ_CONTACTS = 0;
    private UserLoginTask mAuthTask = null;
    // UI references.
    private AutoCompleteTextView userName;
    private EditText pwd;
    private Button btn_longin;
    private Button btn_regist;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        userName = (AutoCompleteTextView) findViewById(R.id.uname);
        pwd = (EditText) findViewById(R.id.password);
        btn_longin = (Button) findViewById(R.id.btn_login);

        btn_longin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
                btn_longin.setText("正在登陆");
                btn_longin.requestFocus();
            }
        });

        findViewById(R.id.btn_regist).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegistActivity.class));
            }
        });
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }
        userName.setError(null);
        pwd.setError(null);
        String name = userName.getText().toString();
        String password = pwd.getText().toString();
        boolean cancel = false;
        View focusView = null;
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            pwd.setError(getString(R.string.error_invalid_password));
            focusView = pwd;
            cancel = true;
        }
        if (TextUtils.isEmpty(name)) {
            userName.setError(getString(R.string.error_field_required));
            focusView = userName;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mAuthTask = new UserLoginTask(name, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mLoginFormView.setEnabled(!show);
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, User> {
        private String user;
        private String mPassword;
        private int responesCode;
        private String message;

        UserLoginTask(String user, String password) {
            this.user = user;
            this.mPassword = password;
        }

        @Override
        protected User doInBackground(Void... params) {
            try {
                URL url = new URL(getResources().getString(R.string.login));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("POST");
                // 配置本次连接的Content-type，配置为application/x-www-form-urlencoded的
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                // 连接，从postUrl.openConnection()至此的配置必须要在connect之前完成，
                // 要注意的是connection.getOutputStream会隐含的进行connect。
                conn.connect();
                //DataOutputStream流
                DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                //要上传的参数
                String content = "name=" + URLEncoder.encode(user + "", "utf-8") +
                        "&password=" + URLEncoder.encode(MD5Encoding.encoding(mPassword), "utf-8");
                //将要上传的内容写入流中
                out.writeBytes(content);
                //刷新、关闭
                out.flush();
                out.close();
                responesCode = conn.getResponseCode();
                System.out.println(responesCode);
                if (responesCode != 200) {
                    message = "登录失败！";
                    return null;
                }
                return parseToUser(readString(conn.getInputStream()));
            } catch (Exception e) {
                e.printStackTrace();
                message = "连接服务器异常！";
                return null;
            }
        }

        private String readString(InputStream is) throws IOException {
            byte[] bytes = new byte[1024];
            int len = 0;
            StringBuffer sb = new StringBuffer();
            while ((len = is.read(bytes)) != -1) {
                sb.append(new String(bytes, 0, len));
            }
            return sb.toString();
        }

        private User parseToUser(String json) {
            try {
                JSONObject object = new JSONObject(json);
                boolean isSuccess = object.getBoolean("succeed");
                message = object.getString("message");
                if (isSuccess == false) {
                    return null;
                }
                JSONObject jsonuUser = object.getJSONObject("user");
                User user = User.toUser(jsonuUser.toString());
                Log.i("TAG",user.getToken());
                return user;
            } catch (JSONException e) {
                e.printStackTrace();
                message = "json数据解析错误";
                return null;
            }
        }

        @Override
        protected void onPostExecute(User user) {
            makeToast(message);
            btn_longin.setText("登录");
            if (user != null) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("user",user);
                intent.putExtras(bundle);
                setResult(1,intent);
                finish();
            } else {
                //    pwd.setError(getString(R.string.error_incorrect_password));
                pwd.requestFocus();
            }
            mAuthTask = null;
            showProgress(false);
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
    private void makeToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        setResult(-1,null);
        finish();
    }
}

