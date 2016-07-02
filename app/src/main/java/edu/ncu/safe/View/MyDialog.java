package edu.ncu.safe.View;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import edu.ncu.safe.R;

/**
 * Created by Mr_Yang on 2016/5/17.
 */
public class MyDialog extends Dialog{
    private Context context;
    private TextView tv_title;
    private TextView tv_message;
    private LinearLayout messageView;
    private View view_divider;
    private LinearLayout ll_YESNO;
    private LinearLayout ll_YES;
    private LinearLayout ll_NO;
    private TextView tv_YES;
    private TextView tv_NO;
    private int maxHeight = 300;

    private View.OnClickListener positiveListener;
    private View.OnClickListener negativeListener;
    public MyDialog(Context context) {
        super(context, R.style.MyDialog);
        this.context = context;
        init();
    }
    private void init(){
        View view = View.inflate(context,R.layout.mydialog,null);
        tv_title = (TextView) view.findViewById(R.id.tv_title	);
        tv_message = (TextView) view.findViewById(R.id.tv_message);
        messageView = (LinearLayout) view.findViewById(R.id.ll_contentview);
        view_divider = view.findViewById(R.id.view_divider);
        ll_YESNO = (LinearLayout) view.findViewById(R.id.ll_YESNO);
        ll_YES = (LinearLayout) view.findViewById(R.id.ll_YES);
        ll_NO = (LinearLayout) view.findViewById(R.id.ll_NO);
        tv_YES = (TextView) view.findViewById(R.id.tv_YES);
        tv_YES = (TextView) view.findViewById(R.id.tv_NO);

        ll_YESNO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (positiveListener != null) {
                    positiveListener.onClick(ll_YES);
                }else{
                    dismiss();
                }
            }
        });

        ll_NO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(negativeListener!=null){
                    negativeListener.onClick(ll_NO);
                }else {
                    dismiss();
                }
            }
        });
        setContentView(view);
    }

    /**
     * 设置对话框的标题
     * @param title
     */
    public void setTitle(CharSequence title){
        this.tv_title.setText(title);
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    /**
     * 设置主体内容 替换掉原来的文本信息  如果要设置新信息的界面的最大高度，请在本方法之前调用setmaxlength
     * @param view
     */
    public void setMessageView(View view){
        messageView.removeAllViews();
        messageView.addView(view);
        messageView.measure(0,0);
        int height  = messageView.getMeasuredHeight();
        if(height>maxHeight){
           ViewGroup.LayoutParams params =  messageView.getLayoutParams();
            params.height = maxHeight;

            messageView.setLayoutParams(params);
        }
    }

    /**
     * 设置确定按钮的点击事件
     * @param listener
     */
    public void setPositiveListener(View.OnClickListener listener){
        this.positiveListener = listener;
    }

    /**
     * 设置取消按钮的点击事件
     * @param listener
     */
    public void setNegativeListener(View.OnClickListener listener){
        this.negativeListener = listener;
    }

    /**
     * 选择是否显示确定取消按钮
     * @param b
     */
    public void ShowYESNO(boolean b){
        if(b){
            ll_YESNO.setVisibility(View.GONE);
        }else{
            ll_YESNO.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置是否设置分界线
     * @param b
     */
    public void showDivider(boolean b){
        if(b){
            view_divider.setVisibility(View.GONE);
        }else{
            view_divider.setVisibility(View.VISIBLE);
        }
    }

    public void setYESText(CharSequence text){
        tv_YES.setText(text);
    }
    public void setNOText(CharSequence text){
        tv_NO.setText(text);
    }
    public void setMessage(CharSequence message){
        tv_message.setText(message);
    }
}
