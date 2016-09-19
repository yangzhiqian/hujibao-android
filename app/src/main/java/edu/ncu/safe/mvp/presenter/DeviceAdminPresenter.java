package edu.ncu.safe.mvp.presenter;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import edu.ncu.safe.mvp.view.MvpView;

/**
 * Created by Mr_Yang on 2016/9/18.
 */
public class DeviceAdminPresenter {
    private MvpView view;
    private Context context;
    private Class clazz;

    public DeviceAdminPresenter(MvpView view, Context context,Class clazz) {
        this.view = view;
        this.context = context;
        this.clazz = clazz;
    }

    /**
     * 判断是当前代替的是否为设备管理者
     * @return
     */
    public boolean isDeviceAdmin(){
        DevicePolicyManager manager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName adminName = new ComponentName(context,clazz);
        return manager.isAdminActive(adminName);
    }

    public void removeDeviceAdmin(){
        DevicePolicyManager manager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName adminName = new ComponentName(context, clazz);
        manager.removeActiveAdmin(adminName);
    }

    public void toDeviceAdmin() {
        DevicePolicyManager manager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName adminName = new ComponentName(context, clazz);
        if (!manager.isAdminActive(adminName)) {
            Intent intent = new Intent(
                    DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(
                    DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                    adminName);
            ((Activity)context).startActivityForResult(intent,1);
        }
    }
}
