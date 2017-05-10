package edu.ncu.safe.base.fragment;


import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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
import edu.ncu.safe.adapter.BackupLVAdapter;
import edu.ncu.safe.customerview.MyProgressBar;
import edu.ncu.safe.domainadapter.ITarget;
import edu.ncu.safe.engine.NetDataOperator.BACKUP_TYPE;
import edu.ncu.safe.ui.fragment.ContactsBackupFragment;
import edu.ncu.safe.ui.fragment.MessageBackupFragment;
import edu.ncu.safe.ui.fragment.PictureBackupFragment;
import edu.ncu.yang.pulltorefreshandload.PullToRefreshLayout;

/**
 * Created by Mr_Yang on 2016/6/1
 */
public abstract class BackupBaseFragment extends Fragment implements AdapterView.OnItemClickListener, BackupLVAdapter.OnAdapterEventListener {
    /**
     * 每次要加载的数据量
     */
    private static final int SHOW_NUMBERS = 30;

    //type
    protected BACKUP_TYPE type;
    protected SHOW_TYPE showType = SHOW_TYPE.SHOW_TYPE_LOCAL;

    //views
    protected PullToRefreshLayout ptr;
    protected ListView lv;
    protected MyProgressBar mpb_load;
    protected LinearLayout ll_empty;

    //adapter
    protected BackupLVAdapter adapter;
    //popup
    private PopupWindow popupWindow;

    //data
    private List<ITarget> cloudInfos;
    private List<ITarget> localInfos;
    private boolean isAllCloudDataLoaded = false;


    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_backup, null);
        ptr = (PullToRefreshLayout) view.findViewById(R.id.refresh_view);
        lv = (ListView) view.findViewById(R.id.lv);
        mpb_load = (MyProgressBar) view.findViewById(R.id.mpb_load);
        ll_empty = (LinearLayout) view.findViewById(R.id.ll_empty);

        ptr.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                switch (showType) {
                    case SHOW_TYPE_LOCAL:
                        loadLocalInfosAsync();//刷新
                        break;
                    default:
                        cloudInfos = new ArrayList<>();
                        isAllCloudDataLoaded = false;
                        //刷新加载
                        loadCloudInfos(0, SHOW_NUMBERS);
                        break;
                }
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                switch (showType) {
                    case SHOW_TYPE_LOCAL:
                        makeToast("已经没有数据了");
                        break;
                    default:
                        if (isAllCloudDataLoaded) {
                            makeToast("已经没有数据了");
                            ptr.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                            return;
                        }
                        //加载
                        loadCloudInfos(cloudInfos.size(), cloudInfos.size() + SHOW_NUMBERS);
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
    public void showLocal() {
        showType = SHOW_TYPE.SHOW_TYPE_LOCAL;
        adapter.setInfos(localInfos);
        adapter.notifyDataSetChanged();
        //第一次显示，要加载
        if (localInfos == null) {
            showLoader(true);
            loadLocalInfosAsync();
        }
    }

    /**
     * 当用户点击了网络调用显示网络内容
     */
    public void showCloud() {
        showType = SHOW_TYPE.SHOW_TYPE_CLOUD;
        adapter.setInfos(cloudInfos);
        adapter.notifyDataSetChanged();
        if (cloudInfos == null) {
            //当前还未加载过数据
            showLoader(true);
            loadCloudInfos(0, SHOW_NUMBERS);
        }
    }

    /**
     * 显示网络上有但本地没有的数据
     */
    public void showRecovery() {
        showType = SHOW_TYPE.SHOW_TYPE_RECOVERY;
        List<ITarget> infos = loadRecoveryInfos();
        adapter.setInfos(infos);
        adapter.notifyDataSetChanged();
        //没有加载到数据，并且显示云端数据状态为还未全部加载，继续加载
        if (infos.size() == 0 && !isAllCloudDataLoaded) {
            showLoader(true);
            //继续向后加载SHOW_NUMBERS 云端数据
            int begin = this.cloudInfos == null ? 0 : this.cloudInfos.size();
            loadCloudInfos(begin, begin + SHOW_NUMBERS);
        }
    }

    public void showMulitChoice(boolean show) {
        adapter.setIsShowMultiChoice(show);
        adapter.notifyDataSetChanged();
    }

    public boolean isShowingMulitChoice() {
        return adapter.isShowMultiChoice();
    }

    /**
     * 内部调用，用于刷新当前页面的数据
     */
    private void updateView() {
        switch (showType) {
            case SHOW_TYPE_LOCAL:
                showLocal();
                break;
            case SHOW_TYPE_CLOUD:
                showCloud();
                break;
            case SHOW_TYPE_RECOVERY:
                showRecovery();
                break;
        }
    }


    /**
     * 当本地的数据加载完毕时调用
     *
     * @param infos 子类加载到的数据，该数据为全部数据
     */
    protected void onLocalInfosLoaded(List<ITarget> infos) {
        this.localInfos = infos;
        if (showType == SHOW_TYPE.SHOW_TYPE_LOCAL) {
            ptr.refreshFinish(PullToRefreshLayout.SUCCEED);
            showLoader(false);
            updateView();
        }
    }

    /**
     * 当网络的数据加载到新数据时调用
     *
     * @param infos 子类加载到的数据，该数据为刚刷新到的新数据
     */
    protected void onCloudInfosLoaded(List<ITarget> infos, boolean isOver) {
        //排除刚才是还未初始化的情况
        if (this.cloudInfos == null || this.cloudInfos.size() == 0) {
            //首次进入加载数据或者刷新数据
            this.cloudInfos = infos;
            //影藏刷新栏
            if (showType == SHOW_TYPE.SHOW_TYPE_CLOUD || showType == SHOW_TYPE.SHOW_TYPE_RECOVERY) {
                ptr.refreshFinish(PullToRefreshLayout.SUCCEED);
                //隐藏加载动画
                showLoader(false);
                isAllCloudDataLoaded = isOver;
                makeToast("刷新了" + infos.size() + "条云端数据");
                updateView();
            }
        } else {
            //加载更多
            this.cloudInfos.addAll(infos);
            //隐藏加载栏
            if (showType == SHOW_TYPE.SHOW_TYPE_CLOUD || showType == SHOW_TYPE.SHOW_TYPE_RECOVERY) {
                ptr.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                isAllCloudDataLoaded = isOver;
                makeToast("加载到了" + infos.size() + "条云端数据");
                updateView();
            }
        }
    }

    /**
     * 对比本地的数据和云端的数据，如果云端有但本地没有，则加入到未还原中
     *
     * @return 本地没有的但云端有的数据
     */
    private List<ITarget> loadRecoveryInfos() {
        List<ITarget> infos = new ArrayList<>();
        if(cloudInfos==null){
            //没有云端数据，直接返回空集合
            return  infos;
        }
        for (ITarget cloudInfo : cloudInfos) {
            for (int i = 0; i < localInfos.size(); i++) {
                if (isSameInfo(cloudInfo, localInfos.get(i))) {
                    //相同，代表本地有，比较下一个云端的数据
                    break;
                }
                if (i == localInfos.size() - 1) {
                    //云端的数据和本地的全部数据比较，都不相同，添加进未还原
                    infos.add(cloudInfo);
                }
            }
        }
        return infos;
    }

    /**
     * 创建并显示合适的popupwindow，该方法适配所有的备份数据和显示类型
     *
     * @param parent   显示数据listview中的item
     * @param view     item中触发该popupwindow的view，用于定位popup的显示位置
     * @param position item的position
     * @param info     item对应的数据
     * @return popupwindow
     */
    private PopupWindow createAdaptedPopupWindowAndShow(final View parent, final View view, final int position, ITarget info) {
        TextView tv = new TextView(getActivity());
        tv.setText("无操作");
        View contentView = tv;
        //子类负责创建内容体
        switch (showType) {
            case SHOW_TYPE_LOCAL:
                contentView = createShowLocalPopupWindowContentView(parent, position, info);
                break;
            case SHOW_TYPE_CLOUD:
                contentView = createShowCloudPopupWindowContentView(parent, position, info);
                break;
            case SHOW_TYPE_RECOVERY:
                contentView = createShowRecoveryPopupWindowContentView(parent, position, info);
                break;
        }
        popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //popupWindow设置animation一定要在show之前
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        //一定要设置背景，否则无法自动消失
        Drawable background = getResources().getDrawable(R.drawable.popupbgright);
        view.measure(0, 0);
        contentView.measure(0, 0);
        popupWindow.setBackgroundDrawable(background);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ((ImageView) view).setImageResource(R.drawable.close);
            }
        });
        ((ImageView) view).setImageResource(R.drawable.expand);
        popupWindow.showAsDropDown(view,
                20 + (-1 * contentView.getMeasuredWidth()),
                -1 * (contentView.getMeasuredHeight() + view.getHeight()) / 2);
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

    /**
     * 是否显示加载进度条
     *
     * @param b true表示需要显示
     */
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

    protected void dissmissPopupWindow() {
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    protected void onProgressStateChanged(int position, boolean isShow) {
        adapter.onProgressStateChanged(position, isShow);
    }

    protected void onProgressChanged(int position, int percent) {
        adapter.onProgressChanged(position, percent);
    }


    /**
     * 初始化操作
     */
    public abstract void init();

    /**
     * 异步加载本地数据操作
     */
    protected void loadLocalInfosAsync() {
        //使用异步任务加载本地数据，防止因在主线程操作耗时操作
        new AsyncTask<Void, Void, List<ITarget>>() {
            @Override
            protected List<ITarget> doInBackground(Void... params) {
                return loadLocalInfos();
            }

            @Override
            protected void onPostExecute(List<ITarget> infos) {
                super.onPostExecute(infos);
                onLocalInfosLoaded(infos);
            }
        }.execute();
    }

    protected abstract List<ITarget> loadLocalInfos();

    /**
     * 异步加载云端数据操作
     *
     * @param beginIndex 起始index，同数据库操作里的index
     * @param endIndex   终止index，不包括
     */
    protected abstract void loadCloudInfos(int beginIndex, int endIndex);

    /**
     * 比较两个数据是否是同一个数据<br/>
     * 该方法主要比较的本地数据和云端数据是否相同，因数据的种类不同，比较方法也不相同，所以子类实现
     *
     * @param target1 数据1
     * @param target2 数据2
     * @return true表示相同
     */
    protected abstract boolean isSameInfo(ITarget target1, ITarget target2);

    protected abstract View createShowLocalPopupWindowContentView(final View parent, final int position, final ITarget info);

    protected abstract View createShowCloudPopupWindowContentView(final View parent, final int position, final ITarget info);

    protected abstract View createShowRecoveryPopupWindowContentView(final View parent, final int position, final ITarget info);

    @Override
    public void onShowPopupClicked(View parent, View view, int position, ITarget info) {
        //创建popup并且显示
        createAdaptedPopupWindowAndShow(parent, view, position, info);
    }

    @Override
    public void onDownloadProgressBarClicked(View parent, int position, ITarget info) {
    }

    @Override
    public void onCheckBoxCheckedChanged(View parent, int position, ITarget data, boolean isChecked) {
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //listview中的item被点击，如果是多选状态下，则选中该
        if (adapter.isShowMultiChoice()) {
            BackupLVAdapter.ViewHolder holder = (BackupLVAdapter.ViewHolder) view.getTag();
            holder.cb_check.setChecked(!holder.cb_check.isChecked());
        }
    }

    protected void makeToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 构建备份fragment的工厂
     */
    public static class BackUpFragmentFactory {
        public static BackupBaseFragment createFragment(BACKUP_TYPE type) {
            switch (type) {
                case TYPE_PICURE:
                    return PictureBackupFragment.newInstance(BACKUP_TYPE.TYPE_PICURE);
                case TYPE_SMS:
                    return MessageBackupFragment.newInstance(BACKUP_TYPE.TYPE_SMS);
                case TYPE_CONTACT:
                    return ContactsBackupFragment.newInstance(BACKUP_TYPE.TYPE_CONTACT);
                default:
                    throw new IllegalArgumentException("undefinded backup type");
            }
        }
    }

    public String getTitle() {
        return type.getTitle() + "(" + showType.getDescription() + ")";
    }

    public enum SHOW_TYPE {
        SHOW_TYPE_LOCAL("本地"),
        SHOW_TYPE_CLOUD("云端"),
        SHOW_TYPE_RECOVERY("未还原");
        private String description;

        SHOW_TYPE(String des) {
            this.description = des;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
