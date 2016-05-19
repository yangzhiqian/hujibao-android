package edu.ncu.safe.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RadioGroup;

import edu.ncu.safe.R;
import edu.ncu.safe.adapter.GuideVPAdapter;

public class GuideActivity extends FragmentActivity implements OnTouchListener, ViewPager.OnPageChangeListener {

	private static final String TAG = "GuideActivity";
	ViewPager pager;
	RadioGroup rg;
	SharedPreferences sp ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		
		pager = (ViewPager) this.findViewById(R.id.guide_vp);
		rg = (RadioGroup) this.findViewById(R.id.guide_rg);
		sp = this.getSharedPreferences("conf", Context.MODE_PRIVATE);
		
		pager.setAdapter(new GuideVPAdapter(getSupportFragmentManager()));
		pager.setOnPageChangeListener(this);
		//添加触屏监听，用来解决滑动到最后一个引导页后再像←滑动进入主界面
		pager.setOnTouchListener(this);
	}
	@Override
	public void onPageScrollStateChanged(int arg0) {
	}
	
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}
	@Override
	public void onPageSelected(int arg0) {
		current = arg0;
		rg.check(R.id.pg1+arg0);
	}
	
	int current = 0;
	float xstart = 0;
	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
//		Log.i(TAG, arg1.getAction()+"          "+arg1.getX()+"");
		if(current!=3){
			return false;
		}
		switch(arg1.getAction()){
		case 0://按下
			xstart = arg1.getX();
			break;
		case 2://拖动：
			break;
		case 1://抬起
			float xend = arg1.getX();
			if((xstart-xend)>200){
				//更新偏好文件里的配置信息,将第一次进入软件设置为false
				updateConf();
				//进入主界面
				Intent intent = new Intent(this,MainActivity.class);
				startActivity(intent);
				this.finish();
			}
			xstart = xend;
			break;
		}
		return false;
	}
	
	private void updateConf(){
		Editor editor = sp.edit();
		editor.putBoolean("isfirstuseapp", false);
		editor.commit();
	}
}
