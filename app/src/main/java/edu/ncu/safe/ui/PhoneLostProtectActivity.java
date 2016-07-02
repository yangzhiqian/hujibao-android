package edu.ncu.safe.ui;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import edu.ncu.safe.R;
import edu.ncu.safe.adapter.ContactsDialogAdapter;
import edu.ncu.safe.engine.ContactsService;
import edu.ncu.safe.receiver.AdminReceiver;

public class PhoneLostProtectActivity extends Activity implements
		OnClickListener, OnCheckedChangeListener {
	public static final String[] ORDERS = {"#*delete*#","#*lock*#","#*ring*#","#*pwd*#","#*location*#"};
	public static final String DEFAULT_PWD = "123456";

	private static final String TAG = "PhoneLostProtectActivity";
	public static final String SHAREPERFERENCESNAME = "phonelostprotectconfigure";
	public static final String ISINPROTECTING = "ISINPROTECTING";
	public static final String ISADMIN = "ISADMIN";
	public static final String ISMESSAGE = "ISMESSAGE";
	public static final String ISDELETE = "ISDELETE";
	public static final String ISLOCK = "ISLOCK";
	public static final String ISRING = "ISRING";
	public static final String ISPWD = "ISPWD";
	public static final String ISLOCATION = "ISLOCATION";
	public static final String USERPHONENUMBER = "USERPHONENUMBER";
	public static final String SAFEPHONENUMBER = "SAFEPHONENUMBER";

	public static final String ENTERPWD = "ENTERPWD";
	public static final String HASSETPWD = "HASSETPWD";

	private ImageView iv_back;
	private View swapLine;
	private RotateAnimation swapLineAnimation;
	private ImageView iv_protect;
	private SharedPreferences sp;
	private CheckBox cb_message;
	private CheckBox cb_delete;
	private CheckBox cb_lock;
	private CheckBox cb_ring;
	private CheckBox cb_pwd;
	private CheckBox cb_location;

	private TextView tv_protectState;

	// 抽屉布局里的控件
	private ImageView iv_handle;
	private LinearLayout ll_pd;
	private LinearLayout ll_phoneNumberSet;
	private LinearLayout ll_device;
	private LinearLayout ll_introduction;
	private RotateAnimation clockwiseRotate;
	private RotateAnimation contraRotate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phonelostprotector);

		iv_back = (ImageView) this.findViewById(R.id.back);
		swapLine = this.findViewById(R.id.swapline);
		iv_protect = (ImageView) this.findViewById(R.id.iv_protect);
		cb_message = (CheckBox) this.findViewById(R.id.cb_message);
		cb_delete = (CheckBox) this.findViewById(R.id.cb_delete);
		cb_lock = (CheckBox) this.findViewById(R.id.cb_lock);
		cb_ring = (CheckBox) this.findViewById(R.id.cb_ring);
		cb_pwd = (CheckBox) this.findViewById(R.id.cb_pwd);
		cb_location = (CheckBox) this.findViewById(R.id.cb_location);
		tv_protectState = (TextView) this.findViewById(R.id.tv_protectstate);
		iv_handle = (ImageView) this.findViewById(R.id.handle);
		ll_pd = (LinearLayout) this.findViewById(R.id.ll_pdmodify);
		ll_phoneNumberSet = (LinearLayout) this
				.findViewById(R.id.ll_phonenumberset);
		ll_device = (LinearLayout) this.findViewById(R.id.ll_device);
		ll_introduction = (LinearLayout) this
				.findViewById(R.id.ll_introduction);

		swapLineAnimation = (RotateAnimation) AnimationUtils.loadAnimation(
				this, R.anim.rotate);
		clockwiseRotate = (RotateAnimation) AnimationUtils.loadAnimation(this,
				R.anim.clockwiserotate);
		iv_handle.startAnimation(clockwiseRotate);

		sp = this.getSharedPreferences(SHAREPERFERENCESNAME,
				Context.MODE_MULTI_PROCESS);

		iv_back.setOnClickListener(this);
		iv_protect.setOnClickListener(this);
		cb_message.setOnCheckedChangeListener(this);
		cb_delete.setOnCheckedChangeListener(this);
		cb_lock.setOnCheckedChangeListener(this);
		cb_ring.setOnCheckedChangeListener(this);
		cb_pwd.setOnCheckedChangeListener(this);
		cb_location.setOnCheckedChangeListener(this);

		ll_pd.setOnClickListener(this);
		ll_phoneNumberSet.setOnClickListener(this);
		ll_device.setOnClickListener(this);
		ll_introduction.setOnClickListener(this);
		init();
	}

	private void init() {
		boolean isPhoneProtecting = sp.getBoolean(ISINPROTECTING, false);
		boolean isSmsChangeSendMessage = sp.getBoolean(ISMESSAGE, false);
		boolean isCanDelete = sp.getBoolean(ISDELETE, false);
		boolean isCanLock = sp.getBoolean(ISLOCK, false);
		boolean isCanRing = sp.getBoolean(ISRING, false);
		boolean isCanChangePWD = sp.getBoolean(ISPWD, false);
		boolean isCanGetLocation = sp.getBoolean(ISLOCATION, false);

		if (isPhoneProtecting) {
			swapLine.startAnimation(swapLineAnimation);
			tv_protectState.setTextColor(Color.GREEN);
			tv_protectState.setText("开启(点击轮盘关闭)");
		} else {
			swapLine.clearAnimation();
			tv_protectState.setTextColor(Color.RED);
			tv_protectState.setText("关闭(点击轮盘关闭)");
		}

		if (isSmsChangeSendMessage) {
			cb_message.setChecked(true);
		}

		if (isCanDelete) {
			cb_delete.setChecked(true);
		}

		if (isCanLock) {
			cb_lock.setChecked(true);
		}

		if (isCanRing) {
			cb_ring.setChecked(true);
		}

		if (isCanChangePWD) {
			cb_pwd.setChecked(true);
		}

		if (isCanGetLocation) {
			cb_location.setChecked(true);
		}

		showSimChange();
	}

	private void showSimChange() {
		String userNumber = sp.getString(USERPHONENUMBER, null);
		final String phoneNumber = getPhoneNumber();
		if (phoneNumber == null) {
			Toast.makeText(this, "当前未插入SMS卡", Toast.LENGTH_SHORT).show();
			return;
		}
		if (userNumber == null || !userNumber.equals(phoneNumber)) {
			Builder builder = new Builder(this);
			builder.setTitle("保护号码设置");
			builder.setMessage("是否设置号码" + phoneNumber + "为保护号码？");
			builder.setPositiveButton("设置该号码",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Editor editor = sp.edit();
							editor.putString(USERPHONENUMBER, phoneNumber);
							editor.apply();
						}
					});
			builder.setNegativeButton("不设置", null);
			builder.create().show();
		}
	}

	private String getPhoneNumber() {
		TelephonyManager manager = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
		String phoneNumaber = manager.getLine1Number();
		if (phoneNumaber != null && "".equals(phoneNumaber)) {
			phoneNumaber = null;
		}
		return phoneNumaber;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back:// 点击返回图片
			finish();
			overridePendingTransition(R.anim.activit3dtoright_in,
					R.anim.activit3dtoright_out);
			break;
		case R.id.iv_protect:
			boolean isInProtecting = sp.getBoolean(ISINPROTECTING, false);
			Editor editor = sp.edit();
			if (isInProtecting) {
				editor.putBoolean(ISINPROTECTING, false);
				editor.apply();
				swapLine.clearAnimation();
				tv_protectState.setTextColor(Color.RED);
				tv_protectState.setText("关闭(点击轮盘关闭)");

			} else {
				// 开启保护是要对保护号码和安全号码进行设置
				if (!isNumbersOK()) {// 还有未完成的设置
					toSetNumbers();
					return;
				}
				// 已经完成设置，可以开启
				editor.putBoolean(ISINPROTECTING, true);
				editor.apply();
				swapLine.startAnimation(swapLineAnimation);
				tv_protectState.setTextColor(Color.GREEN);
				tv_protectState.setText("开启(点击轮盘关闭)");
			}
			break;
		case R.id.ll_pdmodify:
			showSetPWDDialog();
			break;
		case R.id.ll_phonenumberset:
			toSetNumbers();
			break;
		case R.id.ll_device:
			if (isDeviceAdmin()) {
				// 当前是deviceadmin
			} else {
				toDeviceAdmin();
			}
			break;
		case R.id.ll_introduction:
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		CheckBox cb = (CheckBox) buttonView;
		Editor editor = sp.edit();
		switch (buttonView.getId()) {
		case R.id.cb_message:
			editor.putBoolean(ISMESSAGE, isChecked);
			editor.apply();
			break;
		case R.id.cb_delete:
			changeDeviceAdminFuntion(isChecked, cb, ISDELETE);
			break;
		case R.id.cb_lock:
			changeDeviceAdminFuntion(isChecked, cb, ISLOCK);
			break;
		case R.id.cb_ring:
			editor.putBoolean(ISRING, isChecked);
			editor.apply();
			break;
		case R.id.cb_pwd:
			changeDeviceAdminFuntion(isChecked, cb, ISPWD);
			break;
		case R.id.cb_location:
			editor.putBoolean(ISLOCATION, isChecked);
			editor.apply();
			break;
		}
	}

	private void changeDeviceAdminFuntion(boolean isChecked, CheckBox cb,
			String name) {
		Editor editor = sp.edit();
		// 已经是设备管理员，并且是取消功能
		if (isChecked == false) {
			editor.putBoolean(name, false);
			editor.apply();
			return;
		}
		// 已经是设备管理员 充关闭到打开
		if (isDeviceAdmin()) {
			editor.putBoolean(name, true);
			editor.apply();
			return;
		}
		// 还不是设备管理员，申请成为设备管理员
		cb.setChecked(false);
		toDeviceAdmin();
	}

	private void toDeviceAdmin() {
		Builder builder = new Builder(this);
		builder.setTitle("激活设备管理员");
		builder.setMessage("是否跳转到激活该软件的设备管理者界面？");
		builder.setNegativeButton("不去了", null);
		builder.setPositiveButton("现在就去",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						DevicePolicyManager manager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
						ComponentName adminName = new ComponentName(
								PhoneLostProtectActivity.this,
								AdminReceiver.class);
						if (!manager.isAdminActive(adminName)) {
							Intent intent = new Intent(
									DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
							intent.putExtra(
									DevicePolicyManager.EXTRA_DEVICE_ADMIN,
									adminName);
							startActivity(intent);
						}
					}
				});
		builder.create().show();

	}

	private boolean isDeviceAdmin() {
		DevicePolicyManager manager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		ComponentName adminName = new ComponentName(this, AdminReceiver.class);
		return manager.isAdminActive(adminName);
	}

	/**
	 * 检测号码信息是否完成
	 * 
	 * @return ture 代表ok
	 */
	private boolean isNumbersOK() {
		String safeNumber = sp.getString(SAFEPHONENUMBER, null);
		if (safeNumber == null) {
			return false;
		}
		return true;
	}

	private void toSetNumbers() {
		String phoneNumber = getPhoneNumber();
		String userNumber = sp.getString(USERPHONENUMBER, null);
		String safeNumber = sp.getString(SAFEPHONENUMBER, null);

		final Dialog dialog = new Dialog(this, R.style.MyDialog);
		View view = LayoutInflater.from(this).inflate(
				R.layout.dialog_phonenumber, null);
		final EditText et_userNumber = (EditText) view
				.findViewById(R.id.usernumber);
		final EditText et_safeNumber = (EditText) view
				.findViewById(R.id.safenumber);
		Button btnYes = (Button) view.findViewById(R.id.yes);
		Button btnNo = (Button) view.findViewById(R.id.no);
		ImageView contacts = (ImageView) view.findViewById(R.id.contects);

		if (userNumber != null) {
			et_userNumber.setText(userNumber);
			et_userNumber.setEnabled(false);
		} else if (phoneNumber != null) {
			et_userNumber.setText(phoneNumber);
			et_userNumber.setEnabled(false);
		} else {
			et_userNumber.setHint("请输入要监测的号码");
			et_userNumber.setEnabled(true);
		}

		if (safeNumber != null) {
			et_safeNumber.setText(safeNumber);
		} else {
			et_safeNumber.setHint("请输入安全号码");
		}

		btnYes.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String userNumber = et_userNumber.getText().toString().trim();
				String safeNumber = et_safeNumber.getText().toString().trim();

				if ("".equals(userNumber) || "".equals(safeNumber)) {
					Toast.makeText(PhoneLostProtectActivity.this, "号码不能为空", Toast.LENGTH_SHORT)
							.show();
					return;
				}

				Editor edi = sp.edit();
				edi.putString(USERPHONENUMBER, userNumber);
				edi.putString(SAFEPHONENUMBER, safeNumber);
				edi.apply();
				dialog.dismiss();
				Toast.makeText(getApplicationContext(), "修改成功",
						Toast.LENGTH_SHORT).show();
			}
		});
		btnNo.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		contacts.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				final Dialog mDialog = new Dialog(
						PhoneLostProtectActivity.this, R.style.MyDialog);
				View mView = LayoutInflater.from(PhoneLostProtectActivity.this)
						.inflate(R.layout.dialog_contacts, null);

				ListView lv = (ListView) mView.findViewById(R.id.lv_contacts);
				ContactsService contactsService = new ContactsService(
						PhoneLostProtectActivity.this);
				final ContactsDialogAdapter adapter = new ContactsDialogAdapter(
						contactsService.getContactsInfos(),
						PhoneLostProtectActivity.this);
				lv.setAdapter(adapter);
				lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						String number = adapter.getNumber(position);
						et_safeNumber.setText(number);
						mDialog.dismiss();
					}
				});
				mDialog.setContentView(mView, new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				mDialog.show();
			}
		});

		dialog.setContentView(view, new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		dialog.show();
	}

	private void showSetPWDDialog() {
		final Dialog dialog = new Dialog(this, R.style.MyDialog);
		LayoutInflater inflater = LayoutInflater.from(this);
		View v = inflater.inflate(R.layout.dialog_passwordregister, null);
		final EditText pwd = (EditText) v.findViewById(R.id.pwd);
		final EditText pwdAgain = (EditText) v.findViewById(R.id.pwdagain);
		Button btnOK = (Button) v.findViewById(R.id.yes);
		Button btnCancle = (Button) v.findViewById(R.id.no);
		btnOK.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String pd = pwd.getText().toString().trim();
				String pdAgain = pwdAgain.getText().toString().trim();
				if (pd.equals(pdAgain)) {
					Editor editor = sp.edit();
					editor.putString(ENTERPWD, pd);
					editor.apply();
					dialog.dismiss();
					Toast.makeText(getApplicationContext(), "修改成功",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getApplicationContext(), "两次密码不相同，请重新输入",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		btnCancle.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.setCancelable(false);
		dialog.setContentView(v, new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		dialog.show();
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
