package edu.ncu.safe.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import edu.ncu.safe.R;
import edu.ncu.safe.View.CircleImageView;
import edu.ncu.safe.domain.User;
import edu.ncu.safe.util.MD5Encoding;

public class RegistActivity extends Activity implements OnClickListener {

    private static final int REQUEST_READ_CONTACTS = 0;
    private UserRegistTask mAuthTask = null;
    // UI references.
    private CircleImageView civ_icon;
    private AutoCompleteTextView userName;
    private EditText tv_pwd;
    private EditText tv_pwdAgain;
    private TextView tv_phone;
    private Button btn_regist;
    private View progress;
    private View formView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        // Set up the login form.
        formView = findViewById(R.id.regist_form);
        progress = findViewById(R.id.progress);

        civ_icon = (CircleImageView) findViewById(R.id.civ_icon);
        userName = (AutoCompleteTextView) findViewById(R.id.uname);
        tv_pwd = (EditText) findViewById(R.id.password);
        tv_pwdAgain = (EditText) findViewById(R.id.tv_pwdagain);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        btn_regist = (Button) findViewById(R.id.btn_regist);

        civ_icon.setOnClickListener(this);
        btn_regist.setOnClickListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_regist:
                attemptRegist();
                btn_regist.requestFocus();
                break;
            case R.id.btn_login:
                finish();
                break;
            case R.id.civ_icon:
                Intent intent = new Intent();
                /* 开启Pictures画面Type设定为image */
                intent.setType("image/*");
                /* 使用Intent.ACTION_GET_CONTENT这个Action */
                intent.setAction(Intent.ACTION_GET_CONTENT);
                /* 取得相片后返回本画面 */
                startActivityForResult(intent, 1);
                break;
        }
    }

    private void attemptRegist() {
        if (mAuthTask != null) {
            return;
        }
        userName.setError(null);
        tv_pwd.setError(null);
        tv_pwdAgain.setError(null);
        String name = userName.getText().toString();
        String password = tv_pwd.getText().toString();
        String passwordAgain = tv_pwdAgain.getText().toString();
        String phone = tv_phone.getText().toString();


        boolean cancel = false;
        View focusView = null;
        if (TextUtils.isEmpty(name)) {
            userName.setError(getString(R.string.error_field_required));
            focusView = userName;
            cancel = true;
        }
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            tv_pwd.setError(getString(R.string.error_invalid_password));
            focusView = tv_pwd;
            cancel = true;
        }
        if (!password.equals(passwordAgain)) {
            tv_pwdAgain.setError(getString(R.string.error_invalid_password_different));
            focusView = tv_pwdAgain;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mAuthTask = new UserRegistTask(name, password, phone, (String) civ_icon.getTag());
            mAuthTask.execute((Void) null);
            btn_regist.setText("正在注册");
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    private void showProgress(final boolean show) {
        progress.setVisibility(show ? View.VISIBLE : View.GONE);
        formView.setEnabled(!show);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            String[] proj = { MediaStore.Images.Media.DATA };
            Cursor actualimagecursor = managedQuery(uri, proj, null, null, null);
            int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            actualimagecursor.moveToFirst();
            String img_path = actualimagecursor.getString(actual_image_column_index);
            System.out.println("图片真实路径：" + img_path);

            Bitmap bitmap = BitmapFactory.decodeFile(img_path);
            civ_icon.setImageBitmap(bitmap);
            civ_icon.setTag(img_path);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserRegistTask extends AsyncTask<Void, Void, Boolean> {
        private static final String TAG = "uploadFile";
        private static final int TIME_OUT = 10 * 1000;   //超时时间
        private static final String CHARSET = "utf-8"; //设置编码
        String BOUNDARY = UUID.randomUUID().toString();  //边界标识   随机生成
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data";   //内容类型


        private User user;
        private String message;
        private String iconPath;

        public UserRegistTask(String name, String password, String phone,String iconPath) {
            user = new User();
            user.setName(name);
            user.setPwd(MD5Encoding.encoding(password));
            user.setPhone(phone);
            this.iconPath = iconPath;
        }

        @Override
        protected Boolean doInBackground(Void... params) {


            String strUrl = getResources().getString(R.string.regist);
            try {
                URL url = new URL(strUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(TIME_OUT);
                conn.setConnectTimeout(TIME_OUT);
                conn.setDoInput(true);  //允许输入流
                conn.setDoOutput(true); //允许输出流
                conn.setUseCaches(false);  //不允许使用缓存
                conn.setRequestMethod("POST");  //请求方式
                conn.setRequestProperty("Charset", CHARSET);  //设置编码
                conn.setRequestProperty("connection", "keep-alive");
                conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);


                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                if(iconPath!=null) {
                    //构造http协议的照片参数断
                    File file = new File(iconPath);
                    StringBuffer sb_image = new StringBuffer();
                    sb_image.append(PREFIX);
                    sb_image.append(BOUNDARY);
                    sb_image.append(LINE_END);
                    sb_image.append("Content-Disposition: form-data; name=\"photo\"; filename=\"" + file.getName() + "\"" + LINE_END);
                    sb_image.append("Content-Type: image/pjpeg" + LINE_END);
                    System.out.println(file.getName());
                    sb_image.append(LINE_END);

                    dos.write(sb_image.toString().getBytes());
                    InputStream is = new FileInputStream(file);
                    byte[] bytes = new byte[1024];
                    int len = 0;
                    while ((len = is.read(bytes)) != -1) {
                        dos.write(bytes, 0, len);
                    }
                    is.close();
                    dos.write(LINE_END.getBytes());
                }

                //构造http协议的数据参数断
                StringBuffer sb_parms = new StringBuffer();
                sb_parms.append(PREFIX);
                sb_parms.append(BOUNDARY);
                sb_parms.append(LINE_END);
                sb_parms.append("Content-Disposition: form-data; name=\"user\"");
                sb_parms.append(LINE_END);
                sb_parms.append(LINE_END);
                //将要上传的内容写入流中
                sb_parms.append(user.toJson());
                dos.write(sb_parms.toString().getBytes());
                dos.write(LINE_END.getBytes());

                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
                dos.write(end_data);
                dos.flush();
                /**
                 * 获取响应码  200=成功
                 * 当响应成功，获取响应的流
                 */

                int res = conn.getResponseCode();
                Log.e(TAG, "response code:" + res);

                if (res == 200) {
                    String re = readString(conn.getInputStream());
                    JSONObject object = new JSONObject(re);
                    message = object.getString("message");
                    return object.getBoolean("succeed");
                } else {
                    message =  "request error";
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                message = e.getMessage();
                return false;
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

        @Override
        protected void onPostExecute(Boolean b) {
            makeToast(message);
            if(b){
                finish();
            }else{
                btn_regist.setText("注册");
                onCancelled();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    @Override
    public void onBackPressed() {
        setResult(1, null);
        finish();
    }


    private void makeToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

