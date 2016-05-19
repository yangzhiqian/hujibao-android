package edu.ncu.safe.test;

import java.util.List;

import android.test.AndroidTestCase;
import edu.ncu.safe.db.dao.FlowsDatabase;
import edu.ncu.safe.domain.FlowsStatisticsAppItemInfo;

public class FlowsDatabaseTest extends AndroidTestCase {
	public void test() {
		FlowsDatabase database = new FlowsDatabase(getContext());
		for(int i=20;i<30;i++){
			FlowsStatisticsAppItemInfo info = new FlowsStatisticsAppItemInfo(i, "appName"+"i", (long)Math.pow(10, i), (long)Math.pow(i, 10));
			database.addIntoAppFlowsDB(info);
		}
		FlowsStatisticsAppItemInfo info = new FlowsStatisticsAppItemInfo(19, "appName"+"19", 0, 0);
		database.updateIntoAppFlowsDB(info);
		List<FlowsStatisticsAppItemInfo> infos = database.queryFromAppFlowsDB();
		for(FlowsStatisticsAppItemInfo in : infos){
			System.out.println(in.getUid()+in.getAppName()+in.getUpdate()+"====="+in.getDownload());
		}
		
	}
}
