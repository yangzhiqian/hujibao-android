package edu.ncu.safe.ui;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.ncu.safe.R;
import edu.ncu.safe.View.MyProgressBar;
import edu.ncu.safe.adapter.SystemQuickenELVAdapter;
import edu.ncu.safe.domain.CacheInfo;
import edu.ncu.safe.domain.ELVParentItemInfo;
import edu.ncu.safe.domain.RunningApplicationInfo;
import edu.ncu.safe.myinterface.ChildItemData;
import edu.ncu.safe.service.AppRubbishCleanService;
import edu.ncu.safe.service.InnerMemoryCleanService;

/**
 * Created by Mr_Yang on 2016/5/26.
 */
public class SystemQuickenActivity extends Activity implements View.OnClickListener, InnerMemoryCleanService.OnMemoryDataGetListener, AppRubbishCleanService.OnRubbishDataChangedListener, SystemQuickenELVAdapter.OnItemCheckedListener, AbsListView.OnScrollListener {
    private ImageView iv_back;
    private MyProgressBar mpb_innerMemory;
    private MyProgressBar mpb_sweep;
    private MyProgressBar mpb_outerMemory;
    private TextView tv_sweepContent;
    private ExpandableListView elv_sweepResult;
    private LinearLayout ll_clean;
    private LinearLayout ll_emptyView;
    private ImageView iv_clean;

    private List<ELVParentItemInfo> datas;
    private SystemQuickenELVAdapter adapter;

    private InnerMemoryCleanService innerMemoryCleanService;
    private AppRubbishCleanService appRubbishCleanService;

    private int totalItem = 2;
    private int itemTask = 1;

    private Animation animationAppear;
    private Animation animationDisappear;

    private ServiceConnection memoryServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            innerMemoryCleanService = ((InnerMemoryCleanService.MyBinder) service).getService();
            innerMemoryCleanService.setOnMemoryDataGetListener(SystemQuickenActivity.this);
            innerMemoryCleanService.runScanner();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };
    private ServiceConnection rubbishServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            appRubbishCleanService = ((AppRubbishCleanService.MyBinder) service).getService();
            appRubbishCleanService.setOnRubbishDataChangedListener(SystemQuickenActivity.this);


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_systemquicken);
        iv_back = (ImageView) this.findViewById(R.id.back);
        mpb_innerMemory = (MyProgressBar) this.findViewById(R.id.mpb_innermemory);
        mpb_sweep = (MyProgressBar) this.findViewById(R.id.mpb_sweep);
        mpb_outerMemory = (MyProgressBar) this.findViewById(R.id.mpb_outermemory);
        tv_sweepContent = (TextView) this.findViewById(R.id.tv_sweepContent);
        elv_sweepResult = (ExpandableListView) this.findViewById(R.id.elv_sweepResult);
        ll_clean = (LinearLayout) this.findViewById(R.id.ll_clean);

        ll_emptyView = (LinearLayout) this.findViewById(R.id.ll_emptyview);
        animationAppear = AnimationUtils.loadAnimation(this, R.anim.cleanbuttonappear);
        animationDisappear = AnimationUtils.loadAnimation(this, R.anim.cleanbuttondisappear);

        datas = new ArrayList<ELVParentItemInfo>();
        adapter = new SystemQuickenELVAdapter(getApplicationContext(), datas);
        elv_sweepResult.setAdapter(adapter);
        elv_sweepResult.setEmptyView(ll_emptyView);

        iv_back.setOnClickListener(this);
        ll_clean.setOnClickListener(this);
        adapter.setOnItemCheckedListener(this);

        elv_sweepResult.setOnScrollListener(this);
        bindService(new Intent(this, InnerMemoryCleanService.class), memoryServiceConnection, Context.BIND_AUTO_CREATE);
        bindService(new Intent(this, AppRubbishCleanService.class), rubbishServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMemoryInfo();
    }

    private void updateMemoryInfo() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        manager.getMemoryInfo(memoryInfo);
        long totalMem = memoryInfo.totalMem;
        long availMem = memoryInfo.availMem;
        mpb_innerMemory.setPercentSlow((totalMem - availMem) * 100 / totalMem);
        mpb_innerMemory.setTitle("总内存:" + String.format("%.2f", totalMem / (1024 * 1024 * 1024.0)) + "G");


        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        long availableBlocks = stat.getAvailableBlocks();
        String totalSize = Formatter.formatFileSize(getApplicationContext(), totalBlocks * blockSize);
        mpb_outerMemory.setPercentSlow((totalBlocks - availableBlocks) * 100 / totalBlocks);
        mpb_outerMemory.setTitle("内部存储:" + totalSize);
    }

    private void updateELVData() {
        adapter.setInfos(datas);
        adapter.notifyDataSetChanged();
        ll_clean.setVisibility(View.VISIBLE);
        ll_clean.startAnimation(animationAppear);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                overridePendingTransition(R.anim.activit3dtoright_in, R.anim.activit3dtoright_out);
                break;
            case R.id.ll_clean:
                innerMemoryCleanService.runClean(adapter.getCheckAppProcessNames());
                ll_clean.startAnimation(animationDisappear);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(memoryServiceConnection);
        unbindService(rubbishServiceConnection);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            overridePendingTransition(R.anim.activit3dtoright_in, R.anim.activit3dtoright_out);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onMemoryScanTaskStart(int sumTask) {
        System.out.println("onMemoryScanTaskStart:" + sumTask);
        mpb_sweep.setTitle("正在扫描内存垃圾");
        tv_sweepContent.setText("0/" + itemTask);
        itemTask = sumTask;
    }

    @Override
    public void onMemoryScanProgressChanged(String tastName, int progress) {
        System.out.println("onMemoryScanProgressChanged:" + tastName + "/t" + progress);
        tv_sweepContent.setText(tastName + "(" + progress + "/" + itemTask + ")");
        mpb_sweep.setPercentSlow(progress * 50.0f / itemTask);
    }

    @Override
    public void onMemoryScanTaskEnded(List<RunningApplicationInfo> infos) {
        System.out.println("onMemoryScanTaskEnded    size :" + infos.size());
        Collections.sort(infos, new MyCompator());
        ELVParentItemInfo data = new ELVParentItemInfo();
        data.setItemName("内存垃圾");
        data.setIsChecked(true);
        data.setChilds(infos);
        datas.add(data);
        appRubbishCleanService.scanRubbish();
    }

    @Override
    public void onMemoryCleanTaskStart(int sumTask) {
        ll_emptyView.setVisibility(View.VISIBLE);
        mpb_sweep.setPercentimmediately(0);
        mpb_sweep.setTitle("正在清理内存垃圾");
        itemTask = sumTask;
        tv_sweepContent.setText("0/"+sumTask);
    }

    @Override
    public void onMemoryCleanProgressChanged(String tastName, int progress) {
        mpb_sweep.setPercentSlow(progress*50.0f/itemTask);
        tv_sweepContent.setText(tastName+"("+progress+"/" + itemTask+")");
    }

    @Override
    public void onMemoryCleanTaskEnded(int res) {
        appRubbishCleanService.cleanRubbish();
        mpb_sweep.setTitle("正在清理缓存垃圾");
    }

    @Override
    public void onRubbishTaskScanStart(int sumTask) {
        System.out.println("onRubbishTaskScanStart:" + sumTask);
        mpb_sweep.setTitle("正在扫描缓存垃圾");
        mpb_sweep.setPercentSlow(0);
        tv_sweepContent.setText("0/" + itemTask);
        itemTask = sumTask;
    }

    @Override
    public void onRubbishScanProgressChanged(String tastName, int progress) {
        System.out.println("onRubbishScanProgressChanged:" + tastName + "/t" + progress);
        tv_sweepContent.setText(tastName + "(" + progress + "/" + itemTask + ")");
        mpb_sweep.setPercentSlow(50+progress * 50.0f / itemTask);
    }

    @Override
    public void onRubbishScanTaskEnded(List<CacheInfo> infos) {
        System.out.println("onRubbishScanTaskEnded    size :" + infos.size());
        Collections.sort(infos, new MyCompator());
        ELVParentItemInfo data = new ELVParentItemInfo();
        data.setItemName("应用缓存垃圾");
        data.setIsChecked(true);
        data.setChilds(infos);
        datas.add(data);
        updateELVData();
    }

    @Override
    public void onRubbishTaskCleanStart(int sumTask) {

        itemTask = sumTask;
        tv_sweepContent.setText("0/"+sumTask);
    }

    @Override
    public void onRubbishCleanProgressChanged(String tastName) {

    }

    @Override
    public void onRubbishCleanTaskEnded(long size) {
        Toast.makeText(this,"共清理内存"+size/(1024*1024)+"MB",Toast.LENGTH_LONG).show();
        mpb_sweep.setPercentSlow(100);
        ll_emptyView.setVisibility(View.GONE);
        datas.clear();
        adapter.setInfos(datas);
        adapter.notifyDataSetChanged();
        innerMemoryCleanService.runScanner();
    }

    @Override
    public void onRubbishTaskScanRootStart(int sumTask) {

    }

    @Override
    public void onRubbishScanRootProgressChanged(String tastName, int progress) {

    }

    @Override
    public void onRubbishScanRootTaskEnded(List<CacheInfo> infos) {

    }

    @Override
    public void onRubbishTaskCleanRootStart(int sumTask) {

    }

    @Override
    public void onRubbishCleanRootProgressChanged(String tastName, long size, int progress) {

    }

    @Override
    public void onRubbishCleanRootTaskEnded(long size) {

    }

    @Override
    public void OnItemChecked(int... parms) {
        int items = 0;
        for (int i : parms) {
            items += i;
        }
        ll_clean.setEnabled(items > 0 ? true : false);
    }


    private boolean isHide = false;

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        System.out.println("onScrollStateChanged:" + scrollState);
    }

    private int lastfirstVisibleItem = 0;

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        System.out.println("onScroll----firstVisibleItem:" + firstVisibleItem);
        System.out.println("onScroll----visibleItemCount:" + visibleItemCount);
        System.out.println("onScroll----totalItemCount:" + totalItemCount);
        if (firstVisibleItem - lastfirstVisibleItem > 0) {
            //向上滑动
            if (!isHide) {
                ll_clean.startAnimation(animationDisappear);
                ll_clean.setEnabled(false);
                ll_clean.setFocusable(false);

                isHide = true;
            }
        } else {
            if (firstVisibleItem - lastfirstVisibleItem < 0) {
                //向下滑动
                if (isHide) {
                    ll_clean.startAnimation(animationAppear);
                    ll_clean.setEnabled(true);
                    ll_clean.setFocusable(true);
                    isHide = false;
                }
            }
        }
        lastfirstVisibleItem = firstVisibleItem;

    }

    class MyCompator implements Comparator<ChildItemData> {
        @Override
        public int compare(ChildItemData a, ChildItemData b) {
            if (a.getItemSize() < b.getItemSize()) {
                return 1;
            }
            if (a.getItemSize() == b.getItemSize()) {
                return 0;
            }
            return -1;
        }
    }
}
