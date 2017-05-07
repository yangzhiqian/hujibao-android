package edu.ncu.safe.ui;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.List;

import edu.ncu.safe.R;
import edu.ncu.safe.adapter.SimpleFragmentPagerAdapter;
import edu.ncu.safe.base.activity.BackAppCompatActivity;
import edu.ncu.safe.ui.fragment.CommunicationBlackListFragment;
import edu.ncu.safe.ui.fragment.CommunicationWhiteListFragment;

public class CommunicationWhiteBlackSetActivity extends BackAppCompatActivity {

    private TabLayout tab;
    private ViewPager vpWhiteBlackList;


    @Override
    protected int initLayout() {
        return R.layout.activity_communicationwhiteblackset;
    }

    @Override
    protected void initViews() {
        tab = (TabLayout) findViewById(R.id.tab_communication);
        vpWhiteBlackList = (ViewPager) this.findViewById(R.id.vp_communication);
    }

    @Override
    protected void initCreate() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new CommunicationWhiteListFragment());
        fragments.add(new CommunicationBlackListFragment());
        vpWhiteBlackList.setAdapter(new SimpleFragmentPagerAdapter(
                this,
                getSupportFragmentManager(),
                fragments,
                new String[]{"白名单", "黑名单"}));

        tab.setupWithViewPager(vpWhiteBlackList);
        tab.setTabMode(TabLayout.MODE_FIXED);
    }


    @Override
    protected CharSequence initTitle() {
        return getResources().getString(R.string.title_communication_protector_white_black_number_set);
    }
}
