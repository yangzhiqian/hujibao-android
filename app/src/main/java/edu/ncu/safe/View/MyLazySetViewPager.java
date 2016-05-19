package edu.ncu.safe.View;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

public class MyLazySetViewPager extends ViewPager {
    PagerAdapter mPagerAdapter;

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mPagerAdapter != null) {
            super.setAdapter(mPagerAdapter);
           // mPageIndicator.setViewPager(this);
        }
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
    }

    public void storeAdapter(PagerAdapter pagerAdapter) {
        mPagerAdapter = pagerAdapter;
    }

    public MyLazySetViewPager(Context context) {
        super(context);
    }

    public MyLazySetViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

}