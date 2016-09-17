package edu.ncu.safe.customerview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by Mr_Yang on 2016/6/22.
 */
public class MyTouchImageView extends ImageView {
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private Matrix matrix = new Matrix();
    private Matrix matrix_changing = new Matrix();
    private Matrix matrix_old = new Matrix();
    int mode = NONE;
    float x_down = 0;
    float y_down = 0;
    float oldDist = 1f;
    float oldRotation = 0;
    PointF mid = new PointF();

    private Bitmap bitmap;

    public MyTouchImageView(Context context) {
        super(context);
    }

    public MyTouchImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTouchImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        setImageMatrix(matrix);
        super.onDraw(canvas);
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mode = DRAG;
                x_down = event.getX();
                y_down = event.getY();
                matrix_old.set(matrix);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mode = ZOOM;
                oldDist = spacing(event);
                oldRotation = rotation(event);
                matrix_old.set(matrix);
                midPoint(mid, event);
                break;
            case MotionEvent.ACTION_MOVE:
                matrix_changing.set(matrix_old);
                if (mode == ZOOM) {
                    float rotation = rotation(event) - oldRotation;
                    float scale = spacing(event) / oldDist;
                    matrix_changing.postScale(scale, scale, mid.x, mid.y);// 缩放
                    matrix_changing.postRotate(rotation, mid.x, mid.y);// 旋转
                } else if (mode == DRAG) {
                    matrix_changing.set(matrix_old);
                    matrix_changing.postTranslate(event.getX() - x_down, event.getY()
                            - y_down);// 平移
                }
                matrix.set(matrix_changing);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
        }
        return true;
    }

    // 触碰两点间距离
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    // 取手势中心点
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    // 取旋转角度
    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }
}
