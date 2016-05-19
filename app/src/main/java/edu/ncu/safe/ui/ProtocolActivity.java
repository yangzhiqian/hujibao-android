package edu.ncu.safe.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import edu.ncu.safe.R;
import edu.ncu.safe.engine.LoadProtocol;

public class ProtocolActivity extends Activity {
	public static final String TAG = "ProtocolActivity";
	TextView text;
	CheckBox cb;
	Button btn;
	SharedPreferences sp ;

	Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			text.setText((String)msg.obj);
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_protocol);
		text = (TextView) this.findViewById(R.id.protocol_tv);
		cb = (CheckBox) this.findViewById(R.id.protocol_cb);
		btn = (Button) this.findViewById(R.id.protocol_btn);
		sp = this.getSharedPreferences("conf", Context.MODE_PRIVATE);
		//在xml中android:singleLine="false"  android:scrollbars="vertical"设置
		//多行并滚动属性后要添加下面一行代码，否者没有滚动效果
		text.setMovementMethod(ScrollingMovementMethod.getInstance());
		
		//开一个线程去服务器端获取protocol文本
		getProtocol();
	}
	
	
	
	private void getProtocol(){
		new Thread(){
			public void run() {
				try {
					Log.i(TAG, "获取protocol文本");
					//获取protocol文本
					String protocol = new LoadProtocol(ProtocolActivity.this).loadProtocol();
					if(protocol==null){
						Log.i(TAG, "protocol获取失败");
						return;
					}
					Log.i(TAG, "protocol获取成功");
					//发消息通知handler更新协议文本
					Message message = handler.obtainMessage();
					message.obj = protocol;
					handler.sendMessage(message);
				} catch (Exception e) {
					e.printStackTrace();
				} 
			};
		}.start();
	}
	public void agreeClick(View view){
		//存取用户是否参与改善用户体验计划
		Editor editor = sp.edit();
		editor.putBoolean("isimprover", cb.isChecked());
		editor.commit();
		//跳转到引导界面
		Intent  intent = new Intent(this,GuideActivity.class);
		startActivity(intent);
		this.finish();//跳到引导页面，关闭当前activity
	}
}
