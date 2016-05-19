package edu.ncu.safe.adapter;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import edu.ncu.safe.ui.fragment.FlowsAppLVFragment;
import edu.ncu.safe.ui.fragment.FlowsDayLVFragment;

public class FlowsStatisticVPAdapter extends FragmentPagerAdapter {

	List<Fragment> fragments = new ArrayList<Fragment>();
	public FlowsStatisticVPAdapter(FragmentManager fragmentManager) {
		super(fragmentManager);
		fragments.add(new FlowsDayLVFragment());
		fragments.add(new FlowsAppLVFragment());
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
