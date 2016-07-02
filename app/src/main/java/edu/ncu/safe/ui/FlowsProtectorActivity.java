package edu.ncu.safe.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import edu.ncu.safe.R;
import edu.ncu.safe.View.MyProgressBar;
import edu.ncu.safe.db.dao.FlowsDatabase;
import edu.ncu.safe.util.FlowsFormartUtil;
import edu.ncu.safe.util.FormatDate;

public class FlowsProtectorActivity extends Activity implements OnClickListener {

    public static final String FLOWSSHAREDPREFERENCES = "flowsconfig";// sharedpreferences名字
    public static final String FLOWSTOTAL = "flowstotal";// 用户输入的当月总流量
    public static final String DBFLOWSOFFSET = "dbflowsoffset";// 用户输入后用来矫正数据库的偏差
    // 该值 = 正确的数 -
    // 数据库记录的数（每个月月初对该值进行清零）
    public static final String DBFLOWSOFFSETUPDATETIME = "dbflowsoffsetupdatetime";// dbflowsoffset跟新的时间

    private MyProgressBar myProgressBar;
    private FlowsDatabase database;
    private TextView tv_month;

    private TextView tv_day;
    private LinearLayout ll_calibration;
    private LinearLayout ll_flows;
    private ImageView iv_back;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flowsmanager);

        iv_back = (ImageView) this.findViewById(R.id.back);
        ll_calibration = (LinearLayout) this.findViewById(R.id.calibration);
        ll_flows = (LinearLayout) this.findViewById(R.id.flows);
        tv_month = (TextView) this.findViewById(R.id.tv_currentmonth);
        tv_day = (TextView) this.findViewById(R.id.tv_currentday);
        myProgressBar = (MyProgressBar) this.findViewById(R.id.mpb_flows);

        database = new FlowsDatabase(this);
        sp = this.getSharedPreferences(FLOWSSHAREDPREFERENCES,
                Context.MODE_MULTI_PROCESS);

        iv_back.setOnClickListener(this);
        ll_calibration.setOnClickListener(this);
        ll_flows.setOnClickListener(this);
    }
    @Override
    protected void onStart() {
        super.onStart();
        initViewData();
    }

    private void initViewData() {
        long dbFlowsOffset = sp.getLong(DBFLOWSOFFSET, 0);
        int offsetUpdateDate = sp.getInt(DBFLOWSOFFSETUPDATETIME, 0);
        int currentDate = FormatDate.getCurrentFormatIntDate();
        if (currentDate / 100 != offsetUpdateDate / 100) {
            // offset时间和当月时间不同
            dbFlowsOffset = 0;
            // 更新配置文件
            Editor editor = sp.edit();
            editor.putLong("DBFLOWSOFFSET", 0);// 初始化
            editor.putInt("DBFLOWSOFFSETUPDATETIME", currentDate);// 初始化
            editor.apply();
        }

        long monthFlows = database.queryCurrentMonthTotalFlows()
                + dbFlowsOffset;
        long dayFlows = database.queryCurrentDayTotalFlows();

        monthFlows = monthFlows <= 0 ? 0 : monthFlows;
        dayFlows = dayFlows <= 0 ? 0 : dayFlows;

        tv_month.setText(FlowsFormartUtil.toMBFormat(monthFlows));
        tv_day.setText(FlowsFormartUtil.toMBFormat(dayFlows));
        long total = sp.getLong(FLOWSTOTAL,0);
        if(total>0) {
            myProgressBar.setPercentSlow((int) (monthFlows * 100 / total));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                this.finish();
                overridePendingTransition(R.anim.activit3dtoright_in, R.anim.activit3dtoright_out);
                break;
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
