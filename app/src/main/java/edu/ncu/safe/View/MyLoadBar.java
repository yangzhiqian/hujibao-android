package edu.ncu.safe.View;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Mr_Yang on 2016/5/25.
 */
public class MyLoadBar extends View {
    private Paint pgPaint;
    private float arcWidth;
    private  float acrPercent = 0.15f;
    private float radiusPercent = 0.05f;
    private float radiusWidth = 10;
    private SweepGradient shader;
    private int beginColor = Color.WHITE;
    private int endColor = Color.RED;
    private int textColor = Color.GREEN;
    private int progress = 100;
    private RectF rect;
    private float centerX;
    private float centerY;
    private boolean isShowProgress = false;
    private int textSize = 30;

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public float getAcrPercent() {
        return acrPercent;
    }

    public void setAcrPercent(float acrPercent) {
        this.acrPercent = acrPercent;
    }

    public float getRadiusPercent() {
        return radiusPercent;
    }

    public void setRadiusPercent(float radiusPercent) {
        this.radiusPercent = radiusPercent;
    }

    public int getBeginColor() {
        return beginColor;
    }

    public void setBeginColor(int beginColor) {
        this.beginColor = beginColor;
    }

    public int getEndColor() {
        return endColor;
    }

    public void setEndColor(int endColor) {
        this.endColor = endColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public boolean isShowProgress() {
        return isShowProgress;
    }

    public void setIsShowProgress(boolean isShowProgress) {
        this.isShowProgress = isShowProgress;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }



    public MyLoadBar(Context context) {
        super(context);
    }

    public MyLoadBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLoadBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onSizeChanged(int w, int h, int ow, int oh) {
        super.onSizeChanged(w, h, ow, oh);
        centerX = w * 0.5f;  // remember the center of the screen
        centerY = h *0.5f;
        arcWidth = w*acrPercent;
        radiusWidth = w*radiusPercent;

        rect = new RectF(arcWidth/2, arcWidth/2,w-arcWidth/2, h-arcWidth/2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //进度
        pgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pgPaint.setStyle(Paint.Style.STROKE);
        pgPaint.setStrokeWidth(arcWidth);
        shader = new SweepGradient(centerX,centerY,beginColor,endColor);
        pgPaint.setShader(shader);
        BlurMaskFilter mBGBlur = new BlurMaskFilter(radiusWidth, BlurMaskFilter.Blur.INNER);
        pgPaint.setMaskFilter(mBGBlur);
        pgPaint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawArc(rect, 30, 300, false, pgPaint);

        if(isShowProgress) {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setTextSize(textSize);
            paint.setColor(textColor);
            float[] widths = new float[(progress + "%").length()];
            paint.breakText(progress + "%", true, rect.width(), widths);
            float width = 0;
            for (float w : widths) {
                width += w;
            }
            canvas.drawText((int) progress + "%", centerX - width / 2, centerY + paint.getTextSize() / 2, paint);
        }
        invalidate();
    }

}
