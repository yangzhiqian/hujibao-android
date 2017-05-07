package edu.ncu.safe.ui;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import edu.ncu.safe.R;
import edu.ncu.safe.adapter.PhoneLostProtectorRecyclerViewAdapter;
import edu.ncu.safe.base.activity.BackAppCompatActivity;
import edu.ncu.safe.domain.PhoneLostProtectorSetsItem;
import edu.ncu.safe.mvp.presenter.PhoneLostProtectorPresenter;
import edu.ncu.safe.mvp.view.PhoneLostProtectorMvpView;

public class PhoneLostProtectActivity extends BackAppCompatActivity implements
        OnClickListener, PhoneLostProtectorRecyclerViewAdapter.OnItemCheckBoxClickedListener, PhoneLostProtectorMvpView {
    public static final String[] ORDERS = {"#*delete*#", "#*lock*#", "#*ring*#", "#*pwd*#", "#*location*#"};

    //ui
    private View swapLine;
    private ImageView iv_protect;
    private TextView tv_protectState;
    private RecyclerView rv_sets;

    // 抽屉布局里的控件
    private ImageView iv_handle;
    private LinearLayout ll_pd;
    private LinearLayout ll_phoneNumberSet;
    private LinearLayout ll_device;
    private LinearLayout ll_introduction;

    private RotateAnimation swapLineAnimation;
    private RotateAnimation clockwiseRotate;

    private PhoneLostProtectorRecyclerViewAdapter adapter;
    private PhoneLostProtectorPresenter presenter;


    @Override
    protected CharSequence initTitle() {
        return getResources().getString(R.string.title_phone_lost_protector);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_phonelostprotector;
    }

    protected void initViews() {
        swapLine = this.findViewById(R.id.swapline);
        iv_protect = (ImageView) this.findViewById(R.id.iv_protect);
        tv_protectState = (TextView) this.findViewById(R.id.tv_protectstate);
        rv_sets = (RecyclerView) this.findViewById(R.id.rv_sets);

        iv_handle = (ImageView) this.findViewById(R.id.handle);
        ll_pd = (LinearLayout) this.findViewById(R.id.ll_pdmodify);
        ll_phoneNumberSet = (LinearLayout) this
                .findViewById(R.id.ll_phonenumberset);
        ll_device = (LinearLayout) this.findViewById(R.id.ll_device);
        ll_introduction = (LinearLayout) this
                .findViewById(R.id.ll_introduction);

        //设置监听
        iv_protect.setOnClickListener(this);
        ll_pd.setOnClickListener(this);
        ll_phoneNumberSet.setOnClickListener(this);
        ll_device.setOnClickListener(this);
        ll_introduction.setOnClickListener(this);
    }

    @Override
    protected void initCreate() {
        //设置recyclerview
        adapter = new PhoneLostProtectorRecyclerViewAdapter(this);
        rv_sets.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rv_sets.setAdapter(adapter);
        adapter.setOnItemClickedListener(this);

        swapLineAnimation = (RotateAnimation) AnimationUtils.loadAnimation(
                this, R.anim.rotate);
        clockwiseRotate = (RotateAnimation) AnimationUtils.loadAnimation(this,
                R.anim.clockwiserotate);
        iv_handle.startAnimation(clockwiseRotate);//开启handler的动画

        presenter = new PhoneLostProtectorPresenter(this, this);
        presenter.init();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(getClass().getSimpleName(), resultCode + "");
        if (presenter.isDeviceAdmin()) {
            makeToast(getString(R.string.Toast_activity_device_admin_succeed));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_protect:
                presenter.protectingStateChanded();
                break;
            case R.id.ll_pdmodify:
                presenter.resetCheckInPassword();
                break;
            case R.id.ll_phonenumberset:
                presenter.toSetNumbers();
                break;
            case R.id.ll_device:
                presenter.activityOrInactivityDeviceAdmin();
                break;
            case R.id.ll_introduction:
                presenter.introduceFunction();
                break;
        }
    }


    @Override
    public void onProtectOpened() {
        swapLine.startAnimation(swapLineAnimation);
        tv_protectState.setTextColor(getResources().getColor(R.color.state_ok));
        tv_protectState.setText(getResources().getString(R.string.phone_lost_protector_title_current_state_open));
    }

    @Override
    public void onProtectClosed() {
        swapLine.clearAnimation();
        tv_protectState.setTextColor(Color.RED);
        tv_protectState.setText(getResources().getString(R.string.phone_lost_protector_title_current_state_close));
    }

    @Override
    public void onSetsItemGet(List<PhoneLostProtectorSetsItem> itemsData) {
        adapter.setInfos(itemsData);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onNoSimCardFind() {
        makeToast(getString(R.string.toast_no_sim));
    }

    @Override
    public void onNumbersModified() {
        makeToast(getString(R.string.number_modify_succeed));
    }

    @Override
    public void onDeviceAdminInactivitied() {
        makeToast(getResources().getString(R.string.phone_lost_protector_succeed_to_unregist_device_admin));
    }

    @Override
    public void onPasswordModified() {
        makeToast(getResources().getString(R.string.pwd_modify_succeed));
    }

    @Override
    public void onCheckedChanged(PhoneLostProtectorSetsItem data, int position, CheckBox cb, boolean isChecked) {
        presenter.setChanged(data, position, cb, isChecked);
    }
}
