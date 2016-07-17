package edu.ncu.safe.ui;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;

import edu.ncu.safe.MyApplication;
import edu.ncu.safe.R;
import edu.ncu.safe.View.MyDialog;
import edu.ncu.safe.db.dao.FlowsDatabase;
import edu.ncu.safe.myadapter.MyAppCompatActivity;
import edu.ncu.safe.util.FlowsFormartUtil;
import edu.ncu.safe.util.FormatDate;

public class FlowsCalibrationActicity extends MyAppCompatActivity implements OnClickListener {
	private static final String[] MESSAGES = { "cxll", "CXLL", "1081" };
	private static final String[] NUMBERS = { "10086", "10010", "10001" };

	private LinearLayout ll_messageCelibration;
	private EditText et_flowsRemian;
	private EditText et_flowsTotal;
	private FlowsDatabase database;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_flowscalibration);
		initToolBar(getResources().getString(R.string.title_flows_calibration));
		database = new FlowsDatabase(this);

		ll_messageCelibration = (LinearLayout) this
				.findViewById(R.id.ll_messagecelibration);
		et_flowsRemian = (EditText) this.findViewById(R.id.et_flowsremain);
		et_flowsTotal = (EditText) this.findViewById(R.id.et_flowstotal);

		ll_messageCelibration.setOnClickListener(this);
		et_flowsRemian.setOnClickListener(this);
		et_flowsTotal.setOnClickListener(this);

		initViewData();
	}

	private void initViewData() {
		SharedPreferences sp = MyApplication.getSharedPreferences();
		long total = sp.getLong(MyApplication.SP_LONG_TOTAL_FLOWS, 0);
		if (total == 0) {
			// 当前没有设置数据
			et_flowsRemian.setText(0 + "");
			et_flowsTotal.setText(0 + "");
		} else {
			long offset = sp.getLong(MyApplication.SP_LONG_DB_OFFSET, 0);
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
		case R.id.ll_messagecelibration:
			Builder builder = new Builder(this);
			builder.setTitle(getResources().getString(R.string.dialog_title_choose_operator));

			builder.setSingleChoiceItems(R.array.company, 0,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							select = which;
						}
					});
			builder.setPositiveButton(getResources().getString(R.string.ok),
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
			builder.setNegativeButton(getResources().getString(R.string.cancle), null);
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
		final MyDialog myDialog = new MyDialog(this);
		myDialog.setTitle(getResources().getString(R.string.dialog_title_input_remaind_flows));
		final EditText input = new EditText(this);
		input.setHint(getResources().getString(R.string.input_hint_as_MB));
		myDialog.setMessageView(input);
		myDialog.setPositiveListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				long remain = 0;
				try {
					remain = (long) (Float.parseFloat(input.getText()
							.toString().trim()) * 1024 * 1024);
				} catch (Exception e) {
					makeToast(getResources().getString(R.string.toast_error_invalid_input));
					return;
				}
				// 更新显示数据
				et_flowsRemian.setText(FlowsFormartUtil.toMBFormat(remain));
				SharedPreferences sp = MyApplication.getSharedPreferences();
				long total = sp.getLong(MyApplication.SP_LONG_TOTAL_FLOWS, 0);
				if (total == 0) {
					// 没有设置过数据流量的值
					long db = database.queryCurrentMonthTotalFlows();
					total = db + remain;
					et_flowsTotal.setText(FlowsFormartUtil.toMBFormat(total));

					Editor editor = sp.edit();
					editor.putLong(MyApplication.SP_LONG_TOTAL_FLOWS, total);
					editor.putLong(MyApplication.SP_LONG_DB_OFFSET, 0);
					editor.putInt(
							MyApplication.SP_INT_OFFSET_UPDATE,
							FormatDate.getCurrentFormatIntDate());
					editor.apply();
				} else {
					// 已经设置过了
					long db = database.queryCurrentMonthTotalFlows();
					long offset = total - remain - db;

					Editor editor = sp.edit();
					editor.putLong(MyApplication.SP_LONG_DB_OFFSET, offset);
					editor.putInt(
							MyApplication.SP_INT_OFFSET_UPDATE,
							FormatDate.getCurrentFormatIntDate());
					editor.apply();
				}
				myDialog.dismiss();
			}
		});
		myDialog.show();
	}

	private void showInputTotalDialog() {
		final MyDialog myDialog = new MyDialog(this);
		myDialog.setTitle(getResources().getString(R.string.dialog_title_total_flows));
		final EditText input = new EditText(this);
		input.setHint(getResources().getString(R.string.input_hint_as_MB));
		myDialog.setMessageView(input);
		myDialog.setPositiveListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				long total = 0;
				try {
					total = (long) (Float.parseFloat(input.getText()
							.toString().trim()) * 1024 * 1024);
				} catch (Exception e) {
					makeToast(getResources().getString(R.string.toast_error_invalid_input));
					return;
				}
				// 更新显示数据
				et_flowsTotal.setText(FlowsFormartUtil.toMBFormat(total));

				long db = database.queryCurrentMonthTotalFlows();
				long remain = (long) (Float.parseFloat(et_flowsRemian.getText().toString().trim()) * 1024 * 1024);
				long offset = total - db - remain;
				SharedPreferences sp = MyApplication.getSharedPreferences();
				Editor editor = sp.edit();
				editor.putLong(MyApplication.SP_LONG_TOTAL_FLOWS, total);
				editor.putLong(MyApplication.SP_LONG_DB_OFFSET, offset);
				editor.putInt(MyApplication.SP_INT_OFFSET_UPDATE, FormatDate.getCurrentFormatIntDate());
				editor.apply();
				myDialog.dismiss();
			}
		});
		myDialog.show();
	}
}
