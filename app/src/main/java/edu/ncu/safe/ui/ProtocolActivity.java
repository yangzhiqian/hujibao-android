package edu.ncu.safe.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import edu.ncu.safe.R;
import edu.ncu.safe.engine.LoadProtocol;

public class ProtocolActivity extends Activity {
	private TextView tv_protocolText;

	Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			tv_protocolText.setText((String) msg.obj);
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_protocol);
		tv_protocolText = (TextView) this.findViewById(R.id.protocol_tv);
		//在xml中android:singleLine="false"  android:scrollbars="vertical"设置
		//多行并滚动属性后要添加下面一行代码，否者没有滚动效果
		tv_protocolText.setMovementMethod(ScrollingMovementMethod.getInstance());
		//开一个线程去服务器端获取protocol文本
		getProtocol();
	}

	private void getProtocol(){
		new Thread(){
			public void run() {
				try {
					//获取protocol文本
					String protocol = new LoadProtocol(ProtocolActivity.this).loadProtocol();
					if(protocol==null){
						return;
					}
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
		//跳转到引导界面
		Intent  intent = new Intent(this,GuideActivity.class);
		startActivity(intent);
		this.finish();//跳到引导页面，关闭当前activity
	}
}
