package edu.ncu.safe.ui;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.ncu.safe.R;
import edu.ncu.safe.adapter.AppManagerPVAdapter;
import edu.ncu.safe.domain.UserAppBaseInfo;
import edu.ncu.safe.myadapter.MyAppCompatActivity;
import edu.ncu.safe.ui.fragment.AppManagerFragment;
import edu.ncu.safe.ui.fragment.NetManagerFragment;
import edu.ncu.safe.util.BitmapUtil;

/**
 * Created by Mr_Yang on 2016/5/19.
 */
public class AppManagerAcitvity extends MyAppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener,AppManagerFragment.OnDataChangeListener {
    private ViewPager vp_appManager;

    private LinearLayout ll_appManager;
    private LinearLayout ll_netManager;
    private ImageView iv_appManager;
    private TextView tv_appManager;
    private ImageView iv_netManager;
    private TextView tv_netManager;

    private ColorStateList enableTextColor;
    private ColorStateList diableTextColor;
    private Drawable enableBackgroundDrawable;
    private Drawable diableBackgroundDrawable;

    private List<Fragment> fragments ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appmanager);
        initToolBar(getResources().getString(R.string.title_app_manager));
        vp_appManager = (ViewPager) this.findViewById(R.id.vp_appmanager);
        ll_appManager  = (LinearLayout) this.findViewById(R.id.ll_appmanager);
        ll_netManager = (LinearLayout) this.findViewById(R.id.ll_netmanager);
        iv_appManager = (ImageView) this.findViewById(R.id.iv_appmanager);
        tv_appManager = (TextView) this.findViewById(R.id.tv_appmanager);
        iv_netManager = (ImageView) this.findViewById(R.id.iv_netmanager);
        tv_netManager = (TextView) this.findViewById(R.id.tv_netmanager);

        enableBackgroundDrawable =ll_appManager.getBackground();
        diableBackgroundDrawable =ll_netManager.getBackground();
        enableTextColor = tv_appManager.getTextColors();
        diableTextColor = tv_netManager.getTextColors();

        fragments = new ArrayList<Fragment>();
        fragments.add(new AppManagerFragment());
        fragments.add(new NetManagerFragment());
        vp_appManager.setAdapter(new AppManagerPVAdapter(getSupportFragmentManager(),fragments));

        ll_appManager.setOnClickListener(this);
        ll_netManager.setOnClickListener(this);
        vp_appManager.addOnPageChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.ll_appmanager:
                vp_appManager.setCurrentItem(0);
                break;
            case R.id.ll_netmanager:
                vp_appManager.setCurrentItem(1);
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }
    @Override
    public void onPageSelected(int position) {
        if(position==0){
            ll_appManager.setBackground(enableBackgroundDrawable);
            iv_appManager.setImageBitmap(BitmapUtil.readBitMap(this,R.drawable.appmanager));
            tv_appManager.setTextColor(enableTextColor);

            ll_netManager.setBackground(diableBackgroundDrawable);
            iv_netManager.setImageBitmap(BitmapUtil.readBitMap(this, R.drawable.netmanagementgray));
            tv_netManager.setTextColor(diableTextColor);
        }else{
            ll_appManager.setBackground(diableBackgroundDrawable);
            iv_appManager.setImageBitmap(BitmapUtil.readBitMap(this,R.drawable.appmanagergray));
            tv_appManager.setTextColor(diableTextColor);

            ll_netManager.setBackground(enableBackgroundDrawable);
            iv_netManager.setImageBitmap(BitmapUtil.readBitMap(this,R.drawable.netmanagement));
            tv_netManager.setTextColor(enableTextColor);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void dataChange(List<UserAppBaseInfo> infos, String packName) {
        ((NetManagerFragment)fragments.get(1)).onDataChanged(infos,packName);
    }
}
