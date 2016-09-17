package edu.ncu.safe.ui.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import edu.ncu.safe.R;
import edu.ncu.safe.customerview.MyDialog;
import edu.ncu.safe.customerview.MyLoadBar;
import edu.ncu.safe.adapter.AppManagerLVAdapter;
import edu.ncu.safe.domain.UserAppBaseInfo;
import edu.ncu.safe.engine.LoadAppInfos;

/**
 * Created by Mr_Yang on 2016/5/19.
 */
public class AppManagerFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    private static final int NEWDATA = 1;
    private static final int UNINSTALLCODE = 1;
    private TextView tv_appNumbers;
    private LinearLayout ll_uninstall;
    private ListView lv_appManager;

    private LoadAppInfos loadAppInfos;
    private List<UserAppBaseInfo> infos;
    private OnDataChangeListener listener;

    private AppManagerLVAdapter adapter;
    private MyLoadBar loadBar;
    private LinearLayout ll_empty;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == NEWDATA) {
                listener.dataChange(infos,null);//通知activity已经加载了应用信息
                tv_appNumbers.setText(infos.size()+"");
                adapter.setInfos(infos);
                adapter.notifyDataSetChanged();
            }
        }
    };
    private List<Map.Entry<String, String>> pkns;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appmanager, null);
        tv_appNumbers = (TextView) view.findViewById(R.id.tv_appnumbers);
        ll_uninstall = (LinearLayout) view.findViewById(R.id.ll_uninstall);
        lv_appManager = (ListView) view.findViewById(R.id.lv_appmanager);
        ll_empty = (LinearLayout) view.findViewById(R.id.ll_empty);
        loadBar = (MyLoadBar) view.findViewById(R.id.lb_loadingbar);

        Animation operatingAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.clockwiserotate);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        operatingAnim.setDuration(1000);
        loadBar.startAnimation(operatingAnim);

        loadAppInfos = new LoadAppInfos(getActivity());
        adapter = new AppManagerLVAdapter(getActivity());
        lv_appManager.setAdapter(adapter);
        lv_appManager.setEmptyView(ll_empty);

        ll_uninstall.setOnClickListener(this);
        lv_appManager.setOnItemClickListener(this);
        addAppDeleteReceiver();
        loadData();
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (OnDataChangeListener) activity;
    }

    //加载数据
    private void loadData(){
        new Thread() {
            @Override
            public void run() {
                infos = loadAppInfos.getUserAppBaseInfo();
                Collections.sort(infos, new Comparator<UserAppBaseInfo>() {
                    @Override
                    public int compare(UserAppBaseInfo info1, UserAppBaseInfo info2) {
                        if (info1.getRunMemory() > info2.getRunMemory()) {
                            return -1;
                        }
                        if (info1.getRunMemory() == info2.getRunMemory()) {
                            return 0;
                        }
                        return 1;
                    }
                });
                Message message = new Message();
                message.what = NEWDATA;
                handler.sendMessage(message);
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_uninstall:
                pkns = adapter.getCheckedAppPackageName();
                if (pkns.size() == 0) {
                    //用户没有选择要卸载的程序
                    MyDialog dialog = new MyDialog(getContext());
                    dialog.setTitle(getResources().getString(R.string.dialog_title_normal_tip));
                    dialog.setMessage(getResources().getString(R.string.dialog_message_no_app_selected));
                    dialog.show();
                    return;
                }
                Map.Entry<String, String> entry = pkns.get(0);
                readyToUninstall(entry.getKey(), entry.getValue());
                break;
        }
    }

    private void readyToUninstall(String appName, final String packName) {
        final MyDialog dialog = new MyDialog(getContext());
        dialog.setTitle(getResources().getString(R.string.dialog_title_normal_tip));
        dialog.setCancelable(false);
        dialog.setMessage(String.format(getResources().getString(R.string.dialog_message_sure_to_uninstall),appName));
        dialog.setPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uninstall(packName);
                dialog.dismiss();
            }
        });
        dialog.setNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * 卸载应用程序
     */
    private void uninstall(String packName) {
        Uri uri = Uri.parse("package:" + packName);
        Intent intent = new Intent(Intent.ACTION_DELETE, uri);
        startActivity(intent);
    }

    /**
     * 注册监听器
     */
    private void addAppDeleteReceiver() {
        BroadcastReceiver mDeleteReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                PackageManager pm = context.getPackageManager();

                if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_ADDED)) {
                    //安装
                    String packageName = intent.getData().getSchemeSpecificPart();
                } else if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_REPLACED)) {
                    //替换
                    String packageName = intent.getData().getSchemeSpecificPart();
                } else if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_REMOVED)) {
                    //卸载成功
                    String packageName = intent.getData().getSchemeSpecificPart();
                    infos.remove(packageName);//移除该数据
                    adapter.notifyDataSetChanged();//更新界面
                    listener.dataChange(null,packageName);//通知activity有程序被卸载
                    pkns.remove(0);//从卸载表中移除该项
                    makeToast(String.format(getResources().getString(R.string.toast_succeed_to_uninstall),packageName));
                    if (pkns.size() > 0) {
                        Map.Entry<String, String> entry = pkns.get(0);
                        readyToUninstall(entry.getKey(), entry.getValue());
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        };//自定义的广播接收类，接收到结果后的操作
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addDataScheme("package");
        getActivity().registerReceiver(mDeleteReceiver, filter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        System.out.println("onItemClick");
        String uristr = "package:" + adapter.getInfos().get(position).getPackName();
        Uri packageURI = Uri.parse(uristr);
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,packageURI);
        startActivity(intent);
    }

    public interface OnDataChangeListener{
        public void dataChange(List<UserAppBaseInfo> infos,String packName);
    }

    private void makeToast(String message){
        Toast.makeText(getActivity(),message, Toast.LENGTH_SHORT).show();
    }
}
