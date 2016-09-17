package edu.ncu.safe.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import edu.ncu.safe.R;
import edu.ncu.safe.customerview.MyProgressBar;
import edu.ncu.safe.customerview.MyTouchImageView;
import edu.ncu.safe.engine.DataLoader;
import edu.ncu.safe.util.BitmapUtil;

public class TouchImageViewActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    private String fileName;
    private MyTouchImageView mtiv_bmp;
    private MyProgressBar mpb_load;
    private TextView tv_empty;
    private DataLoader loader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fileName = getIntent().getStringExtra("filename");
        setContentView(R.layout.activity_showbitphoto);
        mtiv_bmp = (MyTouchImageView) this.findViewById(R.id.mtiv);
        mpb_load = (MyProgressBar) this.findViewById(R.id.mpb_load);
        tv_empty = (TextView) this.findViewById(R.id.tv_empty);

    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread() {
            @Override
            public void run() {
                BitmapUtil.loadImageToImageView(getApplicationContext(), fileName, DataLoader.TYPE_MIDDLE, mtiv_bmp, mpb_load);
            }
        }.start();
    }
}
