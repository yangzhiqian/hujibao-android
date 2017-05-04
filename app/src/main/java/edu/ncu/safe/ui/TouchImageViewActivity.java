package edu.ncu.safe.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import edu.ncu.safe.R;
import edu.ncu.safe.customerview.MyProgressBar;
import edu.ncu.safe.customerview.MyTouchImageView;
import edu.ncu.safe.engine.ImageLoader;
import edu.ncu.safe.engine.NetDataOperator;
import edu.ncu.safe.util.BitmapUtil;

public class TouchImageViewActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    private String fileName;
    private MyTouchImageView mtiv_bmp;
    private MyProgressBar mpb_load;
    private TextView tv_empty;
    private ImageLoader loader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fileName = getIntent().getStringExtra("filename");
        setContentView(R.layout.activity_showbitphoto);
        mtiv_bmp = (MyTouchImageView) this.findViewById(R.id.mtiv);
        mpb_load = (MyProgressBar) this.findViewById(R.id.mpb_load);
        tv_empty = (TextView) this.findViewById(R.id.tv_empty);

        loader = new ImageLoader(getApplicationContext());

    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread() {
            @Override
            public void run() {
                loader.loadImage(fileName, NetDataOperator.IMG_TYPE.TYPE_PREVIEW, new NetDataOperator.OnImageLoadingListener() {
                    @Override
                    public void onFailure(String error) {
                        mpb_load.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(),error,Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Bitmap bmp) {
                        mtiv_bmp.setImageBitmap(bmp);
                        mpb_load.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingProgressChanged(int percent) {
                        mpb_load.setPercentSlow(percent);
                    }
                });
            }
        }.start();
    }
}
