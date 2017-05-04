package edu.ncu.safe.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.List;

import edu.ncu.safe.R;
import edu.ncu.safe.adapter.SimpleFragmentPagerAdapter;
import edu.ncu.safe.ui.fragment.CommunicationBlackListFragment;
import edu.ncu.safe.ui.fragment.CommunicationWhiteListFragment;

public class CommunicationWhiteBlackSetActivity extends MyAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communicationwhiteblackset);
        initToolBar(getResources().getString(R.string.title_communication_protector_white_black_number_set));
        TabLayout tab = (TabLayout) findViewById(R.id.tab_communication);
        ViewPager vpWhiteBlackList = (ViewPager) this.findViewById(R.id.vp_communication);

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new CommunicationWhiteListFragment());
        fragments.add(new CommunicationBlackListFragment());
        vpWhiteBlackList.setAdapter(new SimpleFragmentPagerAdapter(
                this,
                getSupportFragmentManager(),
                fragments,
                new String[]{"白名单", "黑名单"},
                new int[]{R.drawable.whitelist, R.drawable.blacklist}));

        tab.setupWithViewPager(vpWhiteBlackList);
        tab.setTabMode(TabLayout.MODE_FIXED);
    }
}
