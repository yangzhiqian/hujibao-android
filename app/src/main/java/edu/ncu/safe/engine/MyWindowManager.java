package edu.ncu.safe.engine;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import edu.ncu.safe.R;
import edu.ncu.safe.util.FlowsFormartUtil;
import edu.ncu.safe.util.NetTypeUtil;

public class MyWindowManager {

	public static final String FLOATDESKTOPWINDOWCONFIGURE = "FLoatDesktopWindowConfigure";
	public static final String ISFLOATDESKTOPWINDOWCONFIGURESHOW = "isShow";
	public static final String FLOATWINDOWX = "x";
	public static final String FLOATWINDOWY = "y";
	
	
	private static View floatView;
	private static ImageView iv_type;
	private static TextView tv_update;
	private static TextView tv_download;

	private static WindowManager manager;
	private static WindowManager.LayoutParams params;
	
	private static SharedPreferences sp;
	private static Context context;
	private static int width ;
	private static int height;
	public MyWindowManager(Context context) {
		this.context = context;
		sp = context.getSharedPreferences(FLOATDESKTOPWINDOWCONFIGURE, Context.MODE_MULTI_PROCESS);
		
		manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);

		// 参数设置
		params = new WindowManager.LayoutParams();
		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT; // 系统提示类型,重要
		params.format = 1;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; // 不能抢占聚焦点
		params.flags = params.flags
				| WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
		params.flags = params.flags
				| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS; // 排版不受限制
		params.alpha = 1.0f;
		params.gravity = Gravity.LEFT | Gravity.TOP; // 调整悬浮窗口至左上角
		// 以屏幕左上角为原点，设置x、y初始值
		params.x = sp.getInt(FLOATWINDOWX, 0);
		params.y = sp.getInt(FLOATWINDOWY, 0);
		// 设置悬浮窗口长宽数据
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		
		
		
		width = manager.getDefaultDisplay().getWidth();
		height = manager.getDefaultDisplay().getHeight();
	}
	
	public void showView(){
		if(floatView!=null){
			manager.removeView(floatView);
		}
		
		floatView = LayoutInflater.from(context).inflate(
				R.layout.floatwindow_netspeed, null);
		iv_type = (ImageView) floatView.findViewById(R.id.iv_type);
		tv_update = (TextView) floatView.findViewById(R.id.tv_update);
		tv_download = (TextView) floatView.findViewById(R.id.tv_download);
		floatView.setOnTouchListener(new MyOnToutchListener());
		
		manager.addView(floatView, params);
	}
	public void dismiss(){
		if(floatView==null){
			return;
		}
		manager.removeView(floatView);
		floatView = null;
	}
	
	public boolean isShow(){
		if(floatView == null){
			return false;
		}else{
			return true;
		}
	}
	
	public void setData(int type ,long update,long download){
		switch (type) {
		case NetTypeUtil.UNKNOW:
			iv_type.setImageResource(R.drawable.unknow);
			break;
		case NetTypeUtil.MOBILE_GPRS:
			iv_type.setImageResource(R.drawable.gprs);
			break;
		case NetTypeUtil.WIFI:
			iv_type.setImageResource(R.drawable.wifi);
			break;
		default:
			iv_type.setImageResource(R.drawable.unknow);
			break;
		}
		tv_update
				.setText(FlowsFormartUtil.toFlowsSpeedFormart(update));
		tv_download.setText(FlowsFormartUtil
				.toFlowsSpeedFormart(download));
	}
	class MyOnToutchListener implements OnTouchListener {
		float viewX;
		float viewY;
		public boolean onTouch(View v, MotionEvent event) {
			// 获取相对屏幕的坐标，即以屏幕左上角为原点
			float windowX = event.getRawX();
			float windowY = event.getRawY();
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				viewX = event.getX();
				viewY = event.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				// 获得speedView的左上角位置（相对于屏幕左上角）
				params.x = (int) (windowX - viewX);
				params.y = (int) (windowY - viewY);
				
				if(params.x<0){
					params.x = 0;
				}
				if(params.y<0){
					params.y = 0;
				}
				
				if(params.x + floatView.getWidth() > width){
					params.x = width - floatView.getWidth();
				}
				
				if(params.y + floatView.getHeight() > height){
					params.y = height - floatView.getHeight();
				}
				// 更新speedView的位置
				manager.updateViewLayout(floatView, params);
				break;
			case MotionEvent.ACTION_UP:
				Editor editor = sp.edit();
				editor.putInt(FLOATWINDOWX, params.x);
				editor.putInt(FLOATWINDOWY, params.y);
				editor.apply();
				break;
			}
			return false;
		}
	}
}
