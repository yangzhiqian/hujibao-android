package edu.ncu.safe.test;//package edu.ncu.safe.test;
//
//import java.util.List;
//
//import android.test.AndroidTestCase;
//import edu.ncu.safe.db.dao.FlowsDatabase;
//import edu.ncu.safe.domain.FlowsStatisticsDayItemInfo;
//
//public class TotalDBtest extends AndroidTestCase {
//	public void test() {
//		FlowsDatabase database = new FlowsDatabase(getContext());
//		FlowsStatisticsDayItemInfo info = database.queryPresentDayFromTotalFlowsDB();
//		if(info==null){
//			System.out.println(" add befor fist is null");
//		}else{
//			System.out.println("add befor fist is not null");
//		}
//		
//		
//		for(int i=0;i<10;i++){
//			FlowsStatisticsDayItemInfo in = new FlowsStatisticsDayItemInfo(10-i, (long)Math.pow(i,10),(long)Math.pow(10, i));
//			database.addIntoTotalFlowsDB(in);
//		}
//		info = database.queryPresentDayFromTotalFlowsDB();
//		if(info==null){
//			System.out.println(" add after fist is null");
//		}else{
//			System.out.println(info.getDate()+"===" +info.getUpdate()+"======"+info.getDownload());
//		}
//		
//		
//		info = new FlowsStatisticsDayItemInfo(5, -100, -200);
//		database.updateIntoTotalFlowsDB(info);
//		database.deleteIntoTotalFlowsDB(6);
//		List<FlowsStatisticsDayItemInfo>  infos = database.queryAllFromTotalFlowsDB();
//		System.out.println(infos.size());
//		for(FlowsStatisticsDayItemInfo info2:infos){
//			System.out.println(info2.getDate()+"===" +info2.getUpdate()+"======"+info2.getDownload());
//		}
//		
//	}
//}
