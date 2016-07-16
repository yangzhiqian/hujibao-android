package edu.ncu.safe.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RadioGroup;

import edu.ncu.safe.MyApplication;
import edu.ncu.safe.R;
import edu.ncu.safe.adapter.GuideVPAdapter;
import edu.ncu.safe.util.DensityUtil;

public class GuideActivity extends FragmentActivity implements OnTouchListener, ViewPager.OnPageChangeListener {

	private ViewPager pager;
	private RadioGroup rg;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		
		pager = (ViewPager) this.findViewById(R.id.guide_vp);
		rg = (RadioGroup) this.findViewById(R.id.guide_rg);

		pager.setAdapter(new GuideVPAdapter(getSupportFragmentManager()));
		pager.setOnPageChangeListener(this);
		//添加触屏监听，用来解决滑动到最后一个引导页后再像←滑动进入主界面
		pager.setOnTouchListener(this);
	}
	int current = 0;
	float xstart = 0;
	@Override
	public boolean onTouch(View view, MotionEvent event) {
		if(current==pager.getAdapter().getCount()-1){
			switch(event.getAction()){
				case 0://按下
					xstart = event.getX();
					break;
				case 1://抬起
					float xend = event.getX();
					if((xstart-xend)>getEnterSize()){
						//更新偏好文件里的配置信息,将第一次进入软件设置为false
						SharedPreferences.Editor edit = MyApplication.getSharedPreferences().edit();
						edit.putBoolean(MyApplication.SP_BOOLEAN_IS_FIRST_RNTER_APP,false);
						edit.apply();
						//进入主界面
						Intent intent = new Intent(this,MainActivity.class);
						startActivity(intent);
						this.finish();
					}
					xstart = xend;
					break;
			}
		}
		return false;
	}

	private float getEnterSize() {
		return DensityUtil.getScreenWidth(this)/3;
	}

	@Override
	public void onPageSelected(int position) {
		current = position;
		rg.check(R.id.pg1 + position);
	}
	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
	}
	@Override
	public void onPageScrollStateChanged(int state) {
	}
}
