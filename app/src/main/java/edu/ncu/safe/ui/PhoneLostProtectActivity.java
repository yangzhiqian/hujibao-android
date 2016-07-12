package edu.ncu.safe.ui;

import android.app.AlertDialog.Builder;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
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
import edu.ncu.safe.View.MyDialog;
import edu.ncu.safe.adapter.ContactsDialogAdapter;
import edu.ncu.safe.engine.ContactsService;
import edu.ncu.safe.receiver.AdminReceiver;

public class PhoneLostProtectActivity extends AppCompatActivity implements
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
	private Toolbar toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phonelostprotector);
		initToolBar();
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

	private void initToolBar() {
		toolbar = (Toolbar) findViewById(R.id.id_toolbar);
		setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);// 隐藏标题
//        getSupportActionBar().setIcon(R.drawable.user);//设置图标
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);// 是否显示返回按钮
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
				overridePendingTransition(R.anim.activit3dtoright_in,
						R.anim.activit3dtoright_out);
			}
		});
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
			makeToast(getResources().getString(R.string.toast_no_sim));
			return;
		}
		if (userNumber == null || !userNumber.equals(phoneNumber)) {
			MyDialog myDialog = new MyDialog(this);
			myDialog.setTitle(getResources().getString(R.string.dialog_title_set_protect_number));
			myDialog.setMessage(String.format(getResources().getString(R.string.dialog_message_toprotector), phoneNumber));
			myDialog.setYESText(getResources().getString(R.string.dialog_button_ok_set_number));
			myDialog.setNOText(getResources().getString(R.string.dialog_button_cancle_setpout));
			myDialog.setNegativeListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Editor editor = sp.edit();
					editor.putString(USERPHONENUMBER, phoneNumber);
					editor.apply();
				}
			});
			myDialog.show();
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
		case R.id.iv_protect:
			changProtectingState();
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

	private void changProtectingState() {
		boolean isInProtecting = sp.getBoolean(ISINPROTECTING, false);
		Editor editor = sp.edit();
		if (isInProtecting) {
            editor.putBoolean(ISINPROTECTING, false);
            editor.apply();
            swapLine.clearAnimation();
            tv_protectState.setTextColor(getResources().getColor(R.color.phone_lost_protector_title_current_close));
            tv_protectState.setText(getResources().getString(R.string.phone_lost_protector_title_current_state_close));

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
			tv_protectState.setTextColor(getResources().getColor(R.color.phone_lost_protector_title_current_open));
			tv_protectState.setText(getResources().getString(R.string.phone_lost_protector_title_current_state_open));
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

		final MyDialog myDialog = new MyDialog(this);
		myDialog.setTitle(getResources().getString(R.string.dialog_title_set_numbers));
		View view = LayoutInflater.from(this).inflate(
				R.layout.dialog_phonenumber, null);
		myDialog.setMessageView(view);
		final EditText et_userNumber = (EditText) view
				.findViewById(R.id.usernumber);
		final EditText et_safeNumber = (EditText) view
				.findViewById(R.id.safenumber);
		ImageView contacts = (ImageView) view.findViewById(R.id.contects);

		if (userNumber != null) {
			et_userNumber.setText(userNumber);
		} else if (phoneNumber != null) {
			et_userNumber.setText(phoneNumber);
		} else {
			et_userNumber.setHint(getResources().getString(R.string.dialog_edittext_hine_user_number));
		}

		if (safeNumber != null) {
			et_safeNumber.setText(safeNumber);
		} else {
			et_safeNumber.setHint(getResources().getString(R.string.dialog_edittext_hine_safe_number));
		}

		myDialog.setPositiveListener(new OnClickListener() {
			public void onClick(View v) {
				String userNumber = et_userNumber.getText().toString().trim();
				String safeNumber = et_safeNumber.getText().toString().trim();

				if("".equals(userNumber)){
					et_userNumber.setError(getResources().getString(R.string.error_number_can_not_null));
					return;
				}
				if("".equals(safeNumber)){
					et_safeNumber.setError(getResources().getString(R.string.error_number_can_not_null));
					return;
				}
				Editor edi = sp.edit();
				edi.putString(USERPHONENUMBER, userNumber);
				edi.putString(SAFEPHONENUMBER, safeNumber);
				edi.apply();
				myDialog.dismiss();
				makeToast(getResources().getString(R.string.number_modify_succeed));
			}
		});
		contacts.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showContactsDialog(et_safeNumber);
			}
		});
		myDialog.show();
	}

	private void showContactsDialog(final EditText et_safeNumber) {
		final MyDialog myDialog = new MyDialog(this);
		View view = LayoutInflater.from(PhoneLostProtectActivity.this)
                .inflate(R.layout.dialog_contacts, null);
		myDialog.setMessageView(view);
		ListView lv = (ListView) view.findViewById(R.id.lv_contacts);
		final ContactsDialogAdapter adapter = new ContactsDialogAdapter(
				new ContactsService(this).getContactsInfos(),
                PhoneLostProtectActivity.this);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                String number = adapter.getNumber(position);
                et_safeNumber.setText(number);
                myDialog.dismiss();
            }
        });
		myDialog.ShowYESNO(false);
		myDialog.show();
	}

	private void showSetPWDDialog() {
		final MyDialog myDialog = new MyDialog(this);
		myDialog.setTitle(getResources().getString(R.string.dialog_enter_pwd));
		final View view = LayoutInflater.from(this).inflate(R.layout.dialog_passwordregister, null);
		final AutoCompleteTextView pwd_one = (AutoCompleteTextView) view.findViewById(R.id.actv_pwd_one);
		final  AutoCompleteTextView pwd_two = (AutoCompleteTextView) view.findViewById(R.id.actv_pwd_two);
		myDialog.setMessageView(view);
		myDialog.setPositiveListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String pd = pwd_one.getText().toString().trim();
				String pdAgain = pwd_two.getText().toString().trim();
				if (pd.equals(pdAgain)) {
					SharedPreferences sp = getSharedPreferences(
									PhoneLostProtectActivity.SHAREPERFERENCESNAME,
									Context.MODE_MULTI_PROCESS);
					Editor editor = sp.edit();
					editor.putString(PhoneLostProtectActivity.ENTERPWD, pd);
					editor.putBoolean(PhoneLostProtectActivity.HASSETPWD, true);
					editor.apply();
					myDialog.dismiss();
					makeToast(getResources().getString(R.string.pwd_modify_succeed));
					return;
				} else {
					pwd_two.setError(getResources().getString(R.string.error_pwd_different));
				}
			}
		});
		myDialog.show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			this.finish();
			overridePendingTransition(R.anim.activit3dtoright_in, R.anim.activit3dtoright_out);
		}
		return super.onKeyDown(keyCode, event);
	}

	private void makeToast(String message){
		Toast.makeText(PhoneLostProtectActivity.this, message, Toast.LENGTH_SHORT).show();
	}
}
