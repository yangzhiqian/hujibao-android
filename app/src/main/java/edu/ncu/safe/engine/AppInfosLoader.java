package edu.ncu.safe.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Debug;

import java.util.ArrayList;
import java.util.List;

import edu.ncu.safe.domain.UserAppBaseInfo;
import edu.ncu.safe.domain.UserAppInfo;
import edu.ncu.safe.domain.UserAppSimpleInfo;
import edu.ncu.safe.external.runningappinfo.ProcessManager;

public class AppInfosLoader {
    public static final String[][] PERMISSIONS = {
            {"android.permission.READ_PHONE_STATE","com.android.browser.permission.READ_HISTORY_BOOKMARKS","com.android.email.permission.READ_ATTACHMENT","android.permission.READ_SMS","android.permission.RECEIVE_SMS"},//隐私
            {"android.permission.CALL_PHONE","android.permission.SEND_SMS"},//费用
            {"android.permission.ACCESS_COARSE_LOCATION","android.permission.ACCESS_FINE_LOCATION"},//定位
            {"android.permission.INTERNET","android.permission.ACCESS_NETWORK_STATE","android.permission.ACCESS_WIFI_STATE","android.permission.CHANGE_WIFI_STATE","android.permission.CHANGE_NETWORK_STATE"}//网络
    };

    private Context context;

    public AppInfosLoader(Context context) {
        this.context = context;
    }


    /**
     * 加载精简版的用户软件信息
     *
     * @return 返回简单版的用户软件信息集合
     */
    public List<UserAppSimpleInfo> getUserAppSimpleInfos() {
        List<UserAppSimpleInfo> appInfos = new ArrayList<UserAppSimpleInfo>();
        PackageManager manager = context.getPackageManager();
        List<PackageInfo> infos = manager.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES | PackageManager.GET_PERMISSIONS);
        for (PackageInfo info : infos) {
            if (isSystemApp(info.applicationInfo.flags)) {
                continue;
            }
            int uid = info.applicationInfo.uid;
            String packName = info.packageName;
            String appName = info.applicationInfo.loadLabel(manager).toString();
            Drawable icon = info.applicationInfo.loadIcon(manager);
            UserAppSimpleInfo usrAppInfo = new UserAppSimpleInfo(uid, icon, packName, appName);
            appInfos.add(usrAppInfo);
        }
        return appInfos;
    }

    /**
     * 获取用户软件的主要信息，包括精简版的用户信息+用户软件的权限信息和状态
     *
     * @return
     */
    public List<UserAppInfo> getUserAppInfos() {
        List<UserAppInfo> userAppInfos = new ArrayList<UserAppInfo>();

        PackageManager manager = context.getPackageManager();
        List<UserAppSimpleInfo> userAppSimpleInfos = getUserAppSimpleInfos();
        for (UserAppSimpleInfo userAppSimpleInfo : userAppSimpleInfos) {
            List<UserAppInfo.PermissionInfo> permissionInfos = getPermissionInfo(manager, userAppSimpleInfo.getPackName());
            UserAppInfo userAppInfo = new UserAppInfo(userAppSimpleInfo.getUid(), userAppSimpleInfo.getIcon(), userAppSimpleInfo.getPackName(), userAppSimpleInfo.getAppName(), permissionInfos);
            userAppInfos.add(userAppInfo);
        }
        return userAppInfos;
    }

    public List<UserAppBaseInfo> getUserAppBaseInfo(){
        List<UserAppBaseInfo> appBaseInfos = new ArrayList<UserAppBaseInfo>();
        List<UserAppInfo> infos = getUserAppInfos();
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = Build.VERSION.SDK_INT>=21? ProcessManager.getRunningAppProcessInfo(context): manager.getRunningAppProcesses();
        for(UserAppInfo info:infos){
            UserAppBaseInfo baseInfo = new UserAppBaseInfo();
            baseInfo.setUid(info.getUid());
            baseInfo.setIcon(info.getIcon());
            baseInfo.setPackName(info.getPackName());
            baseInfo.setAppName(info.getAppName());

//            List<UserAppInfo.PermissionInfo> permissions = info.getPermissionInfos();
//            for(UserAppInfo.PermissionInfo permission:permissions){
//                System.out.println(info.getAppName()+"--->"+permission.getPermissionName()+"---->"+permission.getPeimissionLabel()+"----->"+permission.getGroupLabel()+"--------->"+permission.getGroupLabel());
//            }
            int per = matchPermissionType(info.getArrayPermission());
            if((per&1)>0){
                //第一位为1  说明含有隐私权限
                baseInfo.setIsPrivacy(true);
            }
            if((per&2)>0){
                //第二位为1  说明含有费用权限
                baseInfo.setIsCost(true);
            }
            if((per&4)>0){
                //第三位为1  说明含有定位权限
                baseInfo.setIsLocation(true);
            }
            if((per&8)>0){
                //第四位为1  说明含有网络权限
                baseInfo.setIsWifi(true);
            }

            for(ActivityManager.RunningAppProcessInfo runningAppProcessInfo:runningAppProcesses){
                if(info.getUid()==runningAppProcessInfo.uid){
                    //程序正在后台运行
                    baseInfo.setIsRunning(true);
                    Debug.MemoryInfo[] memoryInfo = manager.getProcessMemoryInfo(new int[]{runningAppProcessInfo.pid});
                    baseInfo.setRunMemory( memoryInfo[0].getTotalPrivateDirty());
                    break;
                }
            }
            appBaseInfos.add(baseInfo);
        }
        return appBaseInfos;
    }

    /**
     * 加载程序的权限信息
     *
     * @param manager  PackageManager
     * @param packName 包名
     * @return
     */
    private List<UserAppInfo.PermissionInfo> getPermissionInfo(PackageManager manager, String packName) {
        List<UserAppInfo.PermissionInfo> pis = new ArrayList<UserAppInfo.PermissionInfo>();

        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(packName, PackageManager.GET_PERMISSIONS);//通过包名，返回包信息
        } catch (NameNotFoundException e) {
            return pis;
        }
        String[] permissionInfos = info.requestedPermissions;//申请的权限
        if (permissionInfos == null) {
            return pis;
        }
        int[] permissionInfosFlags = info.requestedPermissionsFlags;//批准的权限

        for (int i = 0; i < permissionInfos.length; i++) {
            String permissionName = permissionInfos[i];
            PermissionInfo tmpPermInfo = null;
            PermissionGroupInfo pgi = null;//权限分为不同的群组，通过权限名，我们得到该权限属于什么类型的权限。
            try {
                tmpPermInfo = manager.getPermissionInfo(permissionName, 0);//通过permissionName得到该权限的详细信息
                pgi = manager.getPermissionGroupInfo(tmpPermInfo.group, 0);
            } catch (Exception e) {
                continue;
            }
            String permissionLabel = tmpPermInfo.loadLabel(manager).toString();
            String permissionDescription = "";
            try {
                permissionDescription = tmpPermInfo.loadDescription(manager).toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            String groupLabel = pgi.loadLabel(manager).toString();
            UserAppInfo.PermissionInfo pi = new UserAppInfo.PermissionInfo(permissionName, permissionLabel, permissionDescription, groupLabel, permissionInfosFlags[i] == 1 ? true : false);
            pis.add(pi);
        }
        return pis;
    }

    /**
     * 根据权限的名字匹配权限的类型
     * @param permissionName
     * @return  返回的整形每一位为一种情况，1代表true
     */
    private int matchPermissionType(String[] permissionName){
        int re = 0;
        //隐私
        if(hasComment(PERMISSIONS[0],permissionName)){
            re=re+1;
        }
        //费用
        if(hasComment(PERMISSIONS[1],permissionName)){
            re=re+2;
        }
        //隐私
        if(hasComment(PERMISSIONS[2],permissionName)){
            re=re+4;
        }
        //隐私
        if(hasComment(PERMISSIONS[3],permissionName)){
            re=re+8;
        }
        return re;
    }
    private boolean hasComment(String[] strs1,String[] strs2){
        for(String str1:strs1){
            for(String str2:strs2){
                if(str1.equals(str2)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断某个应用的flag时候表示为系统应用
     *
     * @param flag ApplicationInfo.flag
     * @return true表示为系统该应用
     */
    private boolean isSystemApp(int flag) {
        if ((flag & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
            return false;
        } else if ((flag & ApplicationInfo.FLAG_SYSTEM) == 0) {
            return false;
        }
        return true;
    }
}
