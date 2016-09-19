package edu.ncu.safe.mvp.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.view.View;
import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.List;

import edu.ncu.safe.MyApplication;
import edu.ncu.safe.R;
import edu.ncu.safe.customerview.MyDialog;
import edu.ncu.safe.domain.PhoneLostProtectorSetsItem;
import edu.ncu.safe.mvp.view.PhoneLostProtectorMvpView;
import edu.ncu.safe.receiver.AdminReceiver;
import edu.ncu.safe.util.MD5Encoding;
import edu.ncu.safe.util.MyDialogHelper;
import edu.ncu.safe.util.MyUtil;

/**
 * Created by Mr_Yang on 2016/9/18.
 */
public class PhoneLostProtectorPresenter {
    private PhoneLostProtectorMvpView view;
    private Context context;
    private DeviceAdminPresenter deviceAdminPresenter;

    public PhoneLostProtectorPresenter(PhoneLostProtectorMvpView view, Context context) {
        this.view = view;
        this.context = context;
        deviceAdminPresenter = new DeviceAdminPresenter(view,context,AdminReceiver.class);
    }


    public void init() {
        SharedPreferences sp = MyApplication.getSharedPreferences();
        boolean isPhoneProtecting = sp.getBoolean(MyApplication.SP_BOOLEAN_IS_IN_PROTECTING, false);
        if (isPhoneProtecting) {
            view.onProtectOpened();
        } else {
            view.onProtectClosed();
        }
        view.onSetsItemGet(getItesData());
        showSimChange();//检测是否要更换保护的手机卡

    }

    private List<PhoneLostProtectorSetsItem> getItesData() {
        SharedPreferences sp = MyApplication.getSharedPreferences();
        List<PhoneLostProtectorSetsItem> infos = new ArrayList<PhoneLostProtectorSetsItem>();
        //参考http://blog.csdn.net/jdsjlzx/article/details/46650659
        TypedArray ar = context.getResources().obtainTypedArray(R.array.phone_lost_protector_sets_iconids);
        int len = ar.length();
        int[] iconIds = new int[len];
        for (int i = 0; i < len; i++)
            iconIds[i] = ar.getResourceId(i, 0);
        ar.recycle();

        String[] names = context.getResources().getStringArray(R.array.phone_lost_protector_sets_names);
        String[] notes = context.getResources().getStringArray(R.array.phone_lost_protector_sets_notes);
        boolean[] checks = new boolean[6];
        checks[0] = sp.getBoolean(MyApplication.SP_BOOLEAN_IS_SMS_CHANGE_MESSAGE, false);
        checks[1] = sp.getBoolean(MyApplication.SP_BOOLEAN_IS_REMOTE_DELETE, false);
        checks[2] = sp.getBoolean(MyApplication.SP_BOOLEAN_IS_REMOTE_LOCK, false);
        checks[3] = sp.getBoolean(MyApplication.SP_BOOLEAN_IS_RING, false);
        checks[4] = sp.getBoolean(MyApplication.SP_BOOLEAN_IS_REMOTE_CHANGE_LOCK_PWD, false);
        checks[5] = sp.getBoolean(MyApplication.SP_BOOLEAN_IS_LOCATION, false);
        for (int i = 0; i < iconIds.length; i++) {
            infos.add(new PhoneLostProtectorSetsItem(iconIds[i], names[i], notes[i], checks[i]));
        }
        return infos;
    }


    public void protectingStateChanded() {
        SharedPreferences sp = MyApplication.getSharedPreferences();
        //拿到原来的状态
        boolean isInProtecting = sp.getBoolean(MyApplication.SP_BOOLEAN_IS_IN_PROTECTING, false);
        SharedPreferences.Editor editor = sp.edit();
//        editor.putBoolean(MyApplication.SP_BOOLEAN_IS_IN_PROTECTING, isInProtecting);
//        editor.apply();
        if (isInProtecting) {
            //原来是开启的，直接设置关闭
            editor.putBoolean(MyApplication.SP_BOOLEAN_IS_IN_PROTECTING, false);
            editor.apply();
            view.onProtectClosed();
        } else {
            //原来是关闭状态，开启要检测是否设置好安全号码，如果设置好，则开启，否则提示设置安全号码
            if (isNumbersOK()) {
                editor.putBoolean(MyApplication.SP_BOOLEAN_IS_IN_PROTECTING, true);
                editor.apply();
                view.onProtectOpened();
            } else {
                toSetNumbers();
            }
        }
    }

    /**
     * 判断是否要更换保护手机的手机号码
     * 如果没有插卡，则提示没有卡，不做处理
     * 如果有卡，则和原来设置的卡对比，如果相同，不做处理，如果不同，则提示用户是否更换成当前检测到的卡号
     */
    private void showSimChange() {
        final String phoneNumber = MyUtil.getPhoneNumber(context);
        if ("".equals(phoneNumber)) {
            //没有卡
            view.onNoSimCardFind();
            return;
        }
        String userPhoneNumber = MyApplication.getSharedPreferences().getString(MyApplication.SP_STRING_USER_PHONE_NUMBER, "");
        //有换卡，提示是否切换卡号
        if (!userPhoneNumber.equals(phoneNumber)) {
            final MyDialog myDialog = new MyDialog(context);
            myDialog.setTitle(context.getString(R.string.dialog_title_set_protect_number));
            myDialog.setMessage(String.format(context.getString(R.string.dialog_message_toprotector), phoneNumber));
            myDialog.setYESText(context.getString(R.string.dialog_button_ok_set_number));
            myDialog.setNOText(context.getString(R.string.dialog_button_cancle_setpout));
            myDialog.setPositiveListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //用户同意更换卡号
                    SharedPreferences.Editor editor = MyApplication.getSharedPreferences().edit();
                    editor.putString(MyApplication.SP_STRING_USER_PHONE_NUMBER, phoneNumber);
                    editor.apply();
                    myDialog.dismiss();
                }
            });
            myDialog.show();
        }
    }

    /**
     * 检测号码信息是否完成
     *
     * @return ture 代表ok
     */
    private boolean isNumbersOK() {
        String safeNumber = MyApplication.getSharedPreferences().getString(MyApplication.SP_STRING_USER_PHONE_NUMBER, "");
        if ("".equals(safeNumber)) {
            return false;
        }
        return true;
    }

    /**
     * 显示设置机主号码和安全号码的对话框
     */
    public void toSetNumbers() {
        MyDialogHelper.showResetPhoneNumbers(context, new MyDialogHelper.InputChecker() {
                    @Override
                    public boolean checkInputFormatLegal(String input) {
                        return MyUtil.isMobileNO(input);
                    }
                }
                , new MyDialogHelper.InputCallBack() {
                    @Override
                    public void inputSucceed(String input) {
                        String[] numbers = input.split(" ");
                        SharedPreferences.Editor edi = MyApplication.getSharedPreferences().edit();
                        edi.putString(MyApplication.SP_STRING_USER_PHONE_NUMBER, numbers[0]);
                        edi.putString(MyApplication.SP_STRING_SAFE_PHONE_NUMBER, numbers[1]);
                        edi.apply();
                        view.onNumbersModified();
                    }

                    @Override
                    public void inputError(String error) {

                    }
                });
    }

    public void resetCheckInPassword(){
        MyDialogHelper.showResetPWDDialog(context, new MyDialogHelper.InputChecker() {
            @Override
            public boolean checkInputFormatLegal(String input) {
                return input.trim().length()>=4;
            }
        }, new MyDialogHelper.InputCallBack() {
            @Override
            public void inputSucceed(String input) {
                SharedPreferences.Editor editor = MyApplication.getSharedPreferences().edit();
                editor.putString(MyApplication.SP_STRING_PWD, MD5Encoding.encoding(input));
                editor.putBoolean(MyApplication.SP_BOOLEAN_HAS_PWD, true);
                editor.apply();
                // 进入界面
                view.onPasswordModified();
            }

            @Override
            public void inputError(String error) {

            }
        });
    }

    /**
     * 根据状态自由选择激活还是取消激活设备管理权限
     */
    public void activityOrInactivityDeviceAdmin() {
        if (deviceAdminPresenter.isDeviceAdmin()) {
            // 当前是deviceadmin
            inactivityDeviceAdmin();
        } else {
            activityDeviceAdmin();
        }
    }


    /**
     * 提示用户是否确定注销本软件的设备管理权限，确定后注销
     */
    public void inactivityDeviceAdmin(){
        final MyDialog myDialog = new MyDialog(context);
        myDialog.setMessage(context.getString(R.string.dialog_message_sure_to_cancle_device_admin));
        myDialog.setPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deviceAdminPresenter.removeDeviceAdmin();
                view.onDeviceAdminInactivitied();
                myDialog.dismiss();
            }
        });
        myDialog.show();
    }

    /**
     * 显示是否去开启设备管理权限
     */
    public void activityDeviceAdmin(){
        final MyDialog myDialog = new MyDialog(context);
        myDialog.setTitle(context.getString(R.string.dialog_title_active_device_admin));
        myDialog.setMessage(context.getString(R.string.dialog_message_active_device_admin));
        myDialog.setYESText(context.getString(R.string.button_go_now));
        myDialog.setNOText(context.getString(R.string.button_un_go));
        myDialog.setPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deviceAdminPresenter.toDeviceAdmin();
                myDialog.dismiss();
            }
        });
        myDialog.setCancelable(false);
        myDialog.show();
    }

    /**
     * 替代activity去显示介绍本模块功能的对话框
     */
    public void introduceFunction() {
        final MyDialog myDialog = new MyDialog(context);
        myDialog.setTitle(context.getString(R.string.dialog_title_phone_lost_introduction));
        myDialog.setMessage(context.getString(R.string.dialog_message_phone_lost_introduction));
        myDialog.show();
    }

    /**
     * 判断当前的AdminReceiver.class是否有设备管理权限
     * @return  true表示有权限
     */
    public boolean isDeviceAdmin() {
        return deviceAdminPresenter.isDeviceAdmin();
    }

    public void setChanged(PhoneLostProtectorSetsItem data, int position, CheckBox cb, boolean isChecked) {
        if(data.isChecked()==isChecked){
            //没有变化
            return;
        }
        SharedPreferences.Editor editor = MyApplication.getSharedPreferences().edit();
        switch (position){
            case 0://换卡通知
                editor.putBoolean(MyApplication.SP_BOOLEAN_IS_SMS_CHANGE_MESSAGE, isChecked);
                editor.apply();
                break;
            case 1://远程删除
                if(isChecked) {
                    if (isDeviceAdmin()) {
                        editor.putBoolean(MyApplication.SP_BOOLEAN_IS_REMOTE_DELETE, true);
                        editor.apply();
                    } else {
                        activityDeviceAdmin();
                        cb.setChecked(false);
                        return;
                    }
                }else{
                    editor.putBoolean(MyApplication.SP_BOOLEAN_IS_REMOTE_DELETE, false);
                    editor.apply();
                }
                break;
            case 2://远程锁定
                if(isChecked) {
                    if (isDeviceAdmin()) {
                        editor.putBoolean(MyApplication.SP_BOOLEAN_IS_REMOTE_LOCK, true);
                        editor.apply();
                    } else {
                        activityDeviceAdmin();
                        cb.setChecked(false);
                        return;
                    }
                }else{
                    editor.putBoolean(MyApplication.SP_BOOLEAN_IS_REMOTE_LOCK, false);
                    editor.apply();
                }
                break;
            case 3://响铃
                editor.putBoolean(MyApplication.SP_BOOLEAN_IS_RING, isChecked);
                editor.apply();
                break;
            case 4://远程改密
                if(isChecked) {
                    if (isDeviceAdmin()) {
                        editor.putBoolean(MyApplication.SP_BOOLEAN_IS_REMOTE_CHANGE_LOCK_PWD, true);
                        editor.apply();
                    } else {
                        activityDeviceAdmin();
                        cb.setChecked(false);
                        return;
                    }
                }else{
                    editor.putBoolean(MyApplication.SP_BOOLEAN_IS_REMOTE_CHANGE_LOCK_PWD, false);
                    editor.apply();
                }
                break;
            case 5:
                editor.putBoolean(MyApplication.SP_BOOLEAN_IS_LOCATION, isChecked);
                editor.apply();
                break;
            default:
                return;
        }
        data.setChecked(isChecked);
    }
}
