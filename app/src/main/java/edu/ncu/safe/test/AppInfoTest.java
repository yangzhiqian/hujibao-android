package edu.ncu.safe.test;

import android.test.AndroidTestCase;

import edu.ncu.safe.engine.LoadAppInfos;

/**
 * Created by Mr_Yang on 2016/5/21.
 */
public class AppInfoTest extends AndroidTestCase{
    public void test(){
        LoadAppInfos loadAppInfos = new LoadAppInfos(getContext());
        loadAppInfos.getUserAppBaseInfo();
    }
}
