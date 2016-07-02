package edu.ncu.safe.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import edu.ncu.safe.R;
import edu.ncu.safe.adapter.MainGVAdapter;
import edu.ncu.safe.domain.MainGVItemInfo;

public class MainActivity extends Activity implements
        View.OnClickListener {
    public static final String TAG = "MainActivity";
    private static final MainGVItemInfo[] infos = {
            new MainGVItemInfo(R.drawable.phoneprotector, "手机防盗", "防盗未开启", Color.parseColor("#ff0000"), "toPhoneProtector", PhoneLostProtectActivity.class),
            new MainGVItemInfo(R.drawable.gprsflows, "流量监控", "监控未开启", Color.parseColor("#ff0000"), null, FlowsProtectorActivity.class),
            new MainGVItemInfo(R.drawable.databackup, "数据备份", "", Color.parseColor("#ff0000"), null, BackUpsActivity.class),
            new MainGVItemInfo(R.drawable.communication, "通讯卫士", "", Color.parseColor("#ff0000"), null, CommunicationProtectorActivity.class),
            new MainGVItemInfo(R.drawable.softwaremanager, "软件管理", "", Color.parseColor("#ff0000"), null, AppManagerAcitvity.class),
            new MainGVItemInfo(R.drawable.systemfaster, "手机防盗", "", Color.parseColor("#ff0000"), null, SystemQuickenActivity.class)
    };
    // 主界面控件
    private ImageView user;// 开启侧滑菜单
    private ImageView set;// 右上角设置图标

    private GridView gv;// 主界面下方的六个选项
    private MainGVAdapter adapter;// gv的数据适配器

    // 滑动菜单控件
    private SlidingMenuView menu;// 侧滑菜单
    private SharedPreferences sp;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 初始化各个成员函数
        user = (ImageView) this.findViewById(R.id.user);
        set = (ImageView) this.findViewById(R.id.set);
        gv = (GridView) this.findViewById(R.id.main_gv);
        sp = getSharedPreferences("conf", Context.MODE_MULTI_PROCESS);
        // 给gridview添加数据适配器和项目点击事件
        adapter = new MainGVAdapter(this);
        gv.setAdapter(adapter);
        gv.setSelector(new ColorDrawable(Color.TRANSPARENT));// 去掉gridview点击后出现的黄色边框
        user.setOnClickListener(this);
        set.setOnClickListener(this);
        //添加layoutanimation
        gv.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this, R.anim.gv_item_appear));

        //添加侧滑菜单
        menu = new SlidingMenuView(this);
        //设置gv点击事件
        gv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String method = infos[position].getInvokeMethod();
                if (method != null) {
                    try {//反射
                        MainActivity.class.getMethod(method).invoke(MainActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {//直接进入
                    toAntherActivity(infos[position].getClazz());
                }
            }
        });
        // 检查是否有新版本信息，有则弹出升级对话框
        startService(new Intent("edu.ncu.myservice.update"));
    }
    @Override
    protected void onStart() {
        super.onStart();
        refrash();
        gv.startLayoutAnimation();//展现动画
        adapter.setInfos(infos);//更新界面信息
        adapter.notifyDataSetChanged();
    }

    private void refrash(){
        SharedPreferences sp = getSharedPreferences(PhoneLostProtectActivity.SHAREPERFERENCESNAME,Context.MODE_MULTI_PROCESS);
        boolean isInProtecting = sp.getBoolean(PhoneLostProtectActivity.ISINPROTECTING, false);
        if(isInProtecting){
            infos[0].setNote("手机保护中");
            infos[0].setColor(Color.parseColor("#00ff00"));
        }

        sp = getSharedPreferences(FlowsProtectorActivity.FLOWSSHAREDPREFERENCES,Context.MODE_MULTI_PROCESS);
        long flows = sp.getLong(FlowsProtectorActivity.FLOWSTOTAL, 0);
        if(flows>0){
            infos[1].setNote("流量监控中");
            infos[1].setColor(Color.parseColor("#00ff00"));
        }
    }

    public void toPhoneProtector() {
        SharedPreferences sp = MainActivity.this.getSharedPreferences(
                PhoneLostProtectActivity.SHAREPERFERENCESNAME,
                Context.MODE_MULTI_PROCESS);
        boolean hasSetPWD = sp.getBoolean(
                PhoneLostProtectActivity.HASSETPWD, false);
        if (hasSetPWD) {// 已经设置过密码
            showInputPWDDialog();
        } else {// 还未设置密码
            showSetPWDDialog();
        }
    }


    // 上方两个点击事件 user set
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user:
                menu.toggle();
                break;
            case R.id.set:
                break;
            default:
                break;
        }
    }

    private void showSetPWDDialog() {
        final Dialog dialog = new Dialog(this, R.style.MyDialog);
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_passwordregister, null);
        final EditText pwd = (EditText) v.findViewById(R.id.pwd);
        final EditText pwdAgain = (EditText) v.findViewById(R.id.pwdagain);
        Button btnOK = (Button) v.findViewById(R.id.yes);
        Button btnCancle = (Button) v.findViewById(R.id.no);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pd = pwd.getText().toString().trim();
                String pdAgain = pwdAgain.getText().toString().trim();
                if (pd.equals(pdAgain)) {
                    SharedPreferences sp = MainActivity.this
                            .getSharedPreferences(
                                    PhoneLostProtectActivity.SHAREPERFERENCESNAME,
                                    Context.MODE_MULTI_PROCESS);
                    Editor editor = sp.edit();
                    editor.putString(PhoneLostProtectActivity.ENTERPWD, pd);
                    editor.putBoolean(PhoneLostProtectActivity.HASSETPWD, true);
                    editor.apply();
                    dialog.dismiss();
                    // 进入界面
                    toAntherActivity(PhoneLostProtectActivity.class);
                    dialog.dismiss();
                    return;
                } else {
                    makeToast("两次密码不相同，请重新输入");
                }
            }
        });

        btnCancle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setContentView(v);
        dialog.show();
    }

    public void showInputPWDDialog() {
        final Dialog dialog = new Dialog(this, R.style.MyDialog);
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_passwordenter, null);
        final EditText pwd = (EditText) v.findViewById(R.id.pwd);
        Button btnOK = (Button) v.findViewById(R.id.enter_yes);
        Button btnCancle = (Button) v.findViewById(R.id.enter_no);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = MainActivity.this.getSharedPreferences(
                        PhoneLostProtectActivity.SHAREPERFERENCESNAME,
                        Context.MODE_MULTI_PROCESS);
                String enterPwd = sp.getString(
                        PhoneLostProtectActivity.ENTERPWD, "");
                String pd = pwd.getText().toString().trim();
                if (enterPwd.equals(pd)) {// 正确输入密码，进入界面
                    toAntherActivity(PhoneLostProtectActivity.class);
                    dialog.dismiss();
                    return;
                }
                makeToast("密码输入错误！");
            }
        });

        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setContentView(v);
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1){
            //登录
            menu.onActivityResult(requestCode, resultCode, data);
        }




        super.onActivityResult(requestCode, resultCode, data);
    }

    //实现跳转
    private void toAntherActivity(Class cls) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        startActivity(intent);
        //切换动画
        overridePendingTransition(R.anim.activit3dtoleft_in,
                R.anim.activit3dtoleft_out);
    }

    //显示toast
    private void makeToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    //显示log
    private void log(String message) {
        Log.i(TAG, message);
    }


}