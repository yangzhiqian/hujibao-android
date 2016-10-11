package edu.ncu.safe.ui.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.ncu.safe.R;
import edu.ncu.safe.adapter.NetManagerLVAdapter;
import edu.ncu.safe.customerview.MyProgressBar;
import edu.ncu.safe.db.dao.FlowsDatabase;
import edu.ncu.safe.domain.UserAppBaseInfo;
import edu.ncu.safe.domain.UserAppNetInfo;
import edu.ncu.safe.engine.IpTable;

/**
 * Created by Mr_Yang on 2016/5/19.
 */
public class NetManagerFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener, NetManagerLVAdapter.OnNetChangeClickedListener {
    private TextView tv_netNumbers;
    private ListView lv_netManager;
    private MyProgressBar mpbLoading;
    private TextView tv_empty;

    private NetManagerLVAdapter adapter;
    private List<UserAppNetInfo> infos;
    private SharedPreferences sp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_netmanager, null);
        tv_netNumbers = (TextView) view.findViewById(R.id.tv_netnumbers);
        lv_netManager = (ListView) view.findViewById(R.id.lv_netmanager);
        mpbLoading = (MyProgressBar) view.findViewById(R.id.mpb_loading);
        tv_empty = (TextView) view.findViewById(R.id.tv_empty);

        sp = getActivity().getSharedPreferences(IpTable.PERFS_NAME, Context.MODE_PRIVATE);

        adapter = new NetManagerLVAdapter(getActivity());
        lv_netManager.setAdapter(adapter);
        adapter.setOnNetChangeClickedListener(this);
        sp.registerOnSharedPreferenceChangeListener(this);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sp.unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * activity回调该方法用来通知fragment数据更新了
     *
     * @param baseInfos
     * @param packName
     */
    public void onDataChanged(List<UserAppBaseInfo> baseInfos, String packName) {
        if (packName != null) {
            //移除了
            packageRemoved(packName);
        } else {
            dataObtained(baseInfos);
        }
    }

    private void dataObtained(List<UserAppBaseInfo> baseInfos) {
        lv_netManager.setEmptyView(tv_empty);
        mpbLoading.setVisibility(View.GONE);
        if (baseInfos == null) {
            return;
        }
        //数据更新了
        infos = new ArrayList<UserAppNetInfo>();
        FlowsDatabase db = new FlowsDatabase(getContext());
        String GPRSS = sp.getString(IpTable.PERFS_GPRS, "");
        String WIFIS = sp.getString(IpTable.PERFS_WIFI, "");
        UserAppNetInfo info;
        for (UserAppBaseInfo baseInfo : baseInfos) {
            if (!baseInfo.isWifi()) {
                //没有网络权限，不显示
                continue;
            }
            int uid = baseInfo.getUid();
            String pkName = baseInfo.getPackName();
            Drawable icon = baseInfo.getIcon();
            String appName = baseInfo.getAppName();
            long flows = db.queryAllFlowsByAppUID(uid);
            boolean isGPRS = !GPRSS.contains(uid + "");
            boolean isWIFI = !WIFIS.contains(uid + "");
            info = new UserAppNetInfo(uid, icon, pkName, appName, flows, isGPRS, isWIFI);
            infos.add(info);
        }

        /**
         * 安流量使用量排序
         */
        Collections.sort(infos, new Comparator<UserAppNetInfo>() {
            @Override
            public int compare(UserAppNetInfo info1, UserAppNetInfo info2) {
                if (info1.getFlows() < info2.getFlows()) {
                    return 1;
                } else if (info1.getFlows() == info2.getFlows()) {
                    return 0;
                }
                return -1;
            }
        });
        adapter.setInfos(infos);
        tv_netNumbers.setText(infos.size() + "");
        adapter.notifyDataSetChanged();
    }

    /**
     * 有app被移除了
     * @param packageName
     */
    private void packageRemoved(String packageName) {
        UserAppNetInfo delInfo = null;
        for(UserAppNetInfo info :infos){
            if(info.getPackName().equals(packageName)){
                delInfo = info;
            }
        }
        if(delInfo!=null){
            infos.remove(delInfo);
            adapter.setInfos(infos);
            adapter.notifyDataSetChanged();
        }
    }


    /**
     * 当用户点击了item里的设置拦截按钮后由adapter回调
     *
     * @param v
     */
    @Override
    public void onNetChangedClicked(View v) {
        if (!IpTable.hasRootAccess(getContext())) {
            makeToast(getContext().getResources().getString(R.string.toast_error_no_root));
            return;
        }
        int position = (int) v.getTag();
        switch (v.getId()) {
            case R.id.iv_GPRS:
                String GPRSS = sp.getString(IpTable.PERFS_GPRS, "");
                if (!infos.get(position).isGPRS()) {
                    //原来已经被屏蔽，将改程序的uid从sharedpreference里去掉
                    GPRSS = GPRSS.replace(infos.get(position).getUid() + "", "");
                    GPRSS = GPRSS.replace("||", "|");
                } else {
                    //原来未被屏蔽，将该程序的uid加入到sharedpreference里
                    GPRSS = GPRSS + "|" + infos.get(position).getUid();
                }
                infos.get(position).setIsGPRS(!infos.get(position).isGPRS());
                ((ImageView) v).setImageResource(infos.get(position).isGPRS() ? R.drawable.yes : R.drawable.no);
                SharedPreferences.Editor edit = sp.edit();
                edit.putString(IpTable.PERFS_GPRS, GPRSS);
                edit.apply();
                break;
            case R.id.iv_WIFI:
                String WIFIS = sp.getString(IpTable.PERFS_WIFI, "");
                if (!infos.get(position).isWIFI()) {
                    WIFIS = WIFIS.replace(infos.get(position).getUid() + "", "");
                    WIFIS = WIFIS.replace("||", "|");
                } else {
                    WIFIS = WIFIS + "|" + infos.get(position).getUid();
                }
                infos.get(position).setIsWIFI(!infos.get(position).isWIFI());
                ((ImageView) v).setImageResource(infos.get(position).isWIFI() ? R.drawable.yes : R.drawable.no);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(IpTable.PERFS_WIFI, WIFIS);
                editor.apply();
                break;
        }
    }

    //监听sp是否变动
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //更新屏蔽情况
        IpTable.updateBlackIPTable(getActivity());
    }

    private void makeToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
