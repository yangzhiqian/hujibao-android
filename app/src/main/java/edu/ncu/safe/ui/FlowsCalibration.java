package edu.ncu.safe.ui;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import edu.ncu.safe.R;
import edu.ncu.safe.db.dao.FlowsDatabase;
import edu.ncu.safe.util.FlowsFormartUtil;
import edu.ncu.safe.util.FormatIntDate;

public class FlowsCalibration extends Activity implements OnClickListener {
	private static final String[] MESSAGES = { "cxll", "CXLL", "1081" };
	private static final String[] NUMBERS = { "10086", "10010", "10001" };

	private ImageView iv_back;
	private LinearLayout ll_messageCelibration;
	private EditText et_flowsRemian;
	private EditText et_flowsTotal;
	private SharedPreferences sp;
	private FlowsDatabase database;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_flowscalibration);

		database = new FlowsDatabase(this);
		sp = getSharedPreferences(
				FlowsProtectorActivity.FLOWSSHAREDPREFERENCES,
				Context.MODE_MULTI_PROCESS);

		iv_back = (ImageView) this.findViewById(R.id.back);
		ll_messageCelibration = (LinearLayout) this
				.findViewById(R.id.ll_messagecelibration);
		et_flowsRemian = (EditText) this.findViewById(R.id.et_flowsremain);
		et_flowsTotal = (EditText) this.findViewById(R.id.et_flowstotal);

		iv_back.setOnClickListener(this);
		ll_messageCelibration.setOnClickListener(this);
		et_flowsRemian.setOnClickListener(this);
		et_flowsTotal.setOnClickListener(this);

		initViewData();
	}

	private void initViewData() {
		long total = sp.getLong(FlowsProtectorActivity.FLOWSTOTAL, 0);
		if (total == 0) {
			// 当前没有设置数据
			et_flowsRemian.setText(0 + "");
			et_flowsTotal.setText(0 + "");
		} else {
			long offset = sp.getLong(FlowsProtectorActivity.DBFLOWSOFFSET, 0);
			// 该处不用检测offset的更新时间，应为在上一个界面一定会更新
			long dbFlows = database.queryCurrentMonthTotalFlows();
			long used = dbFlows + offset;
			long remain = total - used;
			et_flowsRemian.setText(FlowsFormartUtil.toMBFormat(remain));
			et_flowsTotal.setText(FlowsFormartUtil.toMBFormat(total));// 转换成MB显示
		}
	}

	int select = 0;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			overridePendingTransition(R.anim.activit3dtoright_in,
					R.anim.activit3dtoright_out);
			break;
		case R.id.ll_messagecelibration:
			Builder builder = new Builder(this);
			builder.setTitle("选择运营商");

			builder.setSingleChoiceItems(R.array.company, 0,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							select = which;
						}
					});
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 发送查询流量的消息
							Uri uri = Uri.parse("smsto:" + NUMBERS[select]);
							Intent intent = new Intent(Intent.ACTION_SENDTO,
									uri);
							intent.putExtra("sms_body", MESSAGES[select]);
							startActivity(intent);
						}
					});
			builder.setNegativeButton("取消", null);
			builder.create().show();
			break;
		case R.id.et_flowsremain:
			showInputRemainDialog();
			break;
		case R.id.et_flowstotal:
			showInputTotalDialog();
			break;
		}
	}

	private void showInputRemainDialog() {
		Builder builder = new Builder(this);
		builder.setTitle("请输入剩余流量");
		final EditText input = new EditText(this);
		input.setHint("以MB为单位");
		builder.setView(input);
		
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				long remain = 0;
				try {
					remain = (long) (Float.parseFloat(input.getText()
							.toString().trim()) * 1024 * 1024);
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), "输入的数据不合法！", 0)
							.show();
					return;
				}
				// 更新显示数据
				et_flowsRemian.setText(FlowsFormartUtil.toMBFormat(remain));

				long total = sp.getLong(FlowsProtectorActivity.FLOWSTOTAL, 0);
				if (total == 0) {
					// 没有设置过数据流量的值
					long db = database.queryCurrentMonthTotalFlows();
					total = db + remain;
					et_flowsTotal.setText(FlowsFormartUtil.toMBFormat(total));

					Editor editor = sp.edit();
					editor.putLong(FlowsProtectorActivity.FLOWSTOTAL, total);
					editor.putLong(FlowsProtectorActivity.DBFLOWSOFFSET, 0);
					editor.putInt(
							FlowsProtectorActivity.DBFLOWSOFFSETUPDATETIME,
							FormatIntDate.getCurrentFormatIntDate());
					editor.apply();
				} else {
					// 已经设置过了
					long db = database.queryCurrentMonthTotalFlows();
					long offset = total - remain - db;

					Editor editor = sp.edit();
					editor.putLong(FlowsProtectorActivity.DBFLOWSOFFSET, offset);
					editor.putInt(
							FlowsProtectorActivity.DBFLOWSOFFSETUPDATETIME,
							FormatIntDate.getCurrentFormatIntDate());
					editor.apply();
				}
			}
		});
		builder.setNegativeButton("取消", null);
		builder.create().show();
	}

	private void showInputTotalDialog() {
		Builder builder = new Builder(this);
		builder.setTitle("请输入套餐内总流量");
		final EditText input = new EditText(this);
		input.setHint("以MB为单位");
		builder.setView(input);
		
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				long total = 0;
				try {
					total = (long) (Float.parseFloat(input.getText()
							.toString().trim()) * 1024 * 1024);
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), "输入的数据不合法！", 0)
							.show();
					return;
				}
				// 更新显示数据
				et_flowsTotal.setText(FlowsFormartUtil.toMBFormat(total));
				
				long db = database.queryCurrentMonthTotalFlows();
				long remain = (long)(Float.parseFloat(et_flowsRemian.getText().toString().trim())*1024*1024);
				long offset = total - db - remain;
				
				Editor editor = sp.edit();
				editor.putLong(FlowsProtectorActivity.FLOWSTOTAL, total);
				editor.putLong(FlowsProtectorActivity.DBFLOWSOFFSET, offset);
				editor.putInt(FlowsProtectorActivity.DBFLOWSOFFSETUPDATETIME, FormatIntDate.getCurrentFormatIntDate());
				editor.apply();
			}
		});
		builder.setNegativeButton("取消", null);
		builder.create().show();
	}
	
	
	
	
	private long getOffset() {
		long offset = sp.getLong(FlowsProtectorActivity.DBFLOWSOFFSET, 0);
		int offsetUpdateDate = sp.getInt(
				FlowsProtectorActivity.DBFLOWSOFFSETUPDATETIME, 0);
		int date = FormatIntDate.getCurrentFormatIntDate();
		if (offsetUpdateDate / 100 != date / 100) {
			// offset不是本月的
			offset = 0;
			Editor editor = sp.edit();
			editor.putLong(FlowsProtectorActivity.DBFLOWSOFFSET, 0);
			editor.putInt(FlowsProtectorActivity.DBFLOWSOFFSETUPDATETIME, date);
			editor.apply();
		}
		return offset;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			this.finish();
			overridePendingTransition(R.anim.activit3dtoright_in, R.anim.activit3dtoright_out);
		}
		return super.onKeyDown(keyCode, event);
	}
}
