package edu.ncu.safe.ui;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.ncu.safe.R;
import edu.ncu.safe.adapter.FlowsStatisticVPAdapter;
import edu.ncu.safe.db.dao.FlowsDatabase;
import edu.ncu.safe.myadapter.MyAppCompatActivity;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

public class FlowsStatisticsActivity extends MyAppCompatActivity implements
		OnClickListener {
	private TextView tv_flowsDayStatistics;
	private TextView tv_flowsAppStatistics;
	private ViewPager vp_flowsStatistics;
	private LinearLayout ll_rightTip;
	private LinearLayout ll_leftTip;

	private LineChartView chartView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_flowsstatistics);
		initToolBar(getResources().getString(R.string.title_flows_statistic));
		tv_flowsDayStatistics = (TextView) this
				.findViewById(R.id.tv_dayflowsstatistics);
		tv_flowsAppStatistics = (TextView) this
				.findViewById(R.id.tv_appflowsstatistics);
		vp_flowsStatistics = (ViewPager) this
				.findViewById(R.id.vp_flowsstatistics);
		ll_rightTip = (LinearLayout) this.findViewById(R.id.ll_righttip);
		ll_leftTip = (LinearLayout) this.findViewById(R.id.ll_lefttip);
		chartView = (LineChartView) this.findViewById(R.id.chart);

		tv_flowsDayStatistics.setOnClickListener(this);
		tv_flowsAppStatistics.setOnClickListener(this);

		vp_flowsStatistics.setOnPageChangeListener(new MyPageChangeListener());
		vp_flowsStatistics.setAdapter(new FlowsStatisticVPAdapter(
				getSupportFragmentManager()));

		initChars();
	}

	private void initChars() {
		List<PointValue> mPointValues = new ArrayList<PointValue>();
		List<AxisValue> mAxisValues = new ArrayList<AxisValue>();

		FlowsDatabase db = new FlowsDatabase(this);
		float[] flows = db.queryCurrentMonthByDayFlows();

		for (int i = 1; i < flows.length + 1; i++) {
			mPointValues.add(new PointValue(i, flows[i - 1]));
			mAxisValues.add(new AxisValue(i).setLabel(i + "日"));
		}

		Line line = new Line(mPointValues).setColor(Color.BLUE).setCubic(true);
		List<Line> lines = new ArrayList<Line>();
		lines.add(line);
		LineChartData data = new LineChartData();
		data.setLines(lines);
		
		Axis axisX = new Axis();
		axisX.setHasTiltedLabels(true);
		axisX.setTextColor(Color.BLUE);
		axisX.setMaxLabelChars(10);
		axisX.setValues(mAxisValues);
		data.setAxisXBottom(axisX);

		Axis axisY = new Axis();
		axisY.setTextColor(Color.RED);
		axisY.setHasTiltedLabels(true);
		axisY.setName("本月使用流量（MB）");
		axisY.setMaxLabelChars(2);
		data.setAxisYLeft(axisY);

		chartView.setInteractive(true);
		chartView.setZoomType(ZoomType.HORIZONTAL);
		chartView.setContainerScrollEnabled(true,
				ContainerScrollType.HORIZONTAL);
		chartView.setLineChartData(data);
		chartView.setVisibility(View.VISIBLE);
		
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.tv_dayflowsstatistics:
			vp_flowsStatistics.setCurrentItem(0);
			break;
		case R.id.tv_appflowsstatistics:
			vp_flowsStatistics.setCurrentItem(1);
			break;
		}
	}

	class MyPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int item) {
			AlphaAnimation disappear = new AlphaAnimation(1.0f, 0.0f);
			AlphaAnimation appear = new AlphaAnimation(0.0f, 1.0f);
			disappear.setDuration(1000);
			appear.setDuration(1000);

			if (item == 0) {
				ll_rightTip.setVisibility(View.INVISIBLE);
				ll_rightTip.startAnimation(disappear);

				ll_leftTip.setVisibility(View.VISIBLE);
				ll_leftTip.startAnimation(appear);
			} else {
				ll_rightTip.setVisibility(View.VISIBLE);
				ll_rightTip.startAnimation(appear);

				ll_leftTip.setVisibility(View.INVISIBLE);
				ll_leftTip.startAnimation(disappear);
			}

			ColorStateList dayColor = tv_flowsDayStatistics.getTextColors();
			ColorStateList appColor = tv_flowsAppStatistics.getTextColors();
			tv_flowsDayStatistics.setTextColor(appColor);
			tv_flowsAppStatistics.setTextColor(dayColor);
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
}
