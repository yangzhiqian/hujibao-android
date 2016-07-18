package edu.ncu.safe.myadapter;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import edu.ncu.safe.MyApplication;
import edu.ncu.safe.R;
import edu.ncu.safe.View.MyProgressBar;
import edu.ncu.safe.adapter.BackupLVAdapter;
import edu.ncu.safe.domain.User;
import edu.ncu.safe.domainadapter.ContactsAdapter;
import edu.ncu.safe.domainadapter.ITarget;
import edu.ncu.safe.domainadapter.MessageAdapter;
import edu.ncu.safe.engine.DataLoader;
import edu.ncu.safe.engine.DataStorer;
import edu.ncu.safe.util.ContactUtil;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Created by Mr_Yang on 2016/6/1.
 */
public abstract class BackupBaseFragment extends Fragment implements AdapterView.OnItemClickListener, BackupLVAdapter.OnAdapterEventListener, AbsListView.OnScrollListener {
    public static final int TYPE_IMG = 0;
    public static final int TYPE_MESSAGE = 1;
    public static final int TYPE_CONTACT = 2;
    public static final int SHOWTYPE_LOCAL = 0;
    public static final int SHOWTYPE_CLOUD = 1;
    public static final int SHOWTYPE_BACKUP = 2;
    public static final int SHOWTYPE_RECOVERY = 3;
    public static final int SHOW_NUMBERS = 15;

    protected PtrFrameLayout ptr;
    protected ListView lv;
    protected MyProgressBar mpb_load;
    protected LinearLayout ll_empty;
    protected BackupLVAdapter adapter;

    protected List<ITarget> cloudInfos = null;
    protected List<ITarget> localInfos = null;

    protected PopupWindow popupWindow;
    protected int currentShowType = SHOWTYPE_LOCAL;
    protected int currentDataType;
    protected  boolean isLoading = false;
    protected  boolean isOver = false;

    public void setDataType( int type) {
        this.currentDataType = type;
    }

    @Nullable
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
                switch (getCurrentShowType()) {
                    case SHOWTYPE_LOCAL:
                        localInfos = null;
                        loadLocalInfos();
                        break;
                    default:
                        cloudInfos = null;
                        isOver = false;
                        loadCloudInfos(0, SHOW_NUMBERS);
                        break;
                }
            }
        });

        adapter = new BackupLVAdapter(getContext());
        lv.setAdapter(adapter);

        adapter.setOnAdapterEventListener(this);
        lv.setOnItemClickListener(this);
        lv.setOnScrollListener(this);
        init();
        return view;
    }

    /**
     * 当用户点击了本地调用显示本地内容
     */
    public void showLocal() {
        currentShowType = SHOWTYPE_LOCAL;
        adapter.setInfos(localInfos);
        adapter.notifyDataSetChanged();
        if(ptr.isRefreshing()){
            ptr.refreshComplete();
        }
        if (localInfos == null) {
            showLoader(true);
            loadLocalInfos();
        }
    }

    /**
     * 当用户点击了网络调用显示网络内容
     */
    public void showCloud() {
        currentShowType = SHOWTYPE_CLOUD;
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

    public void showBackup() {
       currentShowType = SHOWTYPE_BACKUP;
        List<ITarget> infos = getBackupInfos();
        adapter.setInfos(infos);
        adapter.notifyDataSetChanged();
        if(ptr.isRefreshing()){
            ptr.refreshComplete();
        }
        if (infos == null) {
            showLoader(true);
            loadCloudInfos(0, SHOW_NUMBERS*2);
        }
    }

    public void showRecovery() {
        currentShowType = SHOWTYPE_RECOVERY;
        List<ITarget> infos = getRecoveryInfos();
        adapter.setInfos(infos);
        adapter.notifyDataSetChanged();
        if(ptr.isRefreshing()){
            ptr.refreshComplete();
        }
        if (infos == null) {
            showLoader(true);
            loadCloudInfos(0, SHOW_NUMBERS*2);
        }
    }


    /**
     * 从网络上加载内容，正确返回的是json格式的内容
     *
     * @param beginIndex 偏移量
     * @param size       请求的数量
     * @return 永远返回null, 暂无用处
     */
    public List<ITarget> loadCloudInfos(final int beginIndex, final int size) {
        if(isLoading){
            if (ptr.isRefreshing()) {
                ptr.refreshComplete();
            }
            return null;
        }
        if(isOver){
            makeToast(getResources().getString(R.string.toast_error_no_more_data));
            return null;
        }
        makeToast(getResources().getString(R.string.toast_loading));
        isLoading=true;
        if(adapter.getInfos().size()==0){
            showLoader(true);
        }
        DataLoader loader = new DataLoader(getContext());
        loader.setOnDataObtainListener(new DataLoader.OnDataObtainedListener() {
            @Override
            public void onFailure(String error) {
                makeToast(error);
                showLoader(false);
                isLoading = false;
                if (ptr.isRefreshing()) {
                    ptr.refreshComplete();
                }
            }

            @Override
            public void onResponse(String response) {
                showLoader(false);
                isLoading = false;
                try {
                    List<ITarget> iTargets = parseToInfos(response);
                    if (iTargets.size() < size) {
                        isOver = true;
                    }
                    if (cloudInfos == null) {
                        cloudInfos = iTargets;
                        makeToast("刷新到了" + iTargets.size() + "条数据");
                    } else {
                        if (iTargets.size() > 0) {
                            cloudInfos.addAll(iTargets);
                            makeToast("加载了" + iTargets.size() + "条数据");
                        } else {
                            makeToast(getResources().getString(R.string.toast_error_no_more_data));
                            isOver = true;
                            return;
                        }
                    }
                    switch (getCurrentShowType()) {
                        case SHOWTYPE_CLOUD:
                            showCloud();
                            break;
                        case SHOWTYPE_BACKUP:
                            showBackup();
                            break;
                        case SHOWTYPE_RECOVERY:
                            showRecovery();
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (RuntimeException e) {
                }
            }
        });
        String url = getContext().getResources().getString(R.string.loadbackup);
        User user = User.getUserFromSP(getContext());
        if(user==null){
            makeToast(getResources().getString(R.string.toast_un_log_in));
            showLoader(false);
            isLoading = false;
            return null;
        }
        String[] valuesNames = {"token", "type", "offset", "number"};
        String[] values = {user.getToken(), currentDataType + "", beginIndex + "", size + ""};
        loader.loadServerJson(url, valuesNames, values);
        return null;
    }
    /**
     * 显示popupwindow
     *
     * @param view        父控件view,也就是listview的子条目
     * @param contentView 要显示的内容view
     */
    protected void showPopup(final View view, View contentView) {
        ((ImageView) view).setImageResource(R.drawable.expand);
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //popupWindow设置animation一定要在show之前
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        //一定要设置背景，否则无法自动消失
        Drawable background = getContext().getResources().getDrawable(R.drawable.popupbgright);
        popupWindow.setBackgroundDrawable(background);
        view.measure(0, 0);
        popupWindow.showAsDropDown(view,
                -1 * popupWindow.getContentView().getMeasuredWidth() - 30,
                -1 * (popupWindow.getContentView().getMeasuredHeight() + view.getHeight()) / 2 - 10);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ((ImageView) view).setImageResource(R.drawable.close);
            }
        });
    }


    @Override
    public void onShowPopupClicked(View parent, View view, final int position, ITarget info) {
        switch (getCurrentShowType()) {
            case SHOWTYPE_LOCAL:
            case SHOWTYPE_BACKUP:
                showPopup(view, getBackupView(parent, position, info));
                break;
            case SHOWTYPE_CLOUD:
            case SHOWTYPE_RECOVERY:
                showPopup(view, getRecorveryView(parent, position, info));
                break;
        }
    }


    /**
     * 设置短信和联系人的popupwindow的内容主体
     * 在设置的同时设置了点击监听事件
     *
     * @param parent
     * @param position listview的itemposition
     * @param info     item携带的数据
     * @return 返回构造好的contentview
     */
    protected View getBackupView(View parent, final int position, final ITarget info) {
        //构建所有控件的容器
        LinearLayout layout = getLayout();
        //构建控件
        TextView tv_bk = getTextView(getResources().getString(R.string.backup_pupup_item_backup));
        View divider1 = getDivider();
        TextView tv_msgback = getTextView(getResources().getString(R.string.backup_pupup_item_back_msg));
        View divider2 = getDivider();
        TextView tv_callback = getTextView(getResources().getString(R.string.backup_pupup_item_back_call));
        //装入控件
        layout.addView(tv_bk);
        layout.addView(divider1);
        layout.addView(tv_msgback);
        layout.addView(divider2);
        layout.addView(tv_callback);

        //设置点击事件
        tv_bk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataStorer storer = new DataStorer(getContext());
                storer.setOnDataUploadedListener(new DataStorer.OnDataUploadedListener() {
                    @Override
                    public void onFailure(String error) {
                        makeToast(error);
                    }

                    @Override
                    public void onResponse(String response) {
                        try {
                            String message = (String) new JSONObject(response).getJSONObject("message").getJSONArray("msg").get(0);
                            makeToast(message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            makeToast(getResources().getString(R.string.toast_error_unknow));
                        }
                    }
                });
                popupWindow.dismiss();
                User user = User.getUserFromSP(getContext());
                if (user == null) {
                    makeToast(getResources().getString(R.string.toast_un_log_in));
                    return;
                }
                storer.storeData(user.getToken(), info);
            }
        });
        tv_msgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (info instanceof MessageAdapter) {
                    ContactUtil.sendMessageTo(getContext(), ((MessageAdapter) info).getAddress());
                } else {
                    if (info instanceof ContactsAdapter) {
                        ContactUtil.sendMessageTo(getContext(), ((ContactsAdapter) info).getPhoneNumber());
                    }
                }
                popupWindow.dismiss();
            }
        });
        tv_callback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (info instanceof MessageAdapter) {
                    ContactUtil.callTo(getContext(), ((MessageAdapter) info).getAddress());
                } else {
                    if (info instanceof ContactsAdapter) {
                        ContactUtil.callTo(getContext(), ((ContactsAdapter) info).getPhoneNumber());
                    }
                }
                popupWindow.dismiss();
            }
        });
        return layout;
    }

    protected View getRecorveryView(final View parent, final int position, final ITarget info) {
        //构建所有控件的容器
        LinearLayout layout = getLayout();
        //构建控件
        TextView tv_bk = getTextView(getResources().getString(R.string.backup_pupup_item_recovery));
        View divider = getDivider();
        TextView tv_del = getTextView(getResources().getString(R.string.backup_pupup_item_delete));
        //添加监听
        tv_bk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToPhone(parent, position, info);
                popupWindow.dismiss();
            }
        });
        tv_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                DataLoader loader = new DataLoader(getContext());
                loader.setOnDataObtainListener(new DataLoader.OnDataObtainedListener() {
                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            boolean succeed = object.getBoolean("succeed");
                            String message = object.getString("message");
                            makeToast(message);
                            if (succeed) {
                                if(currentDataType==TYPE_IMG){
                                    User user = User.getUserFromSP(getContext());
                                    File file = new File(info.getIconPath());
                                    user.setUsed( user.getUsed() -  file.length());
                                    SharedPreferences.Editor edit = MyApplication.getSharedPreferences().edit();
                                    edit.putString(MyApplication.SP_STRING_USER,user.toJson());
                                    edit.apply();
                                }
                                List<ITarget> infos = adapter.getInfos();
                                infos.remove(info);
                                adapter.setInfos(infos);
                                adapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            makeToast(getResources().getString(R.string.toast_error_unknow));
                        }
                    }
                });
                User user = User.getUserFromSP(getContext());
                if(user==null){
                    makeToast(getResources().getString(R.string.toast_un_log_in));
                    return;
                }
                loader.deleteBackup(user.getToken(), currentDataType, info.getID());
            }
        });
        //装入控件
        layout.addView(tv_bk);
        layout.addView(divider);
        layout.addView(tv_del);
        return layout;
    }

    protected LinearLayout getLayout() {
        LinearLayout layout = new LinearLayout(getContext());
        ViewGroup.LayoutParams parms = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(parms);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER_VERTICAL);
        layout.setPadding(5, 5, 5, 5);
        return layout;
    }

    protected TextView getTextView(String text) {
        TextView tv = new TextView(getContext());
        ViewGroup.LayoutParams parms = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(parms);
        tv.setText(text);
        tv.setTextColor(Color.parseColor("#FFFFFF"));
        tv.setTextSize(20);
        return tv;
    }

    protected View getDivider() {
        View view = new View(getContext());
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(1, ViewGroup.LayoutParams.MATCH_PARENT);
        parms.setMargins(10, 0, 10, 0);
        view.setLayoutParams(parms);
        view.setBackgroundColor(Color.parseColor("#aaaaaa"));
        return view;
    }


    public void showMultiChoice(boolean choice) {
        selectNone();
        adapter.setIsShowMultiChoice(choice);
        adapter.notifyDataSetChanged();
    }

    protected void showLoader(boolean b) {
        if (b) {
            lv.setEmptyView(mpb_load);
            ll_empty.setVisibility(View.GONE);
        } else {
            lv.setEmptyView(ll_empty);
            mpb_load.setVisibility(View.GONE);
        }
    }

    public boolean isShowMultiChoice() {
        return adapter.isShowMultiChoice();
    }

    public void selectAll() {
        adapter.setSelectedAll(true);
    }

    public void selectNone() {
        adapter.setSelectedAll(false);
    }

    public int getCurrentShowType() {
        return currentShowType;
    }

    public abstract void init();

    public abstract List<ITarget> loadLocalInfos();

    public abstract List<ITarget> parseToInfos(String json) throws JSONException;

    public abstract void backToPhone(View parent, int position, final ITarget info);

    public abstract List<ITarget> getBackupInfos();
    public abstract List<ITarget> getRecoveryInfos();
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    long time = 0;
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        long now = System.currentTimeMillis();
        if(now-time<3000){
            return;
        }
        time = now;
        if(getCurrentShowType()!=SHOWTYPE_LOCAL&&firstVisibleItem+visibleItemCount>=totalItemCount-1){
            int offset = cloudInfos==null?0:cloudInfos.size();
            loadCloudInfos(offset,offset+SHOW_NUMBERS);
        }
    }
    protected void makeToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
