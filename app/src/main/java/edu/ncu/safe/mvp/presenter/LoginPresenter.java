package edu.ncu.safe.mvp.presenter;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.EditText;

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
import edu.ncu.safe.mvp.view.LoginMvpView;
import edu.ncu.safe.util.MD5Encoding;

/**
 * Created by Mr_Yang on 2016/9/18.
 */
public class LoginPresenter {
    private LoginMvpView view;
    private Context context;
    private UserLoginTask loginTask;

    public LoginPresenter(LoginMvpView view, Context context) {
        this.context = context;
        this.view = view;
    }

    /**
     * 用于初始化登录界面
     * 暂时没有操作
     */
    public void init() {

    }

    /**
     * 登录逻辑
     * @param et_uName
     * @param et_pwd
     */
    public void login(EditText et_uName, EditText et_pwd) {
        if(loginTask!=null){
            return;
        }
        String message;
        String uName = et_uName.getText().toString().trim();
        //验证账号
        if( (message = checkUserName(uName))!=null){
            view.loginFail(et_uName,message);
            return;
        }
        String pwd = et_pwd.getText().toString().trim();
        //验证密码
        if( (message = checkPwd(pwd))!=null){
            view.loginFail(et_pwd,message);
            return;
        }
        loginTask = new UserLoginTask(context.getString(R.string.login), uName, pwd);
        view.startLogin(uName);
        loginTask.execute();
    }

    /**
     * 验证用户名是否合法
     * @param uName  检测的用户名
     * @return   null代表合法，非null标示不合法，字符对象代表非法描述
     */
    private String checkUserName(String uName){
        if (TextUtils.isEmpty(uName)) {
            return "用户名不能为空";
        }
        if(uName.length()<4){
            return "用户名不能少于4个字符";
        }
        return null;
    }
    /**
     * 验证密码是否合法
     * @param pwd  检测的密码
     * @return   null代表合法，非null标示不合法，字符对象代表非法描述
     */
    private String checkPwd(String pwd){
        if (TextUtils.isEmpty(pwd)) {
            return "密码不能为空";
        }
        if(pwd.length()<6){
            return "密码不能少于6个字符";
        }
        return null;
    }

    public void onDestroy() {
        if(loginTask!=null){
            loginTask.cancel(true);
        }
    }


    class UserLoginTask extends AsyncTask<Void, Void, User> {
        private String strUrl;
        private String uName;
        private String pwd;

        private int responesCode;
        private String message;

        UserLoginTask(String url, String user, String password) {
            this.strUrl = url;
            this.uName = user;
            this.pwd = password;
        }

        @Override
        protected User doInBackground(Void... params) {
            try {
                URL url = new URL(strUrl);
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
                String content = "name=" + URLEncoder.encode(uName + "", "utf-8") +
                        "&password=" + URLEncoder.encode(MD5Encoding.encoding(pwd), "utf-8");
                //将要上传的内容写入流中
                out.writeBytes(content);
                //刷新、关闭
                out.flush();
                out.close();
                responesCode = conn.getResponseCode();
                if (responesCode != 200) {
                    message = "登录失败：错误的返回码"+responesCode;
                    return null;
                }
                return parseToUser(readString(conn.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
                message = "连接服务器异常";
            } catch (JSONException e) {
                e.printStackTrace();
                message = "返回json数据异常";
            }
            return null;
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

        private User parseToUser(String json) throws JSONException {
            JSONObject object = new JSONObject(json);
            boolean isSuccess = object.getBoolean("succeed");
            message = object.getString("message");
            if (isSuccess == false) {
                return null;
            }
            JSONObject jsonuUser = object.getJSONObject("user");
            User user = User.toUser(jsonuUser.toString());
            return user;
        }

        @Override
        protected void onPostExecute(User user) {
            if (user != null) {
                view.loginSucceed(user);
            } else {
                view.loginFail(null,message);
            }
            loginTask = null;
        }

        @Override
        protected void onCancelled() {
            loginTask = null;
            view.loginFail(null,"用户取消了登陆");
        }
    }
}


