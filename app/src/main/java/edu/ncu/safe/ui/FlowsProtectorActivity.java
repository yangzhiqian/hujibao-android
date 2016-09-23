package edu.ncu.safe.ui;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import edu.ncu.safe.R;
import edu.ncu.safe.customerview.MyProgressBar;
import edu.ncu.safe.mvp.presenter.FlowsProtectorPresenter;
import edu.ncu.safe.mvp.view.FlowsProtectorMvpView;
import edu.ncu.safe.util.FlowsFormartUtil;

public class FlowsProtectorActivity extends MyAppCompatActivity implements OnClickListener ,FlowsProtectorMvpView{
    private MyProgressBar myProgressBar;
    private TextView tv_month;
    private TextView tv_day;
    private LinearLayout ll_calibration;
    private LinearLayout ll_flows;

    private FlowsProtectorPresenter presenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flowsmanager);
        initToolBar(getResources().getString(R.string.title_flows_protector));
        initViews();
        presenter = new FlowsProtectorPresenter(this,getApplicationContext());
        presenter.init();
    }

    private void initViews(){
        ll_calibration = (LinearLayout) this.findViewById(R.id.ll_calibration);
        ll_flows = (LinearLayout) this.findViewById(R.id.ll_flows);
        tv_month = (TextView) this.findViewById(R.id.tv_currentmonth);
        tv_day = (TextView) this.findViewById(R.id.tv_currentday);
        myProgressBar = (MyProgressBar) this.findViewById(R.id.mpb_flows);

        ll_calibration.setOnClickListener(this);
        ll_flows.setOnClickListener(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        presenter.refresh();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_calibration:
                toAntherAvitvity(FlowsCalibrationActicity.class);
                break;
            case R.id.ll_flows:
                toAntherAvitvity(FlowsStatisticsActivity.class);
                break;
        }
    }

    @Override
    public void onCurrentMonthFlowsGet(long flows) {
        tv_month.setText(FlowsFormartUtil.toMBFormat(flows));
    }

    @Override
    public void onCurrentDayFlowsGet(long flows) {
        tv_day.setText(FlowsFormartUtil.toMBFormat(flows));
    }

    @Override
    public void onFlowsPercentGet(float percent) {
        myProgressBar.setPercentSlow(percent);
    }
}
