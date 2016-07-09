package edu.ncu.safe.test;

import android.test.AndroidTestCase;

import edu.ncu.safe.R;
import edu.ncu.safe.engine.DataLoader;

/**
 * Created by Mr_Yang on 2016/7/4.
 */
public class LoadTest extends AndroidTestCase {
    public void test(){
        DataLoader loader = new DataLoader(getContext());
        String url = getContext().getResources().getString(R.string.loadbackup);
        String[] valuesNames = {"token","type","offset","number"};
        String[] values = {"193FD0F61F77EAAA30976F53A62FAC0F","0","0","10"};
        loader.loadServerJson(url,valuesNames,values);
    }
}
