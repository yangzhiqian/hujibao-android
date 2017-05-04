package edu.ncu.safe.engine;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import edu.ncu.safe.constant.Constant;
import edu.ncu.safe.external.ACache;

/**
 * Created by Yang on 2016/11/3.
 */

public class ImageLoader {
    public static final int LOAD_FAILURE = 0;
    public static final int LOAD_SUCCEED = 1;
    public static final int LOAD_PROGRESS = 2;
    public static final int MAX_TASK = 20;
    private NetDataOperator operator;
    private Executor executor;
    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Object[] os = (Object[]) msg.obj;
            if (os[0] != null) {
                NetDataOperator.OnImageLoadingListener listener = (NetDataOperator.OnImageLoadingListener) os[0];
                switch (msg.what) {
                    case LOAD_FAILURE:
                        listener.onFailure((String) os[1]);
                        break;
                    case LOAD_SUCCEED:
                        listener.onResponse((Bitmap) os[1]);
                        break;
                    case LOAD_PROGRESS:
                        listener.onLoadingProgressChanged((Integer) os[1]);
                        break;
                }
            }
            super.handleMessage(msg);
        }
    };

    public ImageLoader(Context context) {
        this(context, MAX_TASK);
    }

    public ImageLoader(Context context, int concurrenceSize) {
        operator = new NetDataOperator(context.getApplicationContext());
        if (concurrenceSize < 1) {
            concurrenceSize = 1;
        }
        executor = new ThreadPoolExecutor(concurrenceSize, concurrenceSize, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(concurrenceSize));

    }

    public void loadImage(final String fileName, final NetDataOperator.IMG_TYPE type, final NetDataOperator.OnImageLoadingListener listener) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                operator.loadImage(fileName, type, new NetDataOperator.OnImageLoadingListener() {
                    @Override
                    public void onFailure(String error) {
                        Message msg = Message.obtain();
                        msg.what = LOAD_FAILURE;
                        Object[] objects = new Object[2];
                        objects[0] = listener;
                        objects[1] = error;
                        msg.obj = objects;
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onResponse(Bitmap bmp) {
                        Message msg = Message.obtain();
                        msg.what = LOAD_SUCCEED;
                        Object[] objects = new Object[2];
                        objects[0] = listener;
                        objects[1] = bmp;
                        msg.obj = objects;
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onLoadingProgressChanged(int percent) {
                        Message msg = Message.obtain();
                        msg.what = LOAD_PROGRESS;
                        Object[] objects = new Object[2];
                        objects[0] = listener;
                        objects[1] = percent;
                        msg.obj = objects;
                        handler.sendMessage(msg);
                    }
                });
            }
        });
    }
}
