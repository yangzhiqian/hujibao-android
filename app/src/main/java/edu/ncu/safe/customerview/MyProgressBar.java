package edu.ncu.safe.customerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import edu.ncu.safe.R;
import edu.ncu.safe.util.MyMathUtil;

/**
 * Created by Mr_Yang on 2016/5/26.
 */
public class MyProgressBar extends View {
    public static final int FLASHTIME = 30;
    public static final int PROGRESS_STYLE_CIRCULAR_VERTICAL = 0;
    public static final int PROGRESS_STYLE_ARC = 1;
    public static final int PROGRESS_STYLE_LOOP = 2;
    public static final int PROGRESS_STYLE_RECTANGLE_HORIZONTAL = 3;
    public static final int PROGRESS_STYLE_RECTANGLE_VERTIVAL = 4;
    public static final float PERPERCENT = 0.3f;

    private int width;
    private int height;
    private int beginX = 0;
    private int beginY = 0;
    private RectF rect;
    private RectF loopRect;
    private boolean shouldSetRect = true;
    private float titleHeight = 0;

    private Paint progressBkPaint;
    private Paint progressPgPaint;
    private Paint percentPaint;
    private Paint titlePaint;

    //进度样式的shader
    LinearGradient circleVerGradient;
    LinearGradient rectHorGradient;
    LinearGradient rectVerGradient;
    SweepGradient arcGradient;
    SweepGradient loopGradient;

    private boolean hasTitle = false;//时候有标题
    private String title = "title";   //标题的文本
    private float titleTextSize = 30;//标题文本的大小
    private int titleTextColor = Color.parseColor("#6cbd45");//标题的颜色
    private boolean hasPercent = true;  //是否显示百分比
    private float percent = 0;        //百分比的值
    private float percentTextSize = 30;//百分比显示的字体大小
    private int percentTextColor = Color.WHITE;  //百分比的颜色
    private int unuseColor = Color.parseColor("#66d2d2d2");   //背景圆圈的颜色
    private int beginColor = Color.parseColor("#6cbd45");     //占比的起始颜色
    private int endColor = Color.parseColor("#FFCC5911");       //占比的结束颜色
    private int progressStyle = PROGRESS_STYLE_ARC;             //进度条的类型
    private boolean isSweeping = false;                         //是否有动画效果
    private float titleMarginTop = 5;

    private float arcWidth = 20;
    private int loopSpeed = 1000;
    private float arcLength = 300;

    private int times = 0;//loop次数
    private int totalTimes = loopSpeed / FLASHTIME;

    private float toPercent = 0;
    private ToPercentRunnable toPercentRunnable;
    float d;
    long sleepTime = (long) (loopSpeed * PERPERCENT / 100);

    public MyProgressBar(Context context) {
        super(context);
        progressStyle = PROGRESS_STYLE_LOOP;
    }

    public MyProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        /**
         * 获得我们所定义的自定义样式属性
         */
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MyProgressBar, defStyleAttr, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.MyProgressBar_hasTitle:
                    hasTitle = a.getBoolean(attr, false);
                    break;
                case R.styleable.MyProgressBar_title:
                    title = a.getString(attr);
                    break;
                case R.styleable.MyProgressBar_percentColor:
                    percentTextColor = a.getColor(attr, percentTextColor);
                    break;
                case R.styleable.MyProgressBar_percentSize:
                    percentTextSize = a.getDimension(attr, percentTextSize);
                    break;
                case R.styleable.MyProgressBar_titleColor:
                    titleTextColor = a.getColor(attr, titleTextColor);
                    break;
                case R.styleable.MyProgressBar_titleSize:
                    titleTextSize = a.getDimension(attr, 30);
                    break;
                case R.styleable.MyProgressBar_unusedColor:
                    unuseColor = a.getColor(attr, unuseColor);
                    break;
                case R.styleable.MyProgressBar_usedBeginColor:
                    beginColor = a.getColor(attr, beginColor);
                    break;
                case R.styleable.MyProgressBar_usedEndColor:
                    endColor = a.getColor(attr, endColor);
                    break;
                case R.styleable.MyProgressBar_progressStyle:
                    progressStyle = a.getInt(attr, progressStyle);
                    break;
                case R.styleable.MyProgressBar_hasPercent:
                    hasPercent = a.getBoolean(attr, true);
                    break;
                case R.styleable.MyProgressBar_sweeping:
                    isSweeping = a.getBoolean(attr, false);
                    break;
                case R.styleable.MyProgressBar_loopSpeed:
                    loopSpeed = a.getInt(attr, loopSpeed);
                    totalTimes = loopSpeed / FLASHTIME;
                    sleepTime = (long) (loopSpeed * PERPERCENT / 100);
                    break;
                case R.styleable.MyProgressBar_arcLength:
                    arcLength = a.getFloat(attr, arcLength);
                    break;
                case R.styleable.MyProgressBar_arcWidth:
                    arcWidth = a.getDimension(attr, arcWidth);
                    break;
                case R.styleable.MyProgressBar_titleMarginTopToProcessBar:
                    titleMarginTop = a.getDimension(attr, titleMarginTop);
                    break;
            }
        }
        a.recycle();
        init();
    }

    private void init(){
        //设置背景进度画笔
        progressBkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressBkPaint.setColor(unuseColor);
        //设置进度画笔
        progressPgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        if(progressStyle==PROGRESS_STYLE_LOOP){
            progressPgPaint.setStyle(Paint.Style.STROKE);
            progressPgPaint.setStrokeWidth(arcWidth);
            BlurMaskFilter maskFilter = new BlurMaskFilter(arcWidth / 3, BlurMaskFilter.Blur.INNER);
            progressPgPaint.setMaskFilter(maskFilter);
            progressPgPaint.setStrokeCap(Paint.Cap.ROUND);
        }

        //设置百分比
        percentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        PorterDuffXfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER);
        percentPaint.setXfermode(xfermode);
        percentPaint.setTextSize(percentTextSize);
        percentPaint.setColor(percentTextColor);
        //设置标题
        titlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        titlePaint.setTextSize(titleTextSize);
        titlePaint.setColor(titleTextColor);
    }

    private void initShader(){
        circleVerGradient = new LinearGradient(beginX + rect.width() / 2, beginY + rect.height(), beginX + rect.width() / 2, beginY, beginColor, endColor, Shader.TileMode.CLAMP);
        arcGradient = new SweepGradient(beginX + rect.width() / 2, beginY + rect.height() / 2, beginColor, endColor);//Shader.TileMode.CLAMP
        loopGradient = new SweepGradient(beginX + rect.width() / 2, beginY + rect.height() / 2, endColor, beginColor);//Shader.TileMode.CLAMP
        rectHorGradient = new LinearGradient(beginX, beginY + rect.height() / 2, beginX + rect.width(), beginY + rect.height() / 2, beginColor, endColor, Shader.TileMode.CLAMP);
        rectVerGradient = new LinearGradient(beginX + rect.width() / 2, beginY + rect.height(), beginX + rect.width() / 2, beginY, beginColor, endColor, Shader.TileMode.CLAMP);
    }
    @Override
    protected void onSizeChanged(int w, int h, int ow, int oh) {
        super.onSizeChanged(w, h, ow, oh);
        width = w;
        height = h;
        shouldSetRect = true;
    }

    private Lock lock;

    @Override
    protected void onDraw(Canvas canvas) {
        //是否需要重新计算rect和起始坐标
        if (shouldSetRect) {
            setRect();
            initShader();
        }
        //画进度条
        switch (progressStyle) {
            case PROGRESS_STYLE_CIRCULAR_VERTICAL:
                drawStyleVertical(canvas);
                break;
            case PROGRESS_STYLE_ARC:
                drawStyleArc(canvas);
                break;
            case PROGRESS_STYLE_LOOP:
                drawStyleLoop(canvas);
                break;
            case PROGRESS_STYLE_RECTANGLE_HORIZONTAL:
                drawStyleRectangleHorizontal(canvas);
                break;
            case PROGRESS_STYLE_RECTANGLE_VERTIVAL:
                drawStyleRectangleVertical(canvas);
                break;
        }
        //画百分比
        if (hasPercent) {
            drawPercent(canvas);
        }
        //画标题
        if (titleHeight > 0) {
            drawTitle(canvas);
        }

        if (isSweeping) {
            percent = (float) ((percent + FLASHTIME * 100.0 / loopSpeed) % 100);
        }
        if (isSweeping || progressStyle == PROGRESS_STYLE_LOOP) {
            postInvalidateDelayed(FLASHTIME);
        }
    }

    /**
     * 根据有无标题设置要画的区域，设置rect
     */
    private void setRect() {
        Paint.FontMetrics fontMetrics = titlePaint.getFontMetrics();
        titleHeight = fontMetrics.bottom - fontMetrics.top + titleMarginTop;
        if (!hasTitle) {
            titleHeight = 0;
        }
        if (progressStyle == PROGRESS_STYLE_RECTANGLE_HORIZONTAL || progressStyle == PROGRESS_STYLE_RECTANGLE_VERTIVAL) {
            beginX = 0;
            beginY = 0;
            rect = new RectF(beginX, beginY, width, height - titleHeight);
            shouldSetRect = false;
            return;
        }
        if (height - titleHeight > width) {
            //高大,圆以宽做直径
            beginX = 0;
            beginY = (int) ((height - width - titleHeight) / 2);
            rect = new RectF(beginX, beginY, beginX + width, beginY + width);
        } else {
            //宽大,圆以height-titleHeight做直径
            beginX = (int) ((width - (height - titleHeight)) / 2);
            beginY = 0;
            rect = new RectF(beginX, beginY, beginX + height - titleHeight, beginY + height - titleHeight);
        }
        if (progressStyle == PROGRESS_STYLE_LOOP) {
            //减去因为弧线的厚度而产生的宽度
            loopRect = new RectF(rect.left + arcWidth / 2, rect.top + arcWidth / 2, rect.right - arcWidth / 2, rect.bottom - arcWidth / 2);
        }
        shouldSetRect = false;
    }

    private void drawStyleRectangleHorizontal(Canvas canvas) {
        //画背景
        canvas.drawRect(rect, progressBkPaint);
        //画进度
        progressPgPaint.setShader(rectHorGradient);
        canvas.drawRect(beginX, beginY, beginX + rect.width() * percent / 100, beginY + rect.height(), progressPgPaint);
    }

    private void drawStyleRectangleVertical(Canvas canvas) {
        //画背景
        canvas.drawRect(rect, progressBkPaint);
        //画进度
        progressPgPaint.setShader(rectVerGradient);
        canvas.drawRect(beginX, beginY + rect.height() - rect.height() * percent / 100, beginX + rect.width(), beginY + rect.height(), progressPgPaint);
    }

    private void drawStyleVertical(Canvas canvas) {
        //画背景圆
        canvas.drawCircle(beginX + rect.width() / 2, beginY + rect.height() / 2, rect.width() / 2, progressBkPaint);
        //画进度
        progressPgPaint.setShader(circleVerGradient);
        float degree = MyMathUtil.toAngle(0, 360, percent, 10);
        canvas.drawArc(rect, 90 - degree / 2, degree, false, progressPgPaint);
    }

    private void drawStyleArc(Canvas canvas) {
        //画背景圆
        canvas.drawCircle(beginX + rect.width() / 2, beginY + rect.height() / 2, rect.width() / 2, progressBkPaint);
        //画进度
        Matrix matrix = new Matrix();
        matrix.setRotate(-90, beginX + rect.width() / 2, beginY + rect.height() / 2);
        arcGradient.setLocalMatrix(matrix);
        progressPgPaint.setShader(arcGradient);
        canvas.drawArc(rect, -90, (float) (percent * 3.6), true, progressPgPaint);
    }

    private void drawStyleLoop(Canvas canvas) {
        Matrix matrix = new Matrix();
        float dAngle = times * 360.0f / totalTimes;
        matrix.setRotate(dAngle, beginX + rect.width() / 2, beginY + rect.height() / 2);
        loopGradient.setLocalMatrix(matrix);
        progressPgPaint.setShader(loopGradient);
        canvas.drawArc(loopRect, 180 + dAngle - arcLength / 2, arcLength, false, progressPgPaint);
        times = (times + 1) % totalTimes;
    }

    private void drawPercent(Canvas canvas) {
        //写上百分比
        Paint.FontMetrics fontMetrics = percentPaint.getFontMetrics();
        String p = (percent - (int) percent - 0.5f) > 0 ? (int) (percent + 1) + "%" : (int) percent + "%";
        float w = percentPaint.measureText(p);
        float x = beginX + rect.width() / 2 - w / 2;
        float y = beginY + rect.height() / 2 + (fontMetrics.leading - (fontMetrics.top + fontMetrics.bottom) / 2);
        canvas.drawText(p, x, y, percentPaint);
    }

    private void drawTitle(Canvas canvas) {
        //写上标题
        float titleWidth = titlePaint.measureText(title);
        Paint.FontMetrics fontMetrics = titlePaint.getFontMetrics();
        float x = beginX + rect.width() / 2 - titleWidth / 2;
        float y = beginY + rect.height() - fontMetrics.top + titleMarginTop;
        canvas.drawText(title, x, y, titlePaint);
    }


    private void toPercent(float toNewPercent) {
        if (isSweeping) {
            isSweeping = false;
            percent = 0;
        }
        new Thread(new ToPercentRunnable(toNewPercent)).start();
    }

    public float getTitleTextSize() {
        return titleTextSize;
    }

    public void setTitleTextSize(float titleTextSize) {
        this.titleTextSize = titleTextSize;
        titlePaint.setTextSize(titleTextSize);
        shouldSetRect = true;
        invalidate();
    }

    public float getPercent() {
        return percent;
    }

    public void setPercentSlow(float percent) {
        toPercent(percent);
    }

    public void setPercentimmediately(float percent) {
        this.percent = percent;
        invalidate();
    }

    public int getTitleTextColor() {
        return titleTextColor;
    }

    public void setTitleTextColor(int titleTextColor) {
        this.titleTextColor = titleTextColor;
        titlePaint.setColor(titleTextColor);
        invalidate();

    }

    public float getPercentTextSize() {
        return percentTextSize;
    }

    public void setPercentTextSize(float percentTextSize) {
        this.percentTextSize = percentTextSize;
        percentPaint.setTextSize(percentTextSize);
        invalidate();
    }

    public int getPercentTextColor() {
        return percentTextColor;
    }

    public void setPercentTextColor(int percentTextColor) {
        this.percentTextColor = percentTextColor;
        percentPaint.setColor(percentTextColor);
        invalidate();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        shouldSetRect = true;
        invalidate();
    }

    public int getUnuseColor() {
        return unuseColor;
    }

    public void setUnuseColor(int unuseColor) {
        this.unuseColor = unuseColor;
        progressBkPaint.setColor(unuseColor);
        invalidate();
    }

    public int getBeginColor() {
        return beginColor;
    }

    public void setBeginColor(int beginColor) {
        this.beginColor = beginColor;
        initShader();
        invalidate();
    }

    public int getEndColor() {
        return endColor;
    }

    public void setEndColor(int endColor) {
        this.endColor = endColor;
        initShader();
        invalidate();
    }

    public boolean isHasTitle() {
        return hasTitle;
    }

    public void setHasTitle(boolean hasTitle) {
        if(this.hasPercent==hasTitle){
            return;
        }
        this.hasTitle = hasTitle;
        shouldSetRect = true;
        invalidate();
    }

    public boolean isHasPercent() {
        return hasPercent;
    }

    public void setHasPercent(boolean hasPercent) {
        if(this.hasPercent==hasPercent){
            return;
        }
        this.hasPercent = hasPercent;
        invalidate();
    }

    public int getProgressStyle() {
        return progressStyle;
    }

    public void setProgressStyle(int progressStyle) {
        this.progressStyle = progressStyle;
        init();
        shouldSetRect = true;
        invalidate();
    }

    public float getArcWidth() {
        return arcWidth;
    }

    public void setArcWidth(float arcWidth) {
        this.arcWidth = arcWidth;
        if(progressStyle==PROGRESS_STYLE_LOOP){
            progressPgPaint.setStyle(Paint.Style.STROKE);
            progressPgPaint.setStrokeWidth(arcWidth);
            BlurMaskFilter maskFilter = new BlurMaskFilter(arcWidth / 3, BlurMaskFilter.Blur.INNER);
            progressPgPaint.setMaskFilter(maskFilter);
            progressPgPaint.setStrokeCap(Paint.Cap.ROUND);
        }else{
            return;
        }
        invalidate();
    }

    public int getLoopSpeed() {
        return loopSpeed;
    }

    public void setLoopSpeed(int loopSpeed) {
        this.loopSpeed = loopSpeed;
        sleepTime = (long) (loopSpeed * PERPERCENT / 100);
        times = 0;
        invalidate();
    }

    public float getArcLength() {
        return arcLength;
    }

    public void setArcLength(float arcLength) {
        this.arcLength = arcLength;
        invalidate();
    }

    public float getTitleMarginTop() {
        return titleMarginTop;
    }

    public void setTitleMarginTop(float titleMarginTop) {
        this.titleMarginTop = titleMarginTop;
        shouldSetRect = true;
        invalidate();
    }

    class ToPercentRunnable implements Runnable {
        private float toNewPercent = 0;

        public ToPercentRunnable(float toNewPercent) {
            this.toNewPercent = toNewPercent;
        }

        @Override
        public void run() {
            if (lock == null) {
                lock = new ReentrantLock();
            }
            if (lock.tryLock()) {
                toPercent = toNewPercent;
                d = toPercent > percent ? PERPERCENT : -1 * PERPERCENT;
                while (Math.abs(toPercent - percent) > PERPERCENT) {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    percent += d;
                    postInvalidate();
                }
                lock.unlock();
            } else {
                percent = toPercent;
                toPercent = toNewPercent;
                d = toPercent > percent ? PERPERCENT : -1 * PERPERCENT;
            }
        }
    }
}
