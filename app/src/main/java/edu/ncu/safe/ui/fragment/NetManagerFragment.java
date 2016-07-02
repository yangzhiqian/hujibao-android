package edu.ncu.safe.ui.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.ncu.safe.R;
import edu.ncu.safe.View.MyLoadBar;
import edu.ncu.safe.adapter.NetManagerLVAdapter;
import edu.ncu.safe.db.dao.FlowsDatabase;
import edu.ncu.safe.domain.UserAppBaseInfo;
import edu.ncu.safe.domain.UserAppNetInfo;
import edu.ncu.safe.engine.IpTable;

/**
 * Created by Mr_Yang on 2016/5/19.
 */
public class NetManagerFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private TextView tv_netNumbers;
    private ListView lv_netManager;
    private LinearLayout ll_empty;
    private MyLoadBar loadBar ;


    private NetManagerLVAdapter adapter;
    private List<UserAppNetInfo> infos;
    private SharedPreferences sp;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_netmanager,null);
        tv_netNumbers = (TextView) view.findViewById(R.id.tv_netnumbers);
        lv_netManager = (ListView) view.findViewById(R.id.lv_netmanager);
        ll_empty = (LinearLayout) view.findViewById(R.id.ll_empty);
        loadBar = (MyLoadBar) view.findViewById(R.id.lb_loadingbar);

        sp = getActivity().getSharedPreferences(IpTable.PERFS_NAME, Context.MODE_PRIVATE);

        Animation operatingAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.clockwiserotate);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        operatingAnim.setDuration(1000);
        loadBar.startAnimation(operatingAnim);

        adapter = new NetManagerLVAdapter(getActivity(),sp);
        lv_netManager.setAdapter(adapter);
        lv_netManager.setEmptyView(ll_empty);
        sp.registerOnSharedPreferenceChangeListener(this);
        return view;
    }

    public void onDataChanged(List<UserAppBaseInfo> baseInfos,String packName){
        if(packName!=null){
            if(infos==null){
                infos = new ArrayList<UserAppNetInfo>();
            }
        }
        if(baseInfos!=null){
            infos = new ArrayList<UserAppNetInfo>();
            FlowsDatabase db = new FlowsDatabase(getContext());
            String GPRSS  = sp.getString(IpTable.PERFS_GPRS, "");
            String WIFIS = sp.getString(IpTable.PERFS_WIFI,"");
            UserAppNetInfo info ;
            for(UserAppBaseInfo baseInfo:baseInfos){
                if(!baseInfo.isWifi()){
                    continue;
                }
                int uid = baseInfo.getUid();
                String pkName = baseInfo.getPackName();
                Drawable icon = baseInfo.getIcon();
                String appName = baseInfo.getAppName();
                long flows = db.queryAllFlowsByAppUID(uid);
                boolean isGPRS = !GPRSS.contains(uid+"");
                boolean isWIFI = !WIFIS.contains(uid+"");
                info =  new UserAppNetInfo(uid, icon, packName, appName, flows, isGPRS, isWIFI);
                infos.add(info);
            }
        }
        Collections.sort(infos, new Comparator<UserAppNetInfo>() {
            @Override
            public int compare(UserAppNetInfo info1, UserAppNetInfo info2) {
                if(info1.getFlows()<info2.getFlows()){
                    return 1;
                }else if(info1.getFlows()==info2.getFlows()){
                    return 0;
                }
                return -1;
            }
        });
        adapter.setInfos(infos);
        tv_netNumbers.setText(infos.size()+"");
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        IpTable.updateBlackIPTable(getActivity());
    }
}
