package edu.ncu.safe.ui.fragment;


import android.app.ProgressDialog;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
public class NetManagerFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener, NetManagerLVAdapter.OnNetChangeClickedListener {
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

        adapter = new NetManagerLVAdapter(getActivity());
        lv_netManager.setAdapter(adapter);
        lv_netManager.setEmptyView(ll_empty);
        adapter.setOnNetChangeClickedListener(this);
        sp.registerOnSharedPreferenceChangeListener(this);
        return view;
    }

    /**
     * activity回调该方法用来通知fragment数据更新了
     * @param baseInfos
     * @param packName
     */
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




    private ProgressDialog dialog;
    /**
     * 当用户点击了item里的设置拦截按钮后由adapter回调
     * @param v
     */
    @Override
    public void onNetChangedClicked(View v) {
        if(dialog==null){
            dialog = new ProgressDialog(getActivity());
            dialog.setTitle(getResources().getString(R.string.dialog_title_setting_network));
        }
        dialog.show();
        if(!IpTable.hasRootAccess(getContext())){
            makeToast(getContext().getResources().getString(R.string.toast_error_no_root));
            dialog.dismiss();
            return;
        }
        int position = (int) v.getTag();
        switch (v.getId()){
            case R.id.iv_GPRS:
                String GPRSS = sp.getString(IpTable.PERFS_GPRS,"");
                if(!infos.get(position).isGPRS()){
                    //原来已经被屏蔽，将改程序的uid从sharedpreference里去掉
                    GPRSS = GPRSS.replace(infos.get(position).getUid() + "", "");
                    GPRSS = GPRSS.replace("||", "|");
                }else{
                    //原来未被屏蔽，将该程序的uid加入到sharedpreference里
                    GPRSS=GPRSS+"|"+infos.get(position).getUid();
                }
                infos.get(position).setIsGPRS(!infos.get(position).isGPRS());
                ((ImageView)v).setImageResource(infos.get(position).isGPRS() ? R.drawable.yes : R.drawable.no);
                SharedPreferences.Editor edit = sp.edit();
                edit.putString(IpTable.PERFS_GPRS,GPRSS);
                edit.apply();
                break;
            case R.id.iv_WIFI:
                String WIFIS = sp.getString(IpTable.PERFS_WIFI,"");
                if(!infos.get(position).isWIFI()){
                    WIFIS = WIFIS.replace(infos.get(position).getUid() + "","");
                    WIFIS =WIFIS.replace("||","|");
                }else{
                    WIFIS=WIFIS+"|"+infos.get(position).getUid();
                }
                infos.get(position).setIsWIFI(!infos.get(position).isWIFI());
                ((ImageView)v).setImageResource(infos.get(position).isWIFI() ? R.drawable.yes : R.drawable.no);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(IpTable.PERFS_WIFI,WIFIS);
                editor.apply();
                break;
        }
    }
    //监听sp是否变动
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //更新屏蔽情况
        IpTable.updateBlackIPTable(getActivity());
        dialog.dismiss();
    }

    private void makeToast(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
