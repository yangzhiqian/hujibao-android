//package edu.ncu.safe.test;
//
//import android.content.pm.PackageManager.NameNotFoundException;
//import android.test.AndroidTestCase;
//import android.widget.Toast;
//
//import java.util.List;
//
//import edu.ncu.safe.db.dao.CommunicationDatabase;
//import edu.ncu.safe.domain.UserAppInfo;
//import edu.ncu.safe.domain.InterceptionInfo;
//import edu.ncu.safe.engine.AppInfosLoader;
//
//public class UserAppInfoTest extends AndroidTestCase{
//	public void test() throws NameNotFoundException{
//		AppInfosLoader infos = new AppInfosLoader(getContext());
//		infos.getAllAppInfos();
//	}
//
//	public void test2(){
//		CommunicationDatabase db = new CommunicationDatabase(getContext());
//		InterceptionInfo info = new InterceptionInfo(-1, "yzq" ,"125211", System.currentTimeMillis(), "556sdfasdf", 1);
//		boolean b = db.insertOneInterceptionMSGInfo(info);
//		int i = db.queryInterceptionMSGCount();
//		System.out.println(db.queryInterceptionMSGCount());
//		Toast.makeText(getContext(),db.queryInterceptionMSGCount()+"",Toast.LENGTH_LONG).show();
//	}
//
//	public void testUserInfo() throws NameNotFoundException {
//		AppInfosLoader loadAppInfos = new AppInfosLoader(getContext());
//		List<UserAppInfo> infos = loadAppInfos.getAllAppInfos();
//		for (UserAppInfo info:infos){
//			List<UserAppInfo.PermissionInfo> pis = info.getPermissionInfos();
//			for(UserAppInfo.PermissionInfo pi : pis){
//				System.out.println(pi.getPermissionName());
//				System.out.println(pi.getPeimissionLabel());
//				System.out.println(pi.getPermissionDescription());
//				System.out.println(pi.getGroupLabel());
//				System.out.println(pi.isFlag());
//			}
//		}
//	}
//}
