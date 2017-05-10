package edu.ncu.yang.pulltorefreshandload.pullableviews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import edu.ncu.yang.pulltorefreshandload.Pullable;


public class PullableImageView extends ImageView implements Pullable {

    public PullableImageView(Context context) {
        super(context);
    }

    public PullableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullableImageView(Context context, AttributeSet attrs, int defStyle) {
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
