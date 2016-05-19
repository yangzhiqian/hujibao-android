package edu.ncu.safe.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import edu.ncu.safe.R;
import edu.ncu.safe.adapter.CommunicationWhiteBlackSetVPAdatpter;

public class CommunicationWhiteBlackSetActivity extends FragmentActivity implements OnClickListener{
	private ImageView iv_back;
	private RelativeLayout rl_msg;
	private RelativeLayout rl_phone;
	private ViewPager vp_protectRecorder;
	
	private Drawable bg_selected;
	private Drawable bg_unselected;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_communicationwhiteblackset);
		
		iv_back = (ImageView) this.findViewById(R.id.back);
		rl_msg = (RelativeLayout) this.findViewById(R.id.rl_msg);
		rl_phone = (RelativeLayout) this.findViewById(R.id.rl_phone);
		vp_protectRecorder = (ViewPager) this.findViewById(R.id.vp_protectrecorder);
		
		iv_back.setOnClickListener(this);
		rl_msg.setOnClickListener(this);
		rl_phone.setOnClickListener(this);
		
		vp_protectRecorder.setAdapter(new CommunicationWhiteBlackSetVPAdatpter(getSupportFragmentManager()));
		vp_protectRecorder.setOnPageChangeListener(new MyOnPagerChangeListener());
		
		
		bg_selected = rl_msg.getBackground();
		bg_unselected = rl_phone.getBackground();
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.back:
			this.finish();
			overridePendingTransition(R.anim.activit3dtoright_in, R.anim.activit3dtoright_out);
			break;
		case R.id.rl_msg:
			vp_protectRecorder.setCurrentItem(0);
			break;
		case R.id.rl_phone:
			vp_protectRecorder.setCurrentItem(1);
			break;
		}
	}
	
	class MyOnPagerChangeListener implements OnPageChangeListener{
		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}
		@Override
		public void onPageSelected(int arg0) {
			if(arg0 == 0){
				//msg
				rl_msg.setBackgroundDrawable(bg_selected);
				rl_phone.setBackgroundDrawable(bg_unselected);
			}
			else{
				//phone
				rl_msg.setBackgroundDrawable(bg_unselected);
				rl_phone.setBackgroundDrawable(bg_selected);
			}
			
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(KeyEvent.KEYCODE_BACK == keyCode){
			this.finish();
			overridePendingTransition(R.anim.activit3dtoright_in, R.anim.activit3dtoright_out);
		}
		return super.onKeyDown(keyCode, event);
	}
}
