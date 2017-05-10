package edu.ncu.yang.pulltorefreshandload.pullableviews;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

import edu.ncu.yang.pulltorefreshandload.Pullable;

public class PullableWebView extends WebView implements Pullable {

    public PullableWebView(Context context) {
        super(context);
    }

    public PullableWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullableWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean canPullDown() {
        if (getScrollY() == 0)
            return true;
        else
            return false;
    }

    @Override
    public boolean canPullUp() {
        if (getScrollY() >= getContentHeight() * getScale()
                - getMeasuredHeight())
            return true;
        else
            return false;
    }
}
