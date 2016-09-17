package edu.ncu.safe.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import edu.ncu.safe.R;
import edu.ncu.safe.adapter.CommunicationVPAdatpter;

public class CommunicationProtectorActivity extends MyAppCompatActivity implements OnClickListener{
	private ImageView iv_showPopup;
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
		initToolBar(getResources().getString(R.string.title_communication_protector));
		iv_showPopup = (ImageView) this.findViewById(R.id.iv_showpopup);
		rl_msg = (RelativeLayout) this.findViewById(R.id.rl_msg);
		rl_phone = (RelativeLayout) this.findViewById(R.id.rl_phone);
		vp_protectRecorder = (ViewPager) this.findViewById(R.id.vp_protectrecorder);

		iv_showPopup.setOnClickListener(this);
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
		case R.id.iv_showpopup:
			showPopup();
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

	private void showPopup() {
		View contentView = LayoutInflater.from(this).inflate(R.layout.popupwindow_commore, null);
		LinearLayout ll_backList = (LinearLayout) contentView.findViewById(R.id.ll_blackwhitelist);
		LinearLayout ll_interceptonSetting = (LinearLayout) contentView.findViewById(R.id.ll_interceptionsetting);
		LinearLayout ll_phoneNumberPlace = (LinearLayout) contentView.findViewById(R.id.ll_phonenumberplace);

		ll_backList.setOnClickListener(this);
		ll_interceptonSetting.setOnClickListener(this);
		ll_phoneNumberPlace.setOnClickListener(this);
		contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
		popupWindow = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		//popupWindow设置animation一定要在show之前
		popupWindow.setAnimationStyle(R.style.popupanimation);

		popupWindow.setTouchable(true);
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		//一定要设置背景，否则无法自动消失
		Drawable background = getResources().getDrawable(
                R.drawable.popupbgtop);
		popupWindow.setBackgroundDrawable(background);
		iv_showPopup.measure(0,0);
		popupWindow.showAsDropDown(iv_showPopup, iv_showPopup.getMeasuredWidth() - contentView.getMeasuredWidth()+10, 0);
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

}
