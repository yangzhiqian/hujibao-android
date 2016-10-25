package edu.ncu.safe.ui;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.ncu.safe.R;
import edu.ncu.safe.customerview.ImageTextView;
import edu.ncu.safe.customerview.MyProgressBar;
import edu.ncu.safe.adapter.SystemQuickenELVAdapter;
import edu.ncu.safe.domain.CacheInfo;
import edu.ncu.safe.domain.ELVParentItemInfo;
import edu.ncu.safe.domain.RunningApplicationInfo;
import edu.ncu.safe.myinterface.ChildItemData;
import edu.ncu.safe.service.AppRubbishManageService;
import edu.ncu.safe.service.InnerMemoryManageService;
import edu.ncu.safe.util.FlowsFormartUtil;
import edu.ncu.safe.util.MyUtil;

/**
 * Created by Mr_Yang on 2016/5/26.
 */
public class SystemQuickenActivity extends MyAppCompatActivity implements View.OnClickListener, SystemQuickenELVAdapter.OnItemCheckedListener, AbsListView.OnScrollListener {
    private MyProgressBar mpb_innerMemory;
    private MyProgressBar mpb_outerMemory;
    private ExpandableListView elv_sweepResult;
    private LinearLayout ll_sweep;
    private MyProgressBar mpb_sweep;
    private TextView tv_sweepContent;
    private ImageTextView itv_clean;

    private List<CacheInfo> cacheInfos = new ArrayList<>();
    private List<RunningApplicationInfo> runningApplicationInfos = new ArrayList<RunningApplicationInfo>();
    private List<ELVParentItemInfo> datas;
    private SystemQuickenELVAdapter adapter;

    private Animation animationAppear;
    private Animation animationDisappear;
    private int itemTask = 0;

    private BroadcastReceiver cacheManageReceiver;
    private BroadcastReceiver innerMemoryManageReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_systemquicken);
        initToolBar(getResources().getString(R.string.title_system_quick));
        //初始化控件
        mpb_innerMemory = (MyProgressBar) this.findViewById(R.id.mpb_innermemory);
        mpb_sweep = (MyProgressBar) this.findViewById(R.id.mpb_sweep);
        mpb_outerMemory = (MyProgressBar) this.findViewById(R.id.mpb_outermemory);
        ll_sweep = (LinearLayout) this.findViewById(R.id.ll_sweep);
        tv_sweepContent = (TextView) this.findViewById(R.id.tv_sweepContent);
        elv_sweepResult = (ExpandableListView) this.findViewById(R.id.elv_sweepResult);
        itv_clean = (ImageTextView) this.findViewById(R.id.itv_clean);

        animationAppear = AnimationUtils.loadAnimation(this, R.anim.cleanbuttonappear);
        ;
        animationDisappear = AnimationUtils.loadAnimation(this, R.anim.cleanbuttondisappear);

        //设置expandableListView适配器
        datas = new ArrayList<ELVParentItemInfo>();
        adapter = new SystemQuickenELVAdapter(getApplicationContext(), datas);
        elv_sweepResult.setAdapter(adapter);
        elv_sweepResult.setEmptyView(ll_sweep);
        //设置监听器
        itv_clean.setOnClickListener(this);
        adapter.setOnItemCheckedListener(this);
        elv_sweepResult.setOnScrollListener(this);
        //注册接收器
        registerReceivers();
        //扫描垃圾
        startService(AppRubbishManageService.class,getString(R.string.Action_Service_AppExternalRubbishScannStart));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //每次看见该activity时重新加载内存数据
        updateMemoryInfo();
    }

    private void registerReceivers() {
        cacheManageReceiver = new AppRubbishManageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(getString(R.string.Action_Broadcast_AppExternalRubbishScannBegin));
        filter.addAction(getString(R.string.Action_Broadcast_AppExternalRubbishScannInDoing));
        filter.addAction(getString(R.string.Action_Broadcast_AppExternalRubbishScannProgressUpdated));
        filter.addAction(getString(R.string.Action_Broadcast_AppExternalRubbishScannProgressEnd));
        filter.addAction(getString(R.string.Action_Broadcast_AppExternalRubbishScannEnd));
        filter.addAction(getString(R.string.Action_Broadcast_AppExternalRubbishCleanBegin));
        filter.addAction(getString(R.string.Action_Broadcast_AppExternalRubbishCleanInDoing));
        filter.addAction(getString(R.string.Action_Broadcast_AppExternalRubbishCleanAwait));
        filter.addAction(getString(R.string.Action_Broadcast_AppExternalRubbishCleanEnd));
        registerReceiver(cacheManageReceiver, filter);

        innerMemoryManageReceiver = new InnerMemoryManageReceiver();
        filter = new IntentFilter();
        filter.addAction(getString(R.string.Action_Broadcast_InnerMemoryRubbishScannBegin));
        filter.addAction(getString(R.string.Action_Broadcast_InnerMemoryRubbishScannInDoing));
        filter.addAction(getString(R.string.Action_Broadcast_InnerMemoryRubbishScannProgressUpdated));
        filter.addAction(getString(R.string.Action_Broadcast_InnerMemoryRubbishScannProgressEnd));
        filter.addAction(getString(R.string.Action_Broadcast_InnerMemoryRubbishScannEnd));
        filter.addAction(getString(R.string.Action_Broadcast_InnerMemoryRubbishCleanBegin));
        filter.addAction(getString(R.string.Action_Broadcast_InnerMemoryRubbishCleanInDoing));
        filter.addAction(getString(R.string.Action_Broadcast_InnerMemoryRubbishCleanProgressUpdated));
        filter.addAction(getString(R.string.Action_Broadcast_InnerMemoryRubbishCleanProgressEnd));
        filter.addAction(getString(R.string.Action_Broadcast_InnerMemoryRubbishCleanEnd));
        registerReceiver(innerMemoryManageReceiver, filter);

    }
    private void startService(Class clazz,String action){
        Intent intent = new Intent(this,clazz);
        intent.setAction(action);
        startService(intent);
    }

    /**
     * 刷新内存信息
     */
    private void updateMemoryInfo() {
        mpb_innerMemory.setPercentSlow((MyUtil.getPhoneUsedInnerMemory(getApplicationContext()) * 100.0f) / MyUtil.getPhoneTotalInnerMemory(getApplicationContext()));
        mpb_innerMemory.setTitle("总内存:" + FlowsFormartUtil.toFlowsFormart(MyUtil.getPhoneTotalInnerMemory(getApplicationContext())));

        mpb_outerMemory.setPercentSlow(MyUtil.getPhoneUsedExternalMemory() * 100f / MyUtil.getPhoneTotalExternalMemory());
        mpb_outerMemory.setTitle("内部存储:" + FlowsFormartUtil.toFlowsFormart(MyUtil.getPhoneTotalExternalMemory()));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.itv_clean:
                //点击了清理垃圾按钮，通知服务清理垃圾
                startService(AppRubbishManageService.class,getString(R.string.Action_Service_AppExternalRubbishCleanStart));
                //按钮消失动画
                itv_clean.setVisibility(View.GONE);
                itv_clean.startAnimation(animationDisappear);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(cacheManageReceiver);
        unregisterReceiver(innerMemoryManageReceiver);
    }


    //---------------------------------app缓存扫描回调---------------------------------
    //缓存垃圾开始扫描
    private void onCacheScanStart(int sumTask) {
        itemTask = sumTask;
        mpb_sweep.setTitle(getResources().getString(R.string.system_quick_cache_scanning));
        mpb_sweep.setPercentimmediately(0);
        tv_sweepContent.setText("0/" + itemTask);
        cacheInfos.clear();
    }

    //扫描任务已经开始了
    private void onCacheScanInDoing() {
        makeToast("扫描缓存任务已经开始了");
    }

    private void onCacheScanProgressUpdated(int index, int appUid, String taskName) {
        tv_sweepContent.setText(taskName + "(" + index + "/" + itemTask + ")");
        mpb_sweep.setPercentSlow(index * 100f / itemTask);
    }

    //单个app扫描完成
    private void onCacheScanProgressEnd(CacheInfo info) {
        cacheInfos.add(info);
    }

    private void onCacheScannEnd(long cacheSize) {
        Collections.sort(cacheInfos, new MyCompator());
        ELVParentItemInfo data = new ELVParentItemInfo();
        data.setItemName(getString(R.string.system_quick_item_cache));
        data.setIsChecked(true);
        data.setChilds(cacheInfos);
        datas.add(data);
        //开始扫描内存垃圾
        startService(InnerMemoryManageService.class,getString(R.string.Action_Service_InnerMemoryRubbishScannStart));
    }

    //------------------------------app缓存清理回调------------------------------
    private void onCacheCleanBegin() {
        mpb_sweep.setTitle(getResources().getString(R.string.system_quick_cache_cleaning));
        mpb_sweep.setPercentimmediately(0);
    }

    private void onCacheCleanInDoing() {
        makeToast("清除缓存任务已经开始了");
    }
    private void onCacheCleanAwait() {
    }

    private void onCacheCleanEnd(boolean result) {
        //启动内存清理
        Intent intent = new Intent(getString(R.string.Action_Service_InnerMemoryRubbishCleanStart));
        intent.setClass(this,InnerMemoryManageService.class);
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("CleanProcessNames",adapter.getCheckAppProcessNames());
        intent.putExtras(bundle);
        startService(intent);
    }

    ;

    //-------------------------------app内存垃圾扫描-----------------------------
    private void onInnerMemoryScanBegin(int sumTask) {
        mpb_sweep.setPercentimmediately(0);
        mpb_sweep.setTitle(getResources().getString(R.string.system_quick_inner_rubbish_scanning));
        tv_sweepContent.setText("0/" + itemTask);
        itemTask = sumTask;
        runningApplicationInfos.clear();
    }

    //扫描任务已经开始了
    private void onInnerMemoryScanInDoing() {
        makeToast("扫描内存垃圾任务已经开始了");
    }

    private void onInnerMemoryScanProgressUpdated(int index, int appUid, String appName) {
        tv_sweepContent.setText(appName + "(" + index + "/" + itemTask + ")");
        mpb_sweep.setPercentSlow(index * 100f / itemTask);
    }

    private void onInnerMemoryScanProgressEnd(RunningApplicationInfo info) {
        runningApplicationInfos.add(info);
    }

    private void onInnerMemoryScanEnd(long size) {
        Collections.sort(runningApplicationInfos, new MyCompator());
        ELVParentItemInfo data = new ELVParentItemInfo();
        data.setItemName(getString(R.string.system_quick_item_inner_memory_rubbish));
        data.setIsChecked(true);
        data.setChilds(runningApplicationInfos);
        datas.add(data);
        adapter.setInfos(datas);
        adapter.notifyDataSetChanged();//显示数据
        itv_clean.setVisibility(View.VISIBLE);
        itv_clean.startAnimation(animationAppear);
    }

    //------------------------------app内存垃圾清理----------------------------------
    private void onInnerMemoryCleanStart(int sumTask) {
        itemTask = sumTask;
        mpb_sweep.setTitle(getResources().getString(R.string.system_quick_inner_memory_cleaning));
        mpb_sweep.setPercentimmediately(0);
        tv_sweepContent.setText("0/" + itemTask);
    }

    private void onInnerMemoryCleanInDoing() {
        makeToast("清除内存垃圾任务已经开始了");
    }

    private void onInnerMemoryCleanProgressUpdated(int index, String progressName) {
        tv_sweepContent.setText(progressName + "(" + index + "/" + itemTask + ")");
        mpb_sweep.setPercentSlow(index * 1f / itemTask);
    }

    private void onInnerMemoryCleanProgressEnd(String progressName, boolean result) {
    }

    private void onInnerMemoryCleanEnd() {
        makeToast(getString(R.string.system_quick_clean_over));
        datas.clear();
        adapter.setInfos(datas);
        adapter.notifyDataSetChanged();
        //再次扫描
        startService(AppRubbishManageService.class,getString(R.string.Action_Service_AppExternalRubbishScannStart));
    }

    ;

    //---------------------------------------------------------------------------------

    @Override
    public void OnItemChecked(int... parms) {
        int items = 0;
        for (int i : parms) {
            items += i;
        }
        itv_clean.setEnabled(items > 0 ? true : false);
    }


    private boolean isHide = false;

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    private int lastfirstVisibleItem = 0;

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem - lastfirstVisibleItem > 0) {
            //向上滑动
            if (!isHide) {
                itv_clean.startAnimation(animationDisappear);
                itv_clean.setEnabled(false);
                itv_clean.setFocusable(false);

                isHide = true;
            }
        } else {
            if (firstVisibleItem - lastfirstVisibleItem < 0) {
                //向下滑动
                if (isHide) {
                    itv_clean.startAnimation(animationAppear);
                    itv_clean.setEnabled(true);
                    itv_clean.setFocusable(true);
                    isHide = false;
                }
            }
        }
        lastfirstVisibleItem = firstVisibleItem;

    }

    class MyCompator implements Comparator<ChildItemData> {
        @Override
        public int compare(ChildItemData a, ChildItemData b) {
            if (a.getCacheSize() < b.getCacheSize()) {
                return 1;
            }
            if (a.getCacheSize() == b.getCacheSize()) {
                return 0;
            }
            return -1;
        }
    }

    class AppRubbishManageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (getString(R.string.Action_Broadcast_AppExternalRubbishScannBegin).equals(action)) {
                //扫描开始
                onCacheScanStart(intent.getExtras().getInt("TotalTask"));

            } else if (getString(R.string.Action_Broadcast_AppExternalRubbishScannInDoing).equals(action)) {
                //正在扫描中又一次开始扫描，直接返回正在扫描的状态
                onCacheScanInDoing();
            } else if (getString(R.string.Action_Broadcast_AppExternalRubbishScannProgressUpdated).equals(action)) {
                //正在扫描新的任务
                onCacheScanProgressUpdated(intent.getExtras().getInt("TaskIndex"),
                        intent.getExtras().getInt("TaskAppUid"),
                        intent.getExtras().getString("TaskAppName"));

            } else if (getString(R.string.Action_Broadcast_AppExternalRubbishScannProgressEnd).equals(action)) {
                //单个app缓存垃圾扫描结果
                onCacheScanProgressEnd((CacheInfo) intent.getExtras().getParcelable("TaskProgressEnd"));
            } else if (getString(R.string.Action_Broadcast_AppExternalRubbishScannEnd).equals(action)) {
                //扫描结束，返回扫描到的缓存垃圾大小
                onCacheScannEnd(intent.getExtras().getLong("TaskEnd"));
            } else if (getString(R.string.Action_Broadcast_AppExternalRubbishCleanBegin).equals(action)) {
                onCacheCleanBegin();
            } else if (getString(R.string.Action_Broadcast_AppExternalRubbishCleanInDoing).equals(action)) {
                onCacheCleanInDoing();
            } else if (getString(R.string.Action_Broadcast_AppExternalRubbishCleanAwait).equals(action)) {
                onCacheCleanAwait();
            } else if (getString(R.string.Action_Broadcast_AppExternalRubbishCleanEnd).equals(action)) {
                onCacheCleanEnd(intent.getExtras().getBoolean("CleanResult"));
            }
        }
    }

    class InnerMemoryManageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (getString(R.string.Action_Broadcast_InnerMemoryRubbishScannBegin).equals(action)) {
                onInnerMemoryScanBegin(intent.getExtras().getInt("TotalTask"));//扫描开始
            } else if (getString(R.string.Action_Broadcast_InnerMemoryRubbishScannInDoing).equals(action)) {
                onInnerMemoryScanInDoing();//扫描正在运行时再次启动扫描回调
            } else if (getString(R.string.Action_Broadcast_InnerMemoryRubbishScannProgressUpdated).equals(action)) {
                //正在扫描新的任务
                onInnerMemoryScanProgressUpdated(intent.getExtras().getInt("TaskIndex"),
                        intent.getExtras().getInt("TaskAppUid"),
                        intent.getExtras().getString("TaskAppName"));
            } else if (getString(R.string.Action_Broadcast_InnerMemoryRubbishScannProgressEnd).equals(action)) {
                onInnerMemoryScanProgressEnd((RunningApplicationInfo) intent.getExtras().getParcelable("TaskProgressEnd"));
            } else if (getString(R.string.Action_Broadcast_InnerMemoryRubbishScannEnd).equals(action)) {
                onInnerMemoryScanEnd(intent.getExtras().getLong("TaskEnd"));
            } else if (getString(R.string.Action_Broadcast_InnerMemoryRubbishCleanBegin).equals(action)) {
                onInnerMemoryCleanStart(intent.getExtras().getInt("TotalTask"));//开始清理app内存垃圾
            } else if (getString(R.string.Action_Broadcast_InnerMemoryRubbishCleanInDoing).equals(action)) {
                onInnerMemoryCleanInDoing();
            } else if (getString(R.string.Action_Broadcast_InnerMemoryRubbishCleanProgressUpdated).equals(action)) {
                onInnerMemoryCleanProgressUpdated(intent.getExtras().getInt("TaskIndex"),
                        intent.getExtras().getString("TaskAppName"));
            } else if (getString(R.string.Action_Broadcast_InnerMemoryRubbishCleanProgressEnd).equals(action)) {
                onInnerMemoryCleanProgressEnd(intent.getExtras().getString("TaskAppName"),
                        intent.getExtras().getBoolean("TaskAppResult"));
            } else if (getString(R.string.Action_Broadcast_InnerMemoryRubbishCleanEnd).equals(action)) {
                onInnerMemoryCleanEnd();
            }
        }
    }
}
