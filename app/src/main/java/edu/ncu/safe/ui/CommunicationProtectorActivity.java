package edu.ncu.safe.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.List;

import edu.ncu.safe.R;
import edu.ncu.safe.adapter.SimpleFragmentPagerAdapter;
import edu.ncu.safe.ui.fragment.CommunicationMsgFragment;
import edu.ncu.safe.ui.fragment.CommunicationPhoneFragment;

public class CommunicationProtectorActivity extends MyAppCompatActivity implements OnClickListener {
    private ImageView iv_showPopup;
    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communicationprotector);
        initToolBar(getResources().getString(R.string.title_communication_protector));
        iv_showPopup = (ImageView) findViewById(R.id.iv_showpopup);
        TabLayout tab = (TabLayout) findViewById(R.id.tab_communication);
        ViewPager vp_protectRecorder = (ViewPager) this.findViewById(R.id.vp_communication);


        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new CommunicationMsgFragment());
        fragments.add(new CommunicationPhoneFragment());
        vp_protectRecorder.setAdapter(new SimpleFragmentPagerAdapter(
                this,
                getSupportFragmentManager(),
                fragments,
                new String[]{"短信拦截", "电话拦截"},
                new int[]{R.drawable.message_comm, R.drawable.phone}));

        iv_showPopup.setOnClickListener(this);
        tab.setupWithViewPager(vp_protectRecorder);
        tab.setTabMode(TabLayout.MODE_FIXED);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_showpopup:
                showPopup();
                break;
            case R.id.ll_blackwhitelist:
                toAntherAvitvity(CommunicationWhiteBlackSetActivity.class);
                break;
            case R.id.ll_interceptionsetting:
                toAntherAvitvity(CommunicationInterceptionSet.class);
                break;
            case R.id.ll_phonenumberplace:
                toAntherAvitvity(CommunicationNumberQuery.class);
                break;
        }
        popupWindow.dismiss();
    }

    private void showPopup() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.popupwindow_commore, null);
        LinearLayout ll_backList = (LinearLayout) contentView.findViewById(R.id.ll_blackwhitelist);
        LinearLayout ll_interceptonSetting = (LinearLayout) contentView.findViewById(R.id.ll_interceptionsetting);
        LinearLayout ll_phoneNumberPlace = (LinearLayout) contentView.findViewById(R.id.ll_phonenumberplace);

        ll_backList.setOnClickListener(this);
        ll_interceptonSetting.setOnClickListener(this);
        ll_phoneNumberPlace.setOnClickListener(this);
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        popupWindow = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        //popupWindow设置animation一定要在show之前
        popupWindow.setAnimationStyle(R.style.popupanimation);

        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        //一定要设置背景，否则无法自动消失
        Drawable background = getResources().getDrawable(
                R.drawable.popupbgtop);
        popupWindow.setBackgroundDrawable(background);
        iv_showPopup.measure(0, 0);
        popupWindow.showAsDropDown(iv_showPopup, iv_showPopup.getMeasuredWidth() - contentView.getMeasuredWidth() + 10, 0);
    }
}
