package edu.ncu.safe.test;//package edu.ncu.safe.test;
//
//import java.util.List;
//
//import android.test.AndroidTestCase;
//import android.util.Log;
//import edu.ncu.safe.db.dao.CommunicationDatabase;
//import edu.ncu.safe.domain.InterceptionMSGInfo;
//import edu.ncu.safe.domain.InterceptionPhoneInfo;
//import edu.ncu.safe.domain.WhiteBlackNumberInfo;
//
//public class CommunicationDBTest extends AndroidTestCase {
//	public void testadd(){
//		CommunicationDatabase db = new CommunicationDatabase(getContext());
//		for(int i=0;i<10;i++){
//			InterceptionMSGInfo info = new InterceptionMSGInfo(0, i+"", null, 1, "dfgdg"+i, i%3);
//			db.insertMSGInfo(info);
//		}
//	}
//	
//	public void testquery(){
//		CommunicationDatabase db = new CommunicationDatabase(getContext());
//		List<InterceptionMSGInfo>  infos =db.queryLastestUsefulMSGInfo(20, 10);
//		for(InterceptionMSGInfo info : infos){
//			System.out.println("     id="+info.getId());
//			System.out.println("address="+info.getAddress());
//			System.out.println("   type="+info.getContactsType());
//			System.out.println("   date="+info.getInterceptTime().toLocaleString());
//			System.out.println("message="+info.getMessage());
//			System.out.println("  state="+info.getDataState());
//			System.out.println("-----------------------------------------");
//		}
//	}
//	
//	public void update(){
//		CommunicationDatabase db = new CommunicationDatabase(getContext());
//		db.deleteMSGInfo(5);
//		db.deleteMSGInfo(2);
//		db.deleteMSGInfo(1);
//		testquery();
//	}
//	
//	
//	
//	///phone
//	
//	public void testAddPhone(){
//		CommunicationDatabase db = new CommunicationDatabase(getContext());
//		for(int i=0;i<30;i++){
//			InterceptionPhoneInfo info = new InterceptionPhoneInfo(0, i+"", null, 1, i%3);
//			db.insertPhoneInfo(info);
//		}
//	}
//	
//	public void testQueryPhone(){
//		CommunicationDatabase db = new CommunicationDatabase(getContext());
//		List<InterceptionPhoneInfo>  infos =db.queryLastestUsefulPhoneInfo(20, 20);
//		for(InterceptionPhoneInfo info : infos){
//			System.out.println("     id="+info.getId());
//			System.out.println("address="+info.getAddress());
//			System.out.println("   type="+info.getContactsType());
//			System.out.println("   date="+info.getInterceptTime().toLocaleString());
//			System.out.println("  state="+info.getDataState());
//			System.out.println("-----------------------------------------");
//		}
//	}
//	
//	public void updatePhone(){
//		CommunicationDatabase db = new CommunicationDatabase(getContext());
//		db.deletePhoneInfo(9);
//		db.deletePhoneInfo(8);
//		db.deletePhoneInfo(7);
//		db.deletePhoneInfo(6);
//		db.deletePhoneInfo(5);
//		db.deletePhoneInfo(4);
//		db.deletePhoneInfo(3);
//		db.deletePhoneInfo(2);
//		db.deletePhoneInfo(1);
//		testQueryPhone();
//	}
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	public void testcommadd(){
//		CommunicationDatabase db = new CommunicationDatabase(getContext());
//		for(int i=0;i<50;i++){
//			WhiteBlackNumberInfo info = new WhiteBlackNumberInfo(Math.pow(i, 5)+"", null, i%2==1, i%10>5);
//			if(i%2==0){
//				db.insertWhiteNumber(info);
//			}else{
//				db.insertBlackNumber(info);
//			}
//		}
//	}
//	
//	public static final String TGA = "TGA";
//	public void testcommque(){
//		CommunicationDatabase db = new CommunicationDatabase(getContext());
//		List<WhiteBlackNumberInfo> infos = db.queryWhiteNumberInfos();
//		Log.i(TGA, "------------------白名单----------------------");
//		Log.i(TGA, infos.size()+"");
//		for(WhiteBlackNumberInfo info : infos){
//			Log.i(TGA," number:"+info.getNumber());
//			Log.i(TGA, "   note:"+info.getNote());
//			Log.i(TGA, "  issms:"+info.isSms());
//			Log.i(TGA, "isPhone:"+info.isPhoneCall());
//			Log.i(TGA, "-------------------------------------------");
//		}
//		
//		infos = db.queryBlackNumberInfos();
//		Log.i(TGA, "------------------黑名单----------------------");
//		Log.i(TGA, infos.size()+"");
//		for(WhiteBlackNumberInfo info : infos){
//			Log.i(TGA," number:"+info.getNumber());
//			Log.i(TGA, "   note:"+info.getNote());
//			Log.i(TGA, "  issms:"+info.isSms());
//			Log.i(TGA, "isPhone:"+info.isPhoneCall());
//			Log.i(TGA, "-------------------------------------------");
//		}
//	}
//	
//	public void testcommupdate(){
//		CommunicationDatabase db = new CommunicationDatabase(getContext());
//		WhiteBlackNumberInfo info = new WhiteBlackNumberInfo(0.0+"", "dfasg", true, true);
//		db.updateWhiteNumber(info);
//		
//
//		info = new WhiteBlackNumberInfo(2.82475249E8+"", "1234565", false, true);
//		db.updateBlackNumber(info);
//		testcommque();
//	}
//	
//	public void testcommdele(){
//		CommunicationDatabase db = new CommunicationDatabase(getContext());
//		
//		if(db.deleteWhiteNumber("0.0")){
//			Log.i(TGA, "删除成功");
//		}else{
//			Log.i(TGA, "删除失败");
//		}
//		testcommque();
//	}
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//}
