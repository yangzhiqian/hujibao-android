package edu.ncu.safe.ui.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import edu.ncu.safe.R;
import edu.ncu.safe.adapter.AppManagerLVAdapter;
import edu.ncu.safe.customerview.ImageTextView;
import edu.ncu.safe.customerview.MyDialog;
import edu.ncu.safe.customerview.MyProgressBar;
import edu.ncu.safe.domain.UserAppBaseInfo;
import edu.ncu.safe.engine.LoadAppInfos;
import edu.ncu.safe.util.MyDialogHelper;
import edu.ncu.safe.util.MyUtil;

/**
 * Created by Mr_Yang on 2016/5/19.
 */
public class AppManagerFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    private TextView tv_appNumbers;
    private ImageTextView itv_uninstall;
    private ListView lv_appManager;
    private MyProgressBar mpbLoading;

    private OnDataChangeListener listener;
    private AppManagerLVAdapter adapter;
    private LoadTask task;//加载应用程序的信息的工具
    private BroadcastReceiver receiver;//监听卸载程序是否成功
    private List<Map.Entry<String, String>> pkns;//要卸载程序包名的集合

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appmanager, null);
        tv_appNumbers = (TextView) view.findViewById(R.id.tv_appnumbers);
        itv_uninstall = (ImageTextView) view.findViewById(R.id.itv_uninstall);
        lv_appManager = (ListView) view.findViewById(R.id.lv_appmanager);
        mpbLoading = (MyProgressBar) view.findViewById(R.id.mpb_loading);
        itv_uninstall.setOnClickListener(this);
        lv_appManager.setOnItemClickListener(this);

        adapter = new AppManagerLVAdapter(getContext());
        lv_appManager.setAdapter(adapter);
        addAppDeleteReceiver();
        loadData();
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (OnDataChangeListener) activity;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (task!=null){
            task.cancel(true);
            task=null;
        }
        getActivity().unregisterReceiver(receiver);
    }

    //加载数据
    private synchronized void loadData(){
        if(task!=null){
            //当前正在加载中
            return;
        }else{
            task = new LoadTask(getContext());
            task.execute();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.itv_uninstall:
                pkns = adapter.getCheckedAppPackageName();
                if (pkns.size() == 0) {
                    //用户没有选择要卸载的程序
                    MyDialogHelper.showNomalTipDialog(getContext(),
                            getResources().getString(R.string.dialog_title_normal_tip),
                            getResources().getString(R.string.dialog_message_no_app_selected));
                    return;
                }
                beginUninstall();
                break;
        }
    }

    /**
     * 从要卸载的集合中卸载，如果为空，则停止卸载
     */
    private void beginUninstall() {
        if(pkns.size() == 0){
            return;
        }
        Map.Entry<String, String> entry = pkns.get(0);
        readyToUninstall(entry.getKey(), entry.getValue());
    }

    private void readyToUninstall(String appName, final String packName) {
        final MyDialog dialog = new MyDialog(getContext());
        dialog.setTitle(getResources().getString(R.string.dialog_title_normal_tip));
        dialog.setCancelable(false);
        dialog.setMessage(String.format(getResources().getString(R.string.dialog_message_sure_to_uninstall),appName));
        dialog.setPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyUtil.unInstall(getContext(),packName);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * 注册监听器
     */
    private void addAppDeleteReceiver() {
        receiver = new UninstallReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addDataScheme("package");
        getActivity().registerReceiver(receiver, filter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MyUtil.openAppSettingActivity(getContext(),adapter.getInfos().get(position).getPackName());
    }

    class LoadTask extends AsyncTask<Void,Void,List<UserAppBaseInfo>>{
        private LoadAppInfos loadAppInfos;
        public LoadTask(Context context){
            this.loadAppInfos = new LoadAppInfos(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mpbLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<UserAppBaseInfo> doInBackground(Void... params) {
            List<UserAppBaseInfo> res = loadAppInfos.getUserAppBaseInfo();
            Collections.sort(res, new Comparator<UserAppBaseInfo>() {
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
            return res;
        }

        @Override
        protected void onPostExecute(List<UserAppBaseInfo> userAppBaseInfos) {
            super.onPostExecute(userAppBaseInfos);
            mpbLoading.setVisibility(View.GONE);
            listener.dataChange(userAppBaseInfos,null);//通知activity已经加载了应用信息
            tv_appNumbers.setText(userAppBaseInfos.size()+"");
            adapter.setInfos(userAppBaseInfos);
            adapter.notifyDataSetChanged();
            task=null;
        }
    }
    class UninstallReceiver extends BroadcastReceiver{
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
                List<UserAppBaseInfo> infos = adapter.getInfos();
                infos.remove(packageName);//移除该数据
                adapter.setInfos(infos);
                adapter.notifyDataSetChanged();//更新界面
                listener.dataChange(null,packageName);//通知activity有程序被卸载
                pkns.remove(0);//从卸载表中移除该项
                makeToast(String.format(getResources().getString(R.string.toast_succeed_to_uninstall),packageName));
                beginUninstall();//继续尝试卸载
            }
        }
    }
    public interface OnDataChangeListener{
        public void dataChange(List<UserAppBaseInfo> infos,String packName);
    }
    private void makeToast(String message){
        Toast.makeText(getActivity(),message, Toast.LENGTH_SHORT).show();
    }
}
