package edu.ncu.safe.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import java.util.List;

/**
 * Created by Yang on 2017/5/4.<br/>
 * tabLayout的滑动页面适配器
 */

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context context;
    private String[] tabTitles;
    private int[] ids;
    private List<Fragment> fragments;


    public SimpleFragmentPagerAdapter(Context context,FragmentManager fm,List<Fragment> fragments, String[] tabTitles) {
        this(context,fm,fragments,tabTitles,null);
    }
    public SimpleFragmentPagerAdapter(Context context,FragmentManager fm,List<Fragment> fragments, String[] tabTitles,int[] ids) {
        super(fm);
        this.context = context;
        this.fragments =fragments;
        this.tabTitles = tabTitles;
        this.ids = ids;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(ids==null){
            return tabTitles[position];
        }
        Drawable image = context.getResources().getDrawable(ids[position]);
        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
        // Replace blank spaces with image icon
        SpannableString sb = new SpannableString("   " + tabTitles[position]);
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }
}
