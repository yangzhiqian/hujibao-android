package edu.ncu.safe.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.GridView;

import edu.ncu.safe.MyApplication;
import edu.ncu.safe.R;
import edu.ncu.safe.View.MyDialog;
import edu.ncu.safe.adapter.MainGVAdapter;
import edu.ncu.safe.domain.MainGVItemInfo;
import edu.ncu.safe.ui.AppManagerAcitvity;
import edu.ncu.safe.ui.BackUpsActivity;
import edu.ncu.safe.ui.CommunicationProtectorActivity;
import edu.ncu.safe.ui.FlowsProtectorActivity;
import edu.ncu.safe.ui.MainActivity;
import edu.ncu.safe.ui.PhoneLostProtectActivity;
import edu.ncu.safe.ui.SystemQuickenActivity;
import edu.ncu.safe.util.MD5Encoding;

/**
 * Created by Mr_Yang on 2016/7/12.
 */
public class MainFragment extends Fragment {
    private static final MainGVItemInfo[] infos = {
            new MainGVItemInfo(R.drawable.phoneprotector, "手机防盗", "防盗未开启", Color.parseColor("#ff0000"), "toPhoneProtector", PhoneLostProtectActivity.class),
            new MainGVItemInfo(R.drawable.gprsflows, "流量监控", "监控未开启", Color.parseColor("#ff0000"), null, FlowsProtectorActivity.class),
            new MainGVItemInfo(R.drawable.databackup, "数据备份", "", Color.parseColor("#ff0000"), null, BackUpsActivity.class),
            new MainGVItemInfo(R.drawable.communication, "通讯卫士", "", Color.parseColor("#ff0000"), null, CommunicationProtectorActivity.class),
            new MainGVItemInfo(R.drawable.softwaremanager, "软件管理", "", Color.parseColor("#ff0000"), null, AppManagerAcitvity.class),
            new MainGVItemInfo(R.drawable.systemfaster, "手机加速", "", Color.parseColor("#ff0000"), null, SystemQuickenActivity.class)
    };

    private MainActivity activity;
    private GridView gv;// 主界面下方的六个选项
    private MainGVAdapter adapter;// gv的数据适配器

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.fragment_main, null);
        gv = (GridView) view.findViewById(R.id.gv_main);
        adapter = new MainGVAdapter(getContext());
        gv.setAdapter(adapter);
        gv.setSelector(new ColorDrawable(Color.TRANSPARENT));// 去掉gridview点击后出现的黄色边框
        gv.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(activity, R.anim.gv_item_appear));
        //设置gv点击事件
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String method = infos[position].getInvokeMethod();
                if (method != null) {
                    try {//反射
                        MainFragment.class.getMethod(method).invoke(MainFragment.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {//直接进入
                    toAntherActivity(infos[position].getClazz());
                }
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refrash();
        gv.startLayoutAnimation();//展现动画
        adapter.setInfos(infos);//更新界面信息
        adapter.notifyDataSetChanged();
    }


    private void refrash() {
        SharedPreferences sp = MyApplication.getSharedPreferences();
        boolean isInProtecting = sp.getBoolean(MyApplication.SP_BOOLEAN_IS_IN_PROTECTING, false);
        if (isInProtecting) {
            infos[0].setNote(getResources().getString(R.string.main_fragment_note_phone_lost_in_protecting));
            infos[0].setColor(getResources().getColor(R.color.state_ok));
        }
        long flows = sp.getLong(MyApplication.SP_LONG_TOTAL_FLOWS, 0);
        if (flows > 0) {
            infos[1].setNote(getResources().getString(R.string.main_fragment_note_flows_in_protecting));
            infos[1].setColor(getResources().getColor(R.color.state_ok));
        }
    }

    /**
     * 进入手机防盗要先进行验证
     */
    public void toPhoneProtector() {
        SharedPreferences sp = MyApplication.getSharedPreferences();
        boolean hasSetPWD = sp.getBoolean(
                MyApplication.SP_BOOLEAN_HAS_PWD, false);
        if (hasSetPWD) {// 已经设置过密码
            showInputPWDDialog();
        } else {// 还未设置密码
            showSetPWDDialog();
        }
    }


    private void showSetPWDDialog() {
        final MyDialog myDialog = new MyDialog(activity);
        myDialog.setTitle(getResources().getString(R.string.dialog_enter_pwd));
        final View view = LayoutInflater.from(activity).inflate(R.layout.dialog_passwordregister, null);
        final AutoCompleteTextView pwd_one = (AutoCompleteTextView) view.findViewById(R.id.actv_pwd_one);
        final AutoCompleteTextView pwd_two = (AutoCompleteTextView) view.findViewById(R.id.actv_pwd_two);
        myDialog.setMessageView(view);
        myDialog.setPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pd = pwd_one.getText().toString().trim();
                String pdAgain = pwd_two.getText().toString().trim();
                if (pd.equals(pdAgain)) {
                    SharedPreferences sp = MyApplication.getSharedPreferences();
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(MyApplication.SP_STRING_PWD, MD5Encoding.encoding(pd));
                    editor.putBoolean(MyApplication.SP_BOOLEAN_HAS_PWD, true);
                    editor.apply();
                    myDialog.dismiss();
                    // 进入界面
                    toAntherActivity(PhoneLostProtectActivity.class);
                    myDialog.dismiss();
                    return;
                } else {
                    pwd_two.setError(getResources().getString(R.string.error_pwd_different));
                }
            }
        });
        myDialog.show();

    }

    public void showInputPWDDialog() {
        final MyDialog myDialog = new MyDialog(activity);
        myDialog.setTitle(getResources().getString(R.string.dialog_input_pwd));
        final View view = LayoutInflater.from(activity).inflate(R.layout.dialog_passwordenter, null);
        final AutoCompleteTextView pwd = (AutoCompleteTextView) view.findViewById(R.id.actv_pwd);
        myDialog.setMessageView(view);
        myDialog.setPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = MyApplication.getSharedPreferences();
                String enterPwd = sp.getString(
                        MyApplication.SP_STRING_PWD, "");
                String pd = pwd.getText().toString().trim();
                if (enterPwd.equals(MD5Encoding.encoding(pd))) {// 正确输入密码，进入界面
                    toAntherActivity(PhoneLostProtectActivity.class);
                    myDialog.dismiss();
                    return;
                }
                pwd.setError(getResources().getString(R.string.error_pwd));
            }
        });
        myDialog.show();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) activity;
    }

    //实现跳转
    private void toAntherActivity(Class cls) {
        Intent intent = new Intent();
        intent.setClass(activity, cls);
        startActivity(intent);
        //切换动画
        activity.overridePendingTransition(R.anim.activit3dtoleft_in,
                R.anim.activit3dtoleft_out);
    }
}
