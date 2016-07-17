package edu.ncu.safe.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
import edu.ncu.safe.View.CircleImageView;
import edu.ncu.safe.View.MyProgressBar;
import edu.ncu.safe.adapter.MainMenuAdapter;
import edu.ncu.safe.domain.MainMenuInfo;
import edu.ncu.safe.domain.User;
import edu.ncu.safe.ui.LoginActivity;
import edu.ncu.safe.ui.MainActivity;
import edu.ncu.safe.ui.ShareActivity;
import edu.ncu.safe.util.BitmapUtil;
import edu.ncu.safe.util.FlowsFormartUtil;

/**
 * Created by Mr_Yang on 2016/7/12.
 */
public class MainMenuFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    private ImageView iv_icon;
    private TextView tv_name;
    private MyProgressBar mpb_memory;
    private ListView lv;
    private Button bt_login;
    private MainMenuAdapter adapter;

    private User user;
    private MainActivity activity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View menuView = LayoutInflater.from(getActivity()).inflate(
                R.layout.besidelayout_mainmenu, null);
        iv_icon = (CircleImageView) menuView.findViewById(R.id.iv_icon);
        tv_name = (TextView) menuView.findViewById(R.id.tv_name);
        mpb_memory = (MyProgressBar) menuView.findViewById(R.id.mpb_memory);
        lv = (ListView) menuView.findViewById(R.id.main_menu_lv);
        bt_login = (Button) menuView.findViewById(R.id.bt_login);

        adapter = new MainMenuAdapter(getActivity(), getMainMenuInfo());
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
        bt_login.setOnClickListener(this);
        return menuView;
    }

    @Override
    public void onResume() {
        super.onResume();
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


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0:
                activity.startActivity(new Intent(activity, ShareActivity.class));
                activity.overridePendingTransition(R.anim.activit3dtoleft_in,
                        R.anim.activit3dtoleft_out);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (user == null) {
            activity.startActivityForResult(new Intent(activity, LoginActivity.class), 1);
            activity.overridePendingTransition(R.anim.activit3dtoleft_in,
                    R.anim.activit3dtoleft_out);
        } else {
            SharedPreferences sp = MyApplication.getSharedPreferences();
            SharedPreferences.Editor edit = sp.edit();
            edit.putString(MyApplication.SP_STRING_USER, "");
            edit.apply();
            logout(user);
            initUser();
        }
    }

    private void logout(final User user) {
        new Thread() {
            @Override
            public void run() {
                try {
                    String urlStr = activity.getResources().getString(R.string.logout);
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

                } catch (Exception e) {
                    e.printStackTrace();
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
        }.start();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode + requestCode == 0) {
            return;
        }
        if(data==null){
            return;
        }
        user = (User) data.getExtras().getSerializable("user");
        if (user == null) {
            return;
        }
        //保存到sp中
        SharedPreferences sp = MyApplication.getSharedPreferences();
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(MyApplication.SP_STRING_USER, user.toJson());
        edit.commit();
        initUser(user);
    }


    private void initUser() {
        SharedPreferences sp = MyApplication.getSharedPreferences();
        String uStr = sp.getString(MyApplication.SP_STRING_USER, "");
        if ("".equals(uStr)) {
            initUser(null);
            return;
        }
        initUser(User.toUser(uStr));
    }

    private void initUser(User user) {
        this.user = user;
        if (user == null) {
            user = new User();
        }

        tv_name.setText(user.getName());
        float percent = (float) (user.getUsed() * 100.0 / user.getTotal());
        mpb_memory.setPercentSlow(percent);
        mpb_memory.setTitle("云空间 " + FlowsFormartUtil.toMBFormat(user.getUsed()) + "M/" + FlowsFormartUtil.toMBFormat(user.getTotal()) + "M");
        String url = activity.getResources().getString(R.string.loadicon) + "?token=" + user.getToken();
        RequestQueue mQueue = Volley.newRequestQueue(activity);

        final String fileName = user.getIconUrl();
        File f = new File(getContext().getCacheDir(), fileName);
        iv_icon.setImageResource(R.drawable.appicon);
        if (f.exists()&&!"".equals(fileName)) {
            Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
            iv_icon.setImageBitmap(bitmap);
        } else {
            ImageRequest imageRequest = new ImageRequest(
                    url,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            try {
                                BitmapUtil.saveBitmapToFile(getContext().getCacheDir().getAbsolutePath(), fileName, response);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            iv_icon.setImageBitmap(response);

                        }
                    }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    iv_icon.setImageResource(R.drawable.appicon);
                }
            });
            mQueue.add(imageRequest);
        }
        if (this.user == null) {
            bt_login.setText(getResources().getString(R.string.button_log_in));
        } else {
            bt_login.setText(getResources().getString(R.string.button_log_out));
        }
    }
}
