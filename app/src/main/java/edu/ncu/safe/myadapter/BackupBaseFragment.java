package edu.ncu.safe.myadapter;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import java.util.List;

import edu.ncu.safe.R;
import edu.ncu.safe.View.MyProgressBar;
import edu.ncu.safe.adapter.BackupLVAdapter;
import edu.ncu.safe.domain.BackupInfo;

/**
 * Created by Mr_Yang on 2016/6/1.
 */
public abstract class BackupBaseFragment extends Fragment implements AdapterView.OnItemClickListener, BackupLVAdapter.OnAdapterEventListener {
    public static final int SHOWTYPE_LOCAL = 0;
    public static final int SHOWTYPE_CLOUD = 1;
    public static final int SHOWTYPE_BACKUP = 2;
    public static final int SHOWTYPE_RECOVERY = 3;

    protected ListView lv;
    protected MyProgressBar mpb_load;
    protected LinearLayout ll_empty;
    protected BackupLVAdapter adapter;

    protected List<BackupInfo> cloudInfos = null;
    protected List<BackupInfo> localInfos = null;

    protected PopupWindow popupWindow;
    protected int currentType = SHOWTYPE_LOCAL;

    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_backup, null);
        lv = (ListView) view.findViewById(R.id.lv);
        mpb_load = (MyProgressBar) view.findViewById(R.id.mpb_load);
        ll_empty = (LinearLayout) view.findViewById(R.id.ll_empty);

        adapter = new BackupLVAdapter(getContext());
        lv.setAdapter(adapter);

        adapter.setOnAdapterEventListener(this);
        lv.setOnItemClickListener(this);
        init();
        return view;
    }

    public void showLocal() {
        currentType = SHOWTYPE_LOCAL;
        if (localInfos != null) {
            adapter.setInfos(localInfos);
            adapter.notifyDataSetChanged();
            return;
        }
        showLoader(true);
        loadLocalInfos();
    }

    public void showCloud() {
        currentType = SHOWTYPE_CLOUD;
        if (cloudInfos != null) {
            adapter.setInfos(cloudInfos);
            adapter.notifyDataSetChanged();
            return;
        }
        showLoader(true);
        loadCloudInfos(0, 30);
    }

    public void showBackup() {
    }

    public void showRecovery() {
    }

    ;

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
        lv.setEmptyView(b ? mpb_load : ll_empty);
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

    public int getCurrentType() {
        return currentType;
    }


    public abstract void init();

    public abstract List<BackupInfo> loadCloudInfos(int beginIndex, int size);

    public abstract List<BackupInfo> loadLocalInfos();
}
