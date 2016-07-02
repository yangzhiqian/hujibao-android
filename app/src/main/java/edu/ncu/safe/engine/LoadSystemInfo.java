package edu.ncu.safe.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;

import java.util.ArrayList;
import java.util.List;

import edu.ncu.safe.domain.UserAppBaseInfo;

/**
 * Created by Mr_Yang on 2016/5/27.
 */
public class LoadSystemInfo {
    private Context context;

    public LoadSystemInfo(Context context) {
        this.context = context;
    }

    /**
     * 获取正在运行的程序的基本信息 包括程序的运行内存
     * @return
     */
    public List<UserAppBaseInfo> getRunningApplications(){
        List<UserAppBaseInfo> infos = new ArrayList<UserAppBaseInfo>();
        if(context==null){
            return infos;
        }
        PackageManager packageManager = context.getPackageManager();
        List<ApplicationInfo> installedApplications = packageManager.getInstalledApplications(0);

        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = manager.getRunningAppProcesses();
        for(ActivityManager.RunningAppProcessInfo runningAppProcessInfo:runningAppProcesses){
            for(ApplicationInfo applicationInfo:installedApplications){
                if(applicationInfo.uid == runningAppProcessInfo.uid){
                    String appName = (String) applicationInfo.loadLabel(packageManager);
                    Drawable icon = applicationInfo.loadIcon(packageManager);
                    final String packageName = applicationInfo.packageName;
                    Debug.MemoryInfo memoryInfo = manager.getProcessMemoryInfo(new int[]{runningAppProcessInfo.pid})[0];
                    int memory = memoryInfo.getTotalPrivateDirty();
                    UserAppBaseInfo info = new UserAppBaseInfo(applicationInfo.uid,icon,packageName, appName, false,false,false,false,true,memory,true);
                    infos.add(info);
                }
            }
        }
        return infos;
    }


}
