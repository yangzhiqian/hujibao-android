package edu.ncu.safe.ui;

import edu.ncu.safe.R;
import edu.ncu.safe.base.activity.BackAppCompatActivity;

public class ShareActivity extends BackAppCompatActivity {

    @Override
    protected int initLayout() {
        return R.layout.activity_share;
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initCreate() {

    }

    @Override
    protected CharSequence initTitle() {
        return getResources().getString(R.string.title_share);
    }
}
