package edu.ncu.safe.ui;

import android.os.Bundle;

import edu.ncu.safe.R;
import edu.ncu.safe.myadapter.MyAppCompatActivity;

public class ShareActivity extends MyAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        initToolBar(getResources().getString(R.string.title_share));
    }
}
