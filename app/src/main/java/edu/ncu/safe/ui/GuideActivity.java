package edu.ncu.safe.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.ncu.safe.MyApplication;
import edu.ncu.safe.R;
import edu.ncu.safe.util.DensityUtil;
import github.chenupt.springindicator.SpringIndicator;

public class GuideActivity extends FragmentActivity implements OnTouchListener {
	private static final String[] texts = {"源码公开","https://github.com/yangzhiqian/safe","欢迎使用"};
	private static final int[] colors={Color.parseColor("#a8d9f5"),Color.parseColor("#73b678"),Color.parseColor("#e17500")};
	private ViewPager pager;
	private float xstart = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);

		pager = (ViewPager) this.findViewById(R.id.guide_vp);
		SpringIndicator indicator = (SpringIndicator) this.findViewById(R.id.si);

		pager.setPageTransformer(true, new DepthPageTransformer());
		pager.setAdapter(new GuidePagerAdapter(getDatas()));
		//添加触屏监听，用来解决滑动到最后一个引导页后再像←滑动进入主界面
		pager.setOnTouchListener(this);
		indicator.setViewPager(pager);
	}

	private  List<View> getDatas() {
		List<View> datas = new ArrayList<View>();
		for(int i=0;i<texts.length;i++){
			View view = View.inflate(this, R.layout.content_guide, null);
			TextView text = (TextView)view.findViewById(R.id.tv_text);
			text.setText(texts[i]);
			if(i==1){
				text.setAutoLinkMask(Linkify.ALL);
				text.setMovementMethod(LinkMovementMethod.getInstance());
			}
			view.setBackgroundColor(colors[i]);
			datas.add(view);
		}
		return datas;
	}
	@Override
	public boolean onTouch(View view, MotionEvent event) {
		if(pager.getCurrentItem() ==pager.getAdapter().getCount()-1){
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
		return DensityUtil.getScreenWidth(this)/4;
	}

	class GuidePagerAdapter extends PagerAdapter {
		private List<View> views;
		public GuidePagerAdapter(List<View> views){
			this.views = views;
		}
		@Override
		public int getCount() {
			return views.size();
		}
		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view==object;
		}
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(views.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(views.get(position));
			return views.get(position);
		}
	}
}



class DepthPageTransformer implements ViewPager.PageTransformer {
	private static final float MIN_SCALE = 0.75f;

	public void transformPage(View view, float position) {
		int pageWidth = view.getWidth();
		if (position < -1) { // [-Infinity,-1)
			// This page is way off-screen to the left.
			view.setAlpha(0);
		} else if (position <= 0) { // [-1,0]
			// Use the default slide transition when moving to the left page
			view.setAlpha(1);
			view.setTranslationX(0);
			view.setScaleX(1);
			view.setScaleY(1);

		} else if (position <= 1) { // (0,1]
			// Fade the page out.
			view.setAlpha(1 - position);

			// Counteract the default slide transition
			view.setTranslationX(pageWidth * -position);

			// Scale the page down (between MIN_SCALE and 1)
			float scaleFactor = MIN_SCALE
					+ (1 - MIN_SCALE) * (1 - Math.abs(position));
			view.setScaleX(scaleFactor);
			view.setScaleY(scaleFactor);

		} else { // (1,+Infinity]
			// This page is way off-screen to the right.
			view.setAlpha(0);
		}
	}
}