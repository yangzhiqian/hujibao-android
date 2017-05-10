package edu.ncu.yang.pulltorefreshandload.pullableviews;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import edu.ncu.yang.pulltorefreshandload.Pullable;

public class PullableTextView extends TextView implements Pullable {

    public PullableTextView(Context context) {
        super(context);
    }

    public PullableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullableTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean canPullDown() {
        return true;
    }

    @Override
    public boolean canPullUp() {
        return true;
    }

}
