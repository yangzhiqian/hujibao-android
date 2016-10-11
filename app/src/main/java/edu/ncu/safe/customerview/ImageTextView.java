package edu.ncu.safe.customerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import edu.ncu.safe.R;
import edu.ncu.safe.util.DensityUtil;

/**
 * Created by Mr_Yang on 2016/9/30.
 */
public class ImageTextView extends LinearLayout {
    private boolean hasImg = true ;
    private int textPaddingLeft = 0;
    private int imgWidth = DensityUtil.dip2px(getContext(),20);
    private int imgHeight = DensityUtil.dip2px(getContext(),20);
    private int textSize = DensityUtil.sp2px(getContext(),16);
    private int imgRes = R.drawable.ic_launcher;
    private CharSequence text = "text";
    private  int textColor = Color.BLACK;

    private LinearLayout linearLayout;
    private ImageView imageView;
    private TextView textView;

    public ImageTextView(Context context) {
        super(context);
    }
    public ImageTextView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }
    public ImageTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(context).inflate(R.layout.customview_imagetextbutton, this,true);
        linearLayout = (LinearLayout) view.findViewById(R.id.ll_container);
        imageView = (ImageView) view.findViewById(R.id.img);
        textView = (TextView) view.findViewById(R.id.text);

        if(!isInEditMode()) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ImageTextView, defStyleAttr, 0);
            for (int i = 0; i < array.length(); i++) {
                int index = array.getIndex(i);
                switch (index) {
                    case R.styleable.ImageTextView_distance:
                        textPaddingLeft = array.getDimensionPixelSize(index, textPaddingLeft);
                        break;
                    case R.styleable.ImageTextView_img_width:
                        imgWidth = array.getDimensionPixelSize(index,imgWidth);
                        break;
                    case R.styleable.ImageTextView_img_height:
                        imgHeight = array.getDimensionPixelSize(index, imgHeight);
                        break;
                    case R.styleable.ImageTextView_text_size:
                        textSize = array.getDimensionPixelSize(index,textSize);
                        break;
                    case R.styleable.ImageTextView_has_img:
                        hasImg = array.getBoolean(index, hasImg);
                        break;
                    case R.styleable.ImageTextView_img:
                        imgRes = array.getResourceId(index, imgRes);
                        break;
                    case R.styleable.ImageTextView_text:
                        text = array.getString(index);
                        break;
                    case R.styleable.ImageTextView_textColor:
                        textColor = array.getColor(index, textColor);
                        break;
                }
            }
            array.recycle();
        }
    }



    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        imageView.setVisibility(hasImg?VISIBLE:GONE);
        imageView.setImageResource(imgRes);
        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        params.width = imgWidth;
        params.height = imgHeight;
        imageView.setLayoutParams(params);
        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        textView.setTextColor(textColor);
        textView.setPadding(textPaddingLeft,0,0,0);
    }

    public void setImageResourseID(int res){
        this.imgRes = res;
        imageView.setImageResource(res);
    }
    public void setText(CharSequence text){
        this.text = text;
        textView.setText(text);
    }
    public void setImageVisibility(boolean b){
        this.hasImg = b;
        imageView.setVisibility(hasImg?VISIBLE:GONE);
    }

    public void setTextViewPaddingImg(float dip){
        this.textPaddingLeft = DensityUtil.dip2px(getContext(),dip);
        textView.setPadding(textPaddingLeft,0,0,0);
    }
    public void setTextColor(int color){
        this.textColor = color;
        textView.setTextColor(this.textColor);
    }

    public void setTextSize(int sp){
        this.textSize = DensityUtil.sp2px(getContext(),sp);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,this.textSize);
    }
    public void setImgSize(int dipWidth,int dipHeight){
        this.imgWidth = DensityUtil.dip2px(getContext(),dipWidth);
        this.imgHeight = DensityUtil.dip2px(getContext(),dipHeight);
        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        params.width = imgWidth;
        params.height = imgHeight;
        imageView.setLayoutParams(params);
    }
}
