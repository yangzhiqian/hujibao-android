package edu.ncu.safe.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import edu.ncu.safe.R;
import edu.ncu.safe.View.MyProgressBar;
import edu.ncu.safe.View.MyTouchImageView;
import edu.ncu.safe.domain.BackupInfo;
import edu.ncu.safe.util.BitmapUtil;

public class TouchImageViewActivity extends Activity {
    /** Called when the activity is first created. */
    private BackupInfo backupInfo;
    private MyTouchImageView mtiv_bmp;
    private MyProgressBar mpb_load;
    private TextView tv_empty;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        backupInfo = (BackupInfo) getIntent().getExtras().getSerializable("backupinfo");
        setContentView(R.layout.activity_showbitphoto);
        mtiv_bmp = (MyTouchImageView) this.findViewById(R.id.mtiv);
        mpb_load = (MyProgressBar) this.findViewById(R.id.mpb_load);
        tv_empty = (TextView) this.findViewById(R.id.tv_empty);
        mtiv_bmp.setImageBitmap(BitmapUtil.getRequireBitmap(backupInfo.getPic(), 300, 300));

        mpb_load.setVisibility(View.GONE);
    }
}
