package edu.ncu.safe.myadapter;


import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.ncu.safe.R;
import edu.ncu.safe.customerview.MyProgressBar;
import edu.ncu.safe.adapter.BackupLVAdapter;
import edu.ncu.safe.domainadapter.ITarget;

import edu.ncu.safe.ui.fragment.ContactsBackupFragment;
import edu.ncu.safe.ui.fragment.MessageBackupFragment;
import edu.ncu.safe.ui.fragment.PictureBackupFragment;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import edu.ncu.safe.engine.NetDataOperator.BACKUP_TYPE;

/**
 * Created by Mr_Yang on 2016/6/1
 */
public abstract class BackupBaseFragment extends Fragment implements AdapterView.OnItemClickListener, BackupLVAdapter.OnAdapterEventListener {
    public static final int SHOWTYPE_LOCAL = 0;
    public static final int SHOWTYPE_CLOUD = 1;
    public static final int SHOWTYPE_RECOVERY = 2;
    public static final int SHOW_NUMBERS = 15;

    protected BACKUP_TYPE type;
    protected int showType = SHOWTYPE_LOCAL;

    protected PtrFrameLayout ptr;
    protected ListView lv;
    protected MyProgressBar mpb_load;
    protected LinearLayout ll_empty;
    protected BackupLVAdapter adapter;
    private PopupWindow popupWindow;

    private List<ITarget> cloudInfos;
    private List<ITarget> localInfos;
    private boolean isAllCloudDataLoaded = false;


    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_backup, null);
        ptr = (PtrFrameLayout) view.findViewById(R.id.ptr);
        lv = (ListView) view.findViewById(R.id.lv);
        mpb_load = (MyProgressBar) view.findViewById(R.id.mpb_load);
        ll_empty = (LinearLayout) view.findViewById(R.id.ll_empty);

        ptr.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                switch (showType) {
                    case SHOWTYPE_LOCAL:
                        loadLocalInfos();//刷新
                        break;
                    default:
                        cloudInfos.clear();
                        isAllCloudDataLoaded = false;
                        loadCloudInfos(0, SHOW_NUMBERS);
                        break;
                }
            }
        });

        adapter = new BackupLVAdapter(getActivity().getApplicationContext());
        lv.setAdapter(adapter);

        adapter.setOnAdapterEventListener(this);
        lv.setOnItemClickListener(this);
        init();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateView();
    }

    /**
     * 当用户点击了本地调用显示本地内容
     */
    protected void showLocal() {
        showType = SHOWTYPE_LOCAL;
        adapter.setInfos(localInfos);
        adapter.notifyDataSetChanged();
        //切换是不可能正在刷新
        if(ptr.isRefreshing()){
            ptr.refreshComplete();
        }
        //第一次显示，要加载
        if (localInfos == null) {
            showLoader(true);
            loadLocalInfos();
        }
    }

    /**
     * 当用户点击了网络调用显示网络内容
     */
    protected void showCloud() {
        showType = SHOWTYPE_CLOUD;
        adapter.setInfos(cloudInfos);
        adapter.notifyDataSetChanged();
        if(ptr.isRefreshing()){
            ptr.refreshComplete();
        }
        if (cloudInfos == null) {
            showLoader(true);
            loadCloudInfos(0, SHOW_NUMBERS);
        }
    }

    /**
     * 显示网络上有但本地没有的数据
     */
    protected void showRecovery() {
        showType = SHOWTYPE_RECOVERY;
        List<ITarget> infos = loadRecoveryInfos();
        adapter.setInfos(infos);
        adapter.notifyDataSetChanged();
        if(ptr.isRefreshing()){
            ptr.refreshComplete();
        }
        if (infos.size()==0 && !isAllCloudDataLoaded) {
            showLoader(true);
            loadCloudInfos(0, SHOW_NUMBERS*2);
        }
    }

    private void updateView(){
        switch (showType){
            case SHOWTYPE_LOCAL:
                showLocal();
                break;
            case SHOWTYPE_CLOUD:
                showCloud();
                break;
            case SHOWTYPE_RECOVERY:
                showRecovery();
                break;
        }
    }


    protected void onLocalInfosLoaded(List<ITarget> infos){
        showLoader(false);
        this.localInfos  = infos;
        updateView();
    }

    protected void onCloudInfosLoaded(List<ITarget> infos,boolean isOver){
        showLoader(false);
        this.cloudInfos.addAll(infos);
        isAllCloudDataLoaded = isOver;
        updateView();
    }

    private  List<ITarget> loadRecoveryInfos(){
        List<ITarget> infos = new ArrayList<ITarget>();
        for (ITarget cloudInfo : cloudInfos) {
            for (int i = 0; i < localInfos.size(); i++) {
                if(isSameInfo(cloudInfo,localInfos.get(i))){
                    break;
                }
                if(i==localInfos.size()-1){
                    infos.add(cloudInfo);
                }
            }
        }
        return infos;
    }

    private PopupWindow createAdaptedPopupWindowAndShow(final View parent,final View  view,final int position,ITarget info){
        TextView tv = new TextView(getActivity());
        tv.setText("无操作");
        View contentView = tv;
        switch (showType){
            case SHOWTYPE_LOCAL:
                contentView = createShowLocalPopupWindowContentView(parent,position,info);
                break;
            case SHOWTYPE_CLOUD:
                contentView = createShowCloudPopupWindowContentView(parent,position,info);
                break;
            case SHOWTYPE_RECOVERY:
                contentView = createShowRecoveryPopupWindowContentView(parent,position,info);
                break;
        }
         popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //popupWindow设置animation一定要在show之前
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        //一定要设置背景，否则无法自动消失
        Drawable background = getResources().getDrawable(R.drawable.popupbgright);
        view.measure(0,0);
        contentView.measure(0,0);
        popupWindow.setBackgroundDrawable(background);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ((ImageView)view).setImageResource(R.drawable.close);
            }
        });
        ((ImageView)view).setImageResource(R.drawable.expand);
        popupWindow.showAsDropDown(view,
               20 +  (-1 *contentView.getMeasuredWidth()),
                -1 * (contentView.getMeasuredHeight() + view.getHeight()) / 2 );
        return popupWindow;
    }
    protected LinearLayout getPopupWindowLayout() {
        LinearLayout layout = new LinearLayout(getActivity());
        ViewGroup.LayoutParams parms = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(parms);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER_VERTICAL);
        layout.setPadding(20, 20, 50, 20);
        return layout;
    }
    protected TextView getPopupWindowTextView(String text) {
        TextView tv = new TextView(getActivity());
        ViewGroup.LayoutParams parms = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(parms);
        tv.setText(text);
        tv.setTextColor(Color.parseColor("#FFFFFF"));
        tv.setTextSize(20);
        return tv;
    }
    protected View getPopupWindowDivider() {
        View view = new View(getActivity());
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(1, ViewGroup.LayoutParams.MATCH_PARENT);
        parms.setMargins(10, 0, 10, 0);
        view.setLayoutParams(parms);
        view.setBackgroundColor(Color.parseColor("#aaaaaa"));
        return view;
    }

    protected void showLoader(boolean b) {
        if (b) {
            lv.setEmptyView(mpb_load);
            mpb_load.setVisibility(View.VISIBLE);
            ll_empty.setVisibility(View.GONE);
        } else {
            lv.setEmptyView(ll_empty);
            ll_empty.setVisibility(View.VISIBLE);
            mpb_load.setVisibility(View.GONE);
        }
    }
    protected void dissmissPopupWindow(){
        if(popupWindow.isShowing()){
            popupWindow.dismiss();
        }
    }

    protected void onProgressStateChanged(int position,boolean isShow){
        adapter.onProgressStateChanged(position,isShow);
    }
    protected void onProgressChanged(int position,int percent){
        adapter.onProgressChanged(position,percent);
    }



    public abstract void init();
    protected abstract void loadLocalInfos();
    protected abstract void loadCloudInfos(int beginIndex,int endIndex);
    protected abstract boolean isSameInfo(ITarget target1,ITarget target2);
    protected abstract View createShowLocalPopupWindowContentView(final View parent,final int position,final ITarget info);
    protected abstract View createShowCloudPopupWindowContentView(final View parent,final int position,final ITarget info);
    protected abstract View createShowRecoveryPopupWindowContentView(final View parent,final int position,final ITarget info);

    @Override
    public void onShowPopupClicked(View parent, View view, int position, ITarget info) {
        createAdaptedPopupWindowAndShow(parent,view,position,info);
    }
    @Override
    public void onDownloadProgressBarClicked(View parent, int position, ITarget info) {
    }
    @Override
    public void onCheckBoxCheckedChanged(View parent, int position, ITarget data, boolean isChecked) {
    }


    public static class BackUpFragmentFactory{
        public static BackupBaseFragment createFragment(BACKUP_TYPE type){
            switch (type){
                case TYPE_PICURE:
                    return PictureBackupFragment.newInstance(BACKUP_TYPE.TYPE_PICURE);
                case TYPE_SMS:
                    return MessageBackupFragment.newInstance(BACKUP_TYPE.TYPE_SMS);
                case TYPE_CONTACT:
                    return ContactsBackupFragment.newInstance(BACKUP_TYPE.TYPE_CONTACT);
                default:return null;
            }
        }
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(adapter.isShowMultiChoice()){
            BackupLVAdapter.ViewHolder holder = (BackupLVAdapter.ViewHolder) view.getTag();
            holder.cb_check.setChecked(!holder.cb_check.isChecked());
            return;
        }
    }

    protected void makeToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
