package edu.ncu.safe.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import edu.ncu.safe.R;

public class WelcomeActivity extends Activity {

	private static final String TAG = "WelcomeActivity";
	private SharedPreferences sp;

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			AlphaAnimation ani = new AlphaAnimation(0.1f, 1.0f);
			ani.setDuration(1500);
			tv_bigName.setVisibility(View.VISIBLE);
			tv_bigName.startAnimation(ani);

		};
	};

	TextView tv_bigName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//解决按下home键在回到原先界面的问题
		if (!isTaskRoot()) {
			finish();
			return;
		}

		setContentView(R.layout.activity_welecome);
		// 开启程序所需的广播
		sendBroadcast(new Intent("edu.ncu.safe.reveiver.relefe"));

		tv_bigName = (TextView) this.findViewById(R.id.tv_bigname);
		// 获取SharedPreferences
		sp = this.getSharedPreferences("conf", Context.MODE_PRIVATE);
		handler.post(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				handler.sendEmptyMessage(0);
			}
		});
		// 两秒后进入下一个activity
		new Thread() {
			public void run() {
				try {
					Thread.sleep(6000);
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
			Log.i(TAG, "用户首次进入app,进入引导界面");
			Intent intent = new Intent(this, ProtocolActivity.class);
			startActivity(intent);
			this.finish();
		} else {
			// 进入mainsrceen界面
			Log.i(TAG, "直接进入mainsrceen界面");
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			this.finish();
		}
	}

	private boolean isFirstUseApp() {
		return sp.getBoolean("isfirstuseapp", true);
	}
}
