package edu.ncu.safe.adapter;

import java.util.ArrayList;
import java.util.List;

import edu.ncu.safe.ui.fragment.CommunicationBlackListFragment;
import edu.ncu.safe.ui.fragment.CommunicationMsgFragment;
import edu.ncu.safe.ui.fragment.CommunicationPhoneFragment;
import edu.ncu.safe.ui.fragment.CommunicationWhiteListFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class CommunicationWhiteBlackSetVPAdatpter extends FragmentPagerAdapter{
	private List<Fragment> fragments;
	public CommunicationWhiteBlackSetVPAdatpter(FragmentManager fm) {
		super(fm);
		fragments = new ArrayList<Fragment>();
		fragments.add(new CommunicationWhiteListFragment());
		fragments.add(new CommunicationBlackListFragment());
		
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
