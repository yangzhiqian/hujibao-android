package edu.ncu.safe.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.ncu.safe.R;
import edu.ncu.safe.adapter.SimpleFragmentPagerAdapter;
import edu.ncu.safe.db.dao.FlowsDatabase;
import edu.ncu.safe.ui.fragment.FlowsAppLVFragment;
import edu.ncu.safe.ui.fragment.FlowsDayLVFragment;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

public class FlowsStatisticsActivity extends MyAppCompatActivity  {
    private LineChartView chartView;
    private TabLayout tl_flowsstatistics;
    private ViewPager vp_flowsStatistics;
    private SimpleFragmentPagerAdapter adapter;
    private TextView tv_gprs;
    private TextView tv_gprswifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flowsstatistics);
        initToolBar(getResources().getString(R.string.title_flows_statistic));
        initViews();

        //初始化vp内容
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new FlowsDayLVFragment());
        fragments.add(new FlowsAppLVFragment());
        adapter = new SimpleFragmentPagerAdapter(
                this,
                getSupportFragmentManager(),
                fragments,
                new String[]{getString(R.string.flows_protector_detail_day_flows),getString(R.string.flows_protector_detail_app_flows)});
        vp_flowsStatistics.setAdapter(adapter);

        tl_flowsstatistics.setupWithViewPager(vp_flowsStatistics);
        tl_flowsstatistics.setTabMode(TabLayout.MODE_FIXED);

        vp_flowsStatistics.addOnPageChangeListener(new MyPageChangeListener());

    }

    private void initViews() {
        chartView = (LineChartView) this.findViewById(R.id.chart);
        tl_flowsstatistics = (TabLayout) findViewById(R.id.tl_flowsstatistics);
        vp_flowsStatistics = (ViewPager) this
                .findViewById(R.id.vp_flowsstatistics);
        tv_gprs = (TextView) findViewById(R.id.tv_gprs);
        tv_gprswifi = (TextView) findViewById(R.id.tv_gprswifi);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initChars();
    }

    /**
     * 加载图标的数据并显示
     */
    private void initChars() {
        LineChartData data = new LineChartData();//图标的总数据
        List<PointValue> mPointValues = new ArrayList<PointValue>();//点值
        List<AxisValue> mAxisValues = new ArrayList<AxisValue>();//坐标值

        //加载流量数据
        FlowsDatabase db = new FlowsDatabase(this);
        float[] flows = db.queryCurrentMonthByDayFlows();
        for (int i = 1; i < flows.length + 1; i++) {
            mPointValues.add(new PointValue(i, flows[i - 1]));
            mAxisValues.add(new AxisValue(i).setLabel(i + "日"));
        }

        /**
         * 设置线条数据
         */
        Line line = new Line(mPointValues).setColor(Color.YELLOW).setCubic(true);
        List<Line> lines = new ArrayList<Line>();
        lines.add(line);
        data.setLines(lines);

        //x轴
        Axis axisX = new Axis();
        axisX.setHasTiltedLabels(false);
        axisX.setTextColor(Color.YELLOW);
        axisX.setMaxLabelChars(4);
        axisX.setValues(mAxisValues);
        data.setAxisXBottom(axisX);
        //y轴
        Axis axisY = new Axis();
        axisY.setTextColor(Color.YELLOW);
        axisY.setHasTiltedLabels(true);
        axisY.setName("本月使用流量（MB）");
        axisY.setMaxLabelChars(3);
        data.setAxisYLeft(axisY);

        chartView.setInteractive(true);
        chartView.setZoomType(ZoomType.HORIZONTAL);
        chartView.setContainerScrollEnabled(true,
                ContainerScrollType.HORIZONTAL);
        chartView.setLineChartData(data);
    }

    class MyPageChangeListener implements OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            if(arg1==0 && arg2==0){
                tv_gprs.setAlpha(arg0==0?1:0);
                tv_gprswifi.setAlpha(arg0);
            }else{
                tv_gprs.setAlpha(1-arg1);
                tv_gprswifi.setAlpha(arg1);
            }
        }

        @Override
        public void onPageSelected(int item) {
        }
    }
}
