package edu.ncu.safe.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import edu.ncu.safe.MyApplication;
import edu.ncu.safe.R;

public class WelcomeActivity extends Activity {
	TextView tv_bigName;
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			AlphaAnimation ani = new AlphaAnimation(0.1f, 1.0f);
			ani.setDuration(1500);
			tv_bigName.setVisibility(View.VISIBLE);
			tv_bigName.startAnimation(ani);
		};
	};


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
		// 开启程序所需的广播]
		sendBroadcast(new Intent(getResources().getString(R.string.action_relife)));
		// 获取SharedPreferences
		handler.post(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				handler.sendEmptyMessage(0);
			}
		});
		// 两秒后进入下一个activity
		new Thread() {
			public void run() {
				try {
					Thread.sleep(getResources().getInteger(R.integer.wellcom_time));
					toNextActivity();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	private void toNextActivity() {
		if (isFirstUseApp()) {
			// 用户首次进入app,进入引导界面
			Intent intent = new Intent(this, ProtocolActivity.class);
			startActivity(intent);
			this.finish();
		} else {
			// 进入mainsrceen界面
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			this.finish();
		}
	}

	private boolean isFirstUseApp() {
		return MyApplication.getSharedPreferences().getBoolean(MyApplication.SP_BOOLEAN_IS_FIRST_RNTER_APP, true);
	}
}
