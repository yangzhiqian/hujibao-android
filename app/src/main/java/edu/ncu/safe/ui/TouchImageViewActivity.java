package edu.ncu.safe.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import edu.ncu.safe.R;
import edu.ncu.safe.View.MyProgressBar;
import edu.ncu.safe.View.MyTouchImageView;
import edu.ncu.safe.engine.DataLoader;
import edu.ncu.safe.util.BitmapUtil;

public class TouchImageViewActivity extends Activity{
    /**
     * Called when the activity is first created.
     */
    private String fileName;
    private String token;
    private MyTouchImageView mtiv_bmp;
    private MyProgressBar mpb_load;
    private TextView tv_empty;
    private DataLoader loader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fileName = getIntent().getStringExtra("filename");
        token = getIntent().getStringExtra("token");
        setContentView(R.layout.activity_showbitphoto);
        mtiv_bmp = (MyTouchImageView) this.findViewById(R.id.mtiv);
        mpb_load = (MyProgressBar) this.findViewById(R.id.mpb_load);
        tv_empty = (TextView) this.findViewById(R.id.tv_empty);
        BitmapUtil.loadImageToImageView(this,token, fileName,1,mtiv_bmp,mpb_load);
    }
}
