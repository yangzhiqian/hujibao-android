package edu.ncu.safe.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import edu.ncu.safe.R;
import edu.ncu.safe.adapter.CommunicationVPAdatpter;

public class CommunicationProtectorActivity extends FragmentActivity implements OnClickListener{
	private ImageView iv_back;
	private TextView tv_more;
	private RelativeLayout rl_msg;
	private RelativeLayout rl_phone;
	private ViewPager vp_protectRecorder;
	private PopupWindow popupWindow;
	private Drawable bg_selected;
	private Drawable bg_unselected;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_communicationprotector);
		
		iv_back = (ImageView) this.findViewById(R.id.back);
		tv_more = (TextView) this.findViewById(R.id.tv_more);
		rl_msg = (RelativeLayout) this.findViewById(R.id.rl_msg);
		rl_phone = (RelativeLayout) this.findViewById(R.id.rl_phone);
		vp_protectRecorder = (ViewPager) this.findViewById(R.id.vp_protectrecorder);
		
		iv_back.setOnClickListener(this);
		tv_more.setOnClickListener(this);
		rl_msg.setOnClickListener(this);
		rl_phone.setOnClickListener(this);
		
		vp_protectRecorder.setAdapter(new CommunicationVPAdatpter(getSupportFragmentManager()));
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
		case R.id.tv_more:
			View contentView = LayoutInflater.from(this).inflate(R.layout.popupwindow_commore, null);
			LinearLayout ll_backList = (LinearLayout) contentView.findViewById(R.id.ll_blackwhitelist);
			LinearLayout ll_interceptonSetting = (LinearLayout) contentView.findViewById(R.id.ll_interceptionsetting);
			LinearLayout ll_phoneNumberPlace = (LinearLayout) contentView.findViewById(R.id.ll_phonenumberplace);
			
			ll_backList.setOnClickListener(this);
			ll_interceptonSetting.setOnClickListener(this);
			ll_phoneNumberPlace.setOnClickListener(this);
			
			popupWindow = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			//popupWindow设置animation一定要在show之前
			popupWindow.setAnimationStyle(R.style.popupanimation);
			
			popupWindow.setTouchable(true);  
			popupWindow.setFocusable(true);  
			popupWindow.setOutsideTouchable(true);  
			//一定要设置背景，否则无法自动消失
			popupWindow.setBackgroundDrawable(new PaintDrawable());  
			popupWindow.showAsDropDown(tv_more,tv_more.getWidth()-contentView.getWidth()-100,30);
		
			break;
		case R.id.rl_msg:
			vp_protectRecorder.setCurrentItem(0);
			break;
		case R.id.rl_phone:
			vp_protectRecorder.setCurrentItem(1);
			break;
		case R.id.ll_blackwhitelist:
			toAntherAvitvity(CommunicationWhiteBlackSetActivity.class);
			break;
		case R.id.ll_interceptionsetting:
			toAntherAvitvity(CommunicationInterceptionSet.class);
			break;
		case R.id.ll_phonenumberplace:
			toAntherAvitvity(CommunicationNumberQuery.class);
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

	private void toAntherAvitvity(Class clazz){
		Intent intent = new Intent();
		intent.setClass(this, clazz);
		startActivity(intent);
		overridePendingTransition(R.anim.activit3dtoleft_in, R.anim.activit3dtoleft_out);

		if(popupWindow!=null&&popupWindow.isShowing()){
			popupWindow.dismiss();
		}
	}
}
