package edu.ncu.safe.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import edu.ncu.safe.MyApplication;
import edu.ncu.safe.R;
import edu.ncu.safe.View.MyProgressBar;
import edu.ncu.safe.db.dao.FlowsDatabase;
import edu.ncu.safe.myadapter.MyAppCompatActivity;
import edu.ncu.safe.util.FlowsFormartUtil;

public class FlowsProtectorActivity extends MyAppCompatActivity implements OnClickListener {
    private MyProgressBar myProgressBar;
    private FlowsDatabase database;
    private TextView tv_month;

    private TextView tv_day;
    private LinearLayout ll_calibration;
    private LinearLayout ll_flows;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flowsmanager);
        initToolBar(getResources().getString(R.string.title_flows_protector));
        ll_calibration = (LinearLayout) this.findViewById(R.id.calibration);
        ll_flows = (LinearLayout) this.findViewById(R.id.flows);
        tv_month = (TextView) this.findViewById(R.id.tv_currentmonth);
        tv_day = (TextView) this.findViewById(R.id.tv_currentday);
        myProgressBar = (MyProgressBar) this.findViewById(R.id.mpb_flows);

        database = new FlowsDatabase(this);

        ll_calibration.setOnClickListener(this);
        ll_flows.setOnClickListener(this);
    }
    @Override
    protected void onStart() {
        super.onStart();
        initViewData();
    }

    private void initViewData() {
        SharedPreferences sp = MyApplication.getSharedPreferences();
        long dbFlowsOffset = sp.getLong(MyApplication.SP_LONG_DB_OFFSET, 0);
        long monthFlows = database.queryCurrentMonthTotalFlows()
                + dbFlowsOffset;
        long dayFlows = database.queryCurrentDayTotalFlows();
        monthFlows = monthFlows <= 0 ? 0 : monthFlows;
        dayFlows = dayFlows <= 0 ? 0 : dayFlows;
        tv_month.setText(FlowsFormartUtil.toMBFormat(monthFlows));
        tv_day.setText(FlowsFormartUtil.toMBFormat(dayFlows));
        long total = sp.getLong(MyApplication.SP_LONG_TOTAL_FLOWS,0);
        if(total>0) {
            myProgressBar.setPercentSlow((int) (monthFlows * 100 / total));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.calibration:
                toAntherAvitvity(FlowsCalibrationActicity.class);
                break;
            case R.id.flows:
                toAntherAvitvity(FlowsStatisticsActivity.class);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            this.finish();
            overridePendingTransition(R.anim.activit3dtoright_in, R.anim.activit3dtoright_out);
        }
        return super.onKeyDown(keyCode, event);
    }

    private void toAntherAvitvity(Class clazz) {
        Intent intent = new Intent();
        intent.setClass(this, clazz);
        startActivity(intent);
        overridePendingTransition(R.anim.activit3dtoleft_in, R.anim.activit3dtoleft_out);
    }
}
