package edu.ncu.safe.mvp.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import edu.ncu.safe.MyApplication;
import edu.ncu.safe.R;
import edu.ncu.safe.domain.MainMenuInfo;
import edu.ncu.safe.domain.User;
import edu.ncu.safe.mvp.view.MainMenuMvpView;
import edu.ncu.safe.ui.fragment.MainMenuFragment;
import edu.ncu.safe.util.BitmapUtil;

/**
 * Created by Mr_Yang on 2016/9/17.
 */
public class MainMenuPresenter {
    private MainMenuMvpView view;
    private Context context;

    public MainMenuPresenter(MainMenuMvpView view) {
        this.view = view;
        context = ((MainMenuFragment)view).getContext();
    }

    public void init() {

        view.onItemsGet(getMainMenuInfo());initUser();
    }
    public void reflesh() {
        initUser();
    }

    public User checkUserState(User user) {
        return user;
    }
    public void logOut(User user) {
        logOutReal(user);
        initUser();
    }

    //获取侧滑菜单里listview的信息
    private ArrayList<MainMenuInfo> getMainMenuInfo() {
        ArrayList<MainMenuInfo> list = new ArrayList<MainMenuInfo>();
        for (int i = 0; i < MainMenuInfo.re.length; i++) {
            MainMenuInfo info = new MainMenuInfo();
            info.setImgID(MainMenuInfo.re[i]);
            info.setTitle(MainMenuInfo.titles[i]);
            info.setAnotation(MainMenuInfo.anotations[i]);
            info.setHasDirection(MainMenuInfo.hasdirection[i]);
            list.add(info);
        }
        return list;
    }

    private void initUser() {
        SharedPreferences sp = MyApplication.getSharedPreferences();
        String uStr = sp.getString(MyApplication.SP_STRING_USER, "");
        if ("".equals(uStr)) {
            view.onLogIn(null);
        } else {
            view.onLogIn(checkUserState(User.toUser(uStr)));
        }
    }

    private void logOutReal(final User user) {

        new AsyncTask<Void,Void,String>(){

            @Override
            protected String doInBackground(Void... params) {
                try {
                    String urlStr = ((MainMenuFragment)view).getContext().getString(R.string.logout);
                    HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
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
                    String content = "sessionid=" + URLEncoder.encode(user.getToken() + "", "utf-8");
                    //将要上传的内容写入流中
                    out.writeBytes(content);
                    //刷新、关闭
                    out.flush();
                    out.close();

                    String message = readString(conn.getInputStream());
                    return message;

                } catch (Exception e) {
                    e.printStackTrace();
                    return "服务器登出异常";
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
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                view.onLogOut(user,s);
            }
        }.execute();
    }

    public void onDestory() {
    }

    /**
     * 用于异步加载头像
     * @param fileName     服务器传回来的文件名
     * @param token        服务器传回来的token认证标示
     */
    public void loadAvator(final String fileName,final String token) {
        //加载图标
        File f = new File(context.getCacheDir(), fileName);
        if (f.exists()) {
            //有缓存
            Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
            view.onAvatorGet(bitmap);
        }else{
            //网络上加载
            String url = context.getString(R.string.loadicon) + "?token=" + token;
            RequestQueue mQueue = Volley.newRequestQueue(context);
            ImageRequest imageRequest = new ImageRequest(
                    url,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            try {
                                //缓存图片
                                BitmapUtil.saveBitmapToFile(context.getCacheDir().getAbsolutePath(), fileName, response);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            view.onAvatorGet(response);

                        }
                    }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    view.onAvatorGetError(error.getMessage());
                }
            });
            mQueue.add(imageRequest);
        }
    }
}
