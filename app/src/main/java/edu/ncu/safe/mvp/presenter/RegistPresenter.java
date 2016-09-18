package edu.ncu.safe.mvp.presenter;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.EditText;

import org.json.JSONException;
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
import edu.ncu.safe.domain.User;
import edu.ncu.safe.mvp.view.RegistMvpView;
import edu.ncu.safe.util.MD5Encoding;

/**
 * Created by Mr_Yang on 2016/9/18.
 */
public class RegistPresenter {
    private RegistMvpView view;
    private Context context;
    UserRegistTask registTask;

    public RegistPresenter(RegistMvpView view, Context context) {
        this.context = context;
        this.view = view;
    }

    public void init(){}
    public void regist(EditText et_uName,EditText et_pwd,EditText et_pwdAgain,EditText et_phone,String iconPath){
        String uName = et_uName.getText().toString().trim();
        String pwd = et_pwd.getText().toString().trim();
        String pwdAgain = et_pwdAgain.getText().toString().trim();
        String phone = et_phone.getText().toString().trim();
        String message;
        if((message=checkUserName(uName))!=null){
            view.onRegistFail(et_uName,message);
            return;
        }
        if((message=checkPwd(pwd))!=null){
            view.onRegistFail(et_pwd,message);
            return;
        }
        if(!pwd.equals(pwdAgain)){
            view.onRegistFail(et_pwd,"两次密码不相同");
            return;
        }
        if((message=checkPhoneNumber(phone))!=null){
            view.onRegistFail(et_phone,message);
            return;
        }
        registReal(context.getString(R.string.regist),uName,pwd,phone,iconPath);
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

    /**
     * 检查手机号是否合法
     * @param phoneNumber   手机号码
     * @return   null代表合法，否则标示错误描述
     */
    private String checkPhoneNumber(String phoneNumber){
        return null;
    }


    private void registReal(String url,String uName,String pwd,String phone,String iconPath){
        if(registTask!=null){
            return;
        }
        view.onStartRegist();
        registTask = new UserRegistTask(url,uName,pwd,phone,iconPath);
        registTask.execute();
    }

    public void onDestroy() {
        if(registTask!=null){
            registTask.cancel(true);
            registTask = null;
        }
    }

    public class UserRegistTask extends AsyncTask<Void, Void, Boolean> {
        private static final String TAG = "uploadFile";
        private static final int TIME_OUT = 10 * 1000;   //超时时间
        private static final String CHARSET = "utf-8"; //设置编码
        String BOUNDARY = UUID.randomUUID().toString();  //边界标识   随机生成
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data";   //内容类型

        private String strUrl;
        private User user;
        private String message;
        private String iconPath;

        public UserRegistTask(String strUrl,String name, String password, String phone,String iconPath) {
            this.strUrl = strUrl;
            user = new User();
            user.setName(name);
            user.setPwd(MD5Encoding.encoding(password));
            user.setPhone(phone);
            this.iconPath = iconPath;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

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
                    sb_image.append(LINE_END);
                    dos.write(sb_image.toString().getBytes());
                    //写入照片实体数据
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
                //将要上传的注册信息写入流中
                sb_parms.append(user.toJson());
                dos.write(sb_parms.toString().getBytes());
                dos.write(LINE_END.getBytes());
                //body的结束比哦啊之
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
                dos.write(end_data);
                dos.flush();//上传数据
                /**
                 * 获取响应码  200=成功
                 * 当响应成功，获取响应的流
                 */

                int responseCode = conn.getResponseCode();
                if(responseCode!=200){
                    //非200失败
                    message = "注册失败：错误的返回码"+responseCode;
                    return false;
                }
                //从服务器端读取反馈信息
                JSONObject object = new JSONObject(readString(conn.getInputStream()));
                message = object.getString("message");
                return object.getBoolean("succeed");
            } catch (IOException e) {
                e.printStackTrace();
                message = "连接服务器异常";
                return false;
            } catch (JSONException e) {
                e.printStackTrace();
                message = "返回json数据异常";
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
           if(b){
               view.onRegistSucceed(user);
           }else{
               view.onRegistFail(null,message);
           }
            registTask = null;
        }

        @Override
        protected void onCancelled() {
            registTask = null;
            view.onRegistFail(null,"用户取消了注册");
        }
    }
}


