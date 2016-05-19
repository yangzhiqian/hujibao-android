package edu.ncu.safe.adapter;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import edu.ncu.safe.ui.fragment.GuideFragment1;
import edu.ncu.safe.ui.fragment.GuideFragment2;
import edu.ncu.safe.ui.fragment.GuideFragment3;
import edu.ncu.safe.ui.fragment.GuideFragment4;

public class GuideVPAdapter extends FragmentPagerAdapter {

	List<Fragment> fragments = new ArrayList<Fragment>();
	public GuideVPAdapter(FragmentManager fragmentManager) {
		super(fragmentManager);
		fragments.add(new GuideFragment1());
		fragments.add(new GuideFragment2());
		fragments.add(new GuideFragment3());
		fragments.add(new GuideFragment4());
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
