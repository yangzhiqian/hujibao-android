package edu.ncu.safe.adapter;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import edu.ncu.safe.ui.fragment.CommunicationBlackListFragment;
import edu.ncu.safe.ui.fragment.CommunicationMsgFragment;
import edu.ncu.safe.ui.fragment.CommunicationPhoneFragment;
import edu.ncu.safe.ui.fragment.CommunicationWhiteListFragment;

public class CommunicationVPAdatpter extends FragmentPagerAdapter{
	private List<Fragment> fragments;
	public CommunicationVPAdatpter(FragmentManager fm) {
		super(fm);
		fragments = new ArrayList<Fragment>();
		fragments.add(new CommunicationMsgFragment());
		fragments.add(new CommunicationPhoneFragment());
	}

	@Override
	public Fragment getItem(int arg0) {
		return fragments.get(arg0);
	}
	@Override
	public int getCount() {
		return fragments.size();
	}
}
