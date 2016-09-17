package edu.ncu.safe.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import edu.ncu.safe.MyApplication;
import edu.ncu.safe.R;

public class WelcomeActivity extends Activity {
	private static final int BIG_TEXT_APPEAR = 1;
	private static final int TO_NEXT_ACTIVITY = 2;

	private TextView tv_bigName;
	private Thread taskDispatcherThread ;
	private Handler handler = new MyHandler(this);

	private static class MyHandler extends Handler{
		private WeakReference<Context> reference;
		public MyHandler(Context context) {
			reference = new WeakReference<>(context);
		}
		public void handleMessage(android.os.Message msg) {
			WelcomeActivity activity = (WelcomeActivity) reference.get();
			if(activity!=null){
				switch (msg.what){
					case BIG_TEXT_APPEAR:
						AlphaAnimation ani = new AlphaAnimation(0.1f, 1.0f);
						ani.setDuration(1000);
						activity.tv_bigName.setVisibility(View.VISIBLE);
						activity.tv_bigName.startAnimation(ani);
						break;
					case TO_NEXT_ACTIVITY:
						activity.toNextActivity();
						break;
				}
			}
		};
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//解决按下home键在回到原先界面的问题
		if (!isTaskRoot()) {
			finish();
			return;
		}
		setContentView(R.layout.activity_welecome);
		tv_bigName = (TextView) this.findViewById(R.id.tv_bigname);
		// 开启程序所需的广播
		sendBroadcast(new Intent(getResources().getString(R.string.action_relife)));
	}

	@Override
	protected void onStart() {
		super.onStart();
		taskDispatcherThread = new Thread(){
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
					handler.sendEmptyMessage(BIG_TEXT_APPEAR);
					Thread.sleep(2000);
					handler.sendEmptyMessage(TO_NEXT_ACTIVITY);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		taskDispatcherThread.start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		handler.removeCallbacksAndMessages(null);
		if(taskDispatcherThread!=null) {
			taskDispatcherThread.interrupt();
		}
		handler = null;
	}

	/**
	 * 判断是否是首次进入app，如果是，则进入引导界面，否则进入主界面
	 */
	private void toNextActivity() {
		startActivity(nextIntent());
		this.finish();
	}

	private Intent nextIntent(){
		if (isFirstUseApp()) {
			return new Intent(this, ProtocolActivity.class);
		}else{
			return new Intent(this, MainActivity.class);
		}
	}
	private boolean isFirstUseApp() {
		return MyApplication.getSharedPreferences().getBoolean(MyApplication.SP_BOOLEAN_IS_FIRST_RNTER_APP, true);
	}
}
