package edu.ncu.safe.engine;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PermissionInfo;
import android.graphics.drawable.Drawable;
import edu.ncu.safe.domain.AppInfo;
import edu.ncu.safe.domain.UserAppInfo;

public class LoadAppInfos {
	private Context context;
	public LoadAppInfos(Context context) {
		super();
		this.context = context;
	}

	public List<AppInfo> getAllAppInfos() throws NameNotFoundException{
		List<AppInfo> appInfos = new ArrayList<AppInfo>();
		
		PackageManager manager = context.getPackageManager();
		
		List<PackageInfo> infos = manager.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES|PackageManager.GET_PERMISSIONS);
		for(PackageInfo info:infos){
			int uid = info.applicationInfo.uid;
			String packName = info.packageName;
			String appName = info.applicationInfo.loadLabel(manager).toString();
			boolean isSystemApp = isSystemApp(info.applicationInfo.flags);
			Drawable icon = info.applicationInfo.loadIcon(manager);
			PermissionInfo[] permissionInfos = info.permissions;
			AppInfo appInfo = new AppInfo(uid, packName, appName, icon, permissionInfos,isSystemApp);
			appInfos.add(appInfo);
		}
		return appInfos;
	}
	
	public List<UserAppInfo> getUserAppInfos(){
		List<UserAppInfo> infos = new ArrayList<UserAppInfo>();
		try {
			List<AppInfo> appInfos = getAllAppInfos();
			for(AppInfo appInfo : appInfos){
				if(appInfo.isSystemApp()){
					continue;
				}
				UserAppInfo usrAppInfo = new UserAppInfo(appInfo.getAppName(), appInfo.getUid());
				infos.add(usrAppInfo);
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return infos;
	}
	
	

	/**
	 * 判断某个应用程序是 不是三方的应用程序
	 * @param info
	 * @return  ture代表第三方
	 */
    public boolean filterApp(ApplicationInfo info) {
        if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
        	//升级了的系统应用也算第三方
            return true;
        } else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
        	//不是系统应用
            return true;
        }
        return false;
    }
    
    /**
     * 判断某个应用的flag时候表示为系统应用
     * @param flag  ApplicationInfo.flag
     * @return true表示为系统该应用
     *      */
    private boolean isSystemApp(int flag){
    	 if ((flag & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
             return false;
         } else if ((flag & ApplicationInfo.FLAG_SYSTEM) == 0) {
             return false;
         }
         return true;
    }

}
