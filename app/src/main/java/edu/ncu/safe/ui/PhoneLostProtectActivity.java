package edu.ncu.safe.ui;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
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

import edu.ncu.safe.MyApplication;
import edu.ncu.safe.R;
import edu.ncu.safe.customerview.MyDialog;
import edu.ncu.safe.adapter.ContactsDialogAdapter;
import edu.ncu.safe.engine.ContactsService;
import edu.ncu.safe.receiver.AdminReceiver;
import edu.ncu.safe.util.MD5Encoding;
import edu.ncu.safe.util.MyUtil;

public class PhoneLostProtectActivity extends MyAppCompatActivity implements
		OnClickListener, OnCheckedChangeListener {
	public static final String[] ORDERS = {"#*delete*#","#*lock*#","#*ring*#","#*pwd*#","#*location*#"};
//	public static final String DEFAULT_PWD = "123456";
//
//	private static final String TAG = "PhoneLostProtectActivity";
//	public static final String SHAREPERFERENCESNAME = "phonelostprotectconfigure";
//	public static final String ISINPROTECTING = "ISINPROTECTING";
//	public static final String ISADMIN = "ISADMIN";
//	public static final String ISMESSAGE = "ISMESSAGE";
//	public static final String ISDELETE = "ISDELETE";
//	public static final String ISLOCK = "ISLOCK";
//	public static final String ISRING = "ISRING";
//	public static final String ISPWD = "ISPWD";
//	public static final String ISLOCATION = "ISLOCATION";
//	public static final String USERPHONENUMBER = "USERPHONENUMBER";
//	public static final String SAFEPHONENUMBER = "SAFEPHONENUMBER";
//
//	public static final String ENTERPWD = "ENTERPWD";
//	public static final String HASSETPWD = "HASSETPWD";

	private View swapLine;
	private RotateAnimation swapLineAnimation;
	private ImageView iv_protect;
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
		initToolBar(getResources().getString(R.string.title_phone_lost_protector));
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
		SharedPreferences sp = MyApplication.getSharedPreferences();
		boolean isPhoneProtecting = sp.getBoolean(MyApplication.SP_BOOLEAN_IS_IN_PROTECTING, false);
		boolean isSmsChangeSendMessage = sp.getBoolean(MyApplication.SP_BOOLEAN_IS_SMS_CHANGE_MESSAGE, false);
		boolean isCanDelete = sp.getBoolean(MyApplication.SP_BOOLEAN_IS_REMOTE_DELETE, false);
		boolean isCanLock = sp.getBoolean(MyApplication.SP_BOOLEAN_IS_REMOTE_LOCK, false);
		boolean isCanRing = sp.getBoolean(MyApplication.SP_BOOLEAN_IS_RING, false);
		boolean isCanChangePWD = sp.getBoolean(MyApplication.SP_BOOLEAN_IS_REMOTE_CHANGE_LOCK_PWD, false);
		boolean isCanGetLocation = sp.getBoolean(MyApplication.SP_BOOLEAN_IS_LOCATION, false);

		if (isPhoneProtecting) {
			swapLine.startAnimation(swapLineAnimation);
			tv_protectState.setTextColor(getResources().getColor(R.color.state_ok));
			tv_protectState.setText(getResources().getString(R.string.phone_lost_protector_title_current_state_open));
		} else {
			swapLine.clearAnimation();
			tv_protectState.setTextColor(Color.RED);
			tv_protectState.setText(getResources().getString(R.string.phone_lost_protector_title_current_state_close));
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
		final String phoneNumber = MyUtil.getPhoneNumber(this);
		if ("".equals(phoneNumber)) {
			makeToast(getResources().getString(R.string.toast_no_sim));
			return;
		}
		String userPhoneNumber = MyApplication.getSharedPreferences().getString(MyApplication.SP_STRING_USER_PHONE_NUMBER, "");
		if (!userPhoneNumber.equals(phoneNumber)) {
			final MyDialog myDialog = new MyDialog(this);
			myDialog.setTitle(getResources().getString(R.string.dialog_title_set_protect_number));
			myDialog.setMessage(String.format(getResources().getString(R.string.dialog_message_toprotector), phoneNumber));
			myDialog.setYESText(getResources().getString(R.string.dialog_button_ok_set_number));
			myDialog.setNOText(getResources().getString(R.string.dialog_button_cancle_setpout));
			myDialog.setPositiveListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Editor editor = MyApplication.getSharedPreferences().edit();
					editor.putString(MyApplication.SP_STRING_USER_PHONE_NUMBER, phoneNumber);
					editor.apply();
					myDialog.dismiss();
				}
			});
			myDialog.show();
		}
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
				cancleDeviceAdmin();
			} else {
				toDeviceAdmin();
			}
			break;
		case R.id.ll_introduction:
			introduction();
			break;
		}
	}

	private void introduction() {
		final MyDialog myDialog = new MyDialog(this);
		myDialog.setTitle(getResources().getString(R.string.dialog_title_phone_lost_introduction));
		myDialog.setMessage(getResources().getString(R.string.dialog_message_phone_lost_introduction));
		myDialog.show();
	}

	private void cancleDeviceAdmin() {
		final MyDialog myDialog = new MyDialog(this);
		myDialog.setMessage(getResources().getString(R.string.dialog_message_sure_to_cancle_device_admin));
		myDialog.setPositiveListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DevicePolicyManager manager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
				ComponentName adminName = new ComponentName(
						PhoneLostProtectActivity.this,
						AdminReceiver.class);
				manager.removeActiveAdmin(adminName);
				makeToast(getResources().getString(R.string.phone_lost_protector_succeed_to_unregist_device_admin));
				myDialog.dismiss();
			}
		});
		myDialog.show();
	}

	private void changProtectingState() {
		SharedPreferences sp = MyApplication.getSharedPreferences();
		boolean isInProtecting = sp.getBoolean(MyApplication.SP_BOOLEAN_IS_IN_PROTECTING, false);
		Editor editor = sp.edit();
		if (isInProtecting) {
            editor.putBoolean(MyApplication.SP_BOOLEAN_IS_IN_PROTECTING, false);
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
            editor.putBoolean(MyApplication.SP_BOOLEAN_IS_IN_PROTECTING,true);
            editor.apply();
            swapLine.startAnimation(swapLineAnimation);
			tv_protectState.setTextColor(getResources().getColor(R.color.phone_lost_protector_title_current_open));
			tv_protectState.setText(getResources().getString(R.string.phone_lost_protector_title_current_state_open));
        }
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		CheckBox cb = (CheckBox) buttonView;
		Editor editor = MyApplication.getSharedPreferences().edit();
		switch (buttonView.getId()) {
		case R.id.cb_message:
			editor.putBoolean(MyApplication.SP_BOOLEAN_IS_SMS_CHANGE_MESSAGE, isChecked);
			editor.apply();
			break;
		case R.id.cb_delete:
			changeDeviceAdminFuntion(isChecked, cb, MyApplication.SP_BOOLEAN_IS_REMOTE_DELETE);
			break;
		case R.id.cb_lock:
			changeDeviceAdminFuntion(isChecked, cb, MyApplication.SP_BOOLEAN_IS_REMOTE_LOCK);
			break;
		case R.id.cb_ring:
			editor.putBoolean(MyApplication.SP_BOOLEAN_IS_RING, isChecked);
			editor.apply();
			break;
		case R.id.cb_pwd:
			changeDeviceAdminFuntion(isChecked, cb, MyApplication.SP_BOOLEAN_IS_REMOTE_CHANGE_LOCK_PWD);
			break;
		case R.id.cb_location:
			editor.putBoolean(MyApplication.SP_BOOLEAN_IS_LOCATION, isChecked);
			editor.apply();
			break;
		}
	}

	private void changeDeviceAdminFuntion(boolean isChecked, CheckBox cb,
			String name) {
		Editor editor = MyApplication.getSharedPreferences().edit();
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
		final MyDialog myDialog = new MyDialog(this);
		myDialog.setTitle(getResources().getString(R.string.dialog_title_active_device_admin));
		myDialog.setMessage(getResources().getString(R.string.dialog_message_active_device_admin));
		myDialog.setYESText(getResources().getString(R.string.button_go_now));
		myDialog.setNOText(getResources().getString(R.string.button_un_go));
		myDialog.setPositiveListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
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
				myDialog.dismiss();
			}
		});
		myDialog.show();
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
		String safeNumber = MyApplication.getSharedPreferences().getString(MyApplication.SP_STRING_USER_PHONE_NUMBER,"");
		if ("".equals(safeNumber)) {
			return false;
		}
		return true;
	}

	private void toSetNumbers() {
		String phoneNumber = MyUtil.getPhoneNumber(this);
		final SharedPreferences sp = MyApplication.getSharedPreferences();
		String userNumber = sp.getString(MyApplication.SP_STRING_USER_PHONE_NUMBER, null);
		String safeNumber = sp.getString(MyApplication.SP_STRING_SAFE_PHONE_NUMBER, null);

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
				if(!MyUtil.isMobileNO(userNumber)){
					et_userNumber.setError(getResources().getString(R.string.error_number_format));
					return;
				}
				if("".equals(safeNumber)){
					et_safeNumber.setError(getResources().getString(R.string.error_number_can_not_null));
					return;
				}
				if(!MyUtil.isMobileNO(safeNumber)){
					et_safeNumber.setError(getResources().getString(R.string.error_number_format));
					return;
				}
				Editor edi = sp.edit();
				edi.putString(MyApplication.SP_STRING_USER_PHONE_NUMBER, userNumber);
				edi.putString(MyApplication.SP_STRING_SAFE_PHONE_NUMBER, safeNumber);
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
					SharedPreferences sp = MyApplication.getSharedPreferences();
					Editor editor = sp.edit();
					editor.putString(MyApplication.SP_STRING_PWD, MD5Encoding.encoding(pd));
					editor.putBoolean(MyApplication.SP_BOOLEAN_HAS_PWD, true);
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
}
