package edu.ncu.safe.service;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import edu.ncu.safe.db.dao.FlowsDatabase;
import edu.ncu.safe.domain.FlowsStatisticsAppItemInfo;
import edu.ncu.safe.domain.FlowsStatisticsDayItemInfo;
import edu.ncu.safe.engine.LoadFlowsDataFromTrafficStats;
import edu.ncu.safe.util.FormatDate;

public class FlowsRecordService extends Service {
	private static final String TAG = "FlowsRecordService";
	private static final long UPDATETIME = 60000;// 1分钟更新一次
	private LoadFlowsDataFromTrafficStats trafficStats;// 用于加载trafficstats里面数据的对象
	private FlowsDatabase database;// 用于操作数据库的对象

	private List<FlowsStatisticsAppItemInfo> preAppTrafficFlowsInfos;// 用于记录每次更新后当时traffic里的数据
	private FlowsStatisticsDayItemInfo trafficDayClock0FlowsInfo;// 当天结算后的traffic值
	private Timer timer;// 时间对象，用于定时做某些任务

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (timer == null) {
			timer = new Timer();
			timer.scheduleAtFixedRate(new MyTimerTask(), 0, UPDATETIME);
		}

		if ("edu.ncu.myservice.flowsupdate".equals(intent.getAction())) {
			// 应用要求更新两个数据库的数据
			updateAppFlowsDBInfo();
			updateDayTotalFlowsDBInfo();
		}
		return Service.START_STICKY;// 被关闭后自动重启
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "FlowsRecordService   onDestroy");
		timer.cancel();
		super.onDestroy();
	}

	private void updateAppFlowsDBInfo() {
		if (trafficStats == null) {
			trafficStats = new LoadFlowsDataFromTrafficStats(
					getApplicationContext());
		}
		if (database == null) {
			database = new FlowsDatabase(getApplicationContext());
		}

		// 当前系统记录的流量数据
		List<FlowsStatisticsAppItemInfo> trafficInfos = trafficStats
				.getAppFlowsData();
		// 当前数据库里的数据
		List<FlowsStatisticsAppItemInfo> dbInfos = database
				.queryFromAppFlowsDB();
		if (preAppTrafficFlowsInfos == null) {
			preAppTrafficFlowsInfos = trafficInfos;
			return;
		}
		// taffic里可能有数据库中没有的
		// 对于两者都有的，做更新操作
		// 对于在taffic里有的，而数据库中没有的，做添加操作
		for (FlowsStatisticsAppItemInfo trafficInfo : trafficInfos) {
			FlowsStatisticsAppItemInfo dbInfo = itemFindInList(trafficInfo,
					dbInfos);
			if (dbInfo == null) {
				// taffic里有但数据库里没有 添加操作
				database.addIntoAppFlowsDB(trafficInfo);
			} else {
				// 数据库和taffic里都有,做更新操作
				FlowsStatisticsAppItemInfo preTrafficInfo = itemFindInList(
						trafficInfo, preAppTrafficFlowsInfos);
				long update = trafficInfo.getUpdate()
						- preTrafficInfo.getUpdate() + dbInfo.getUpdate();
				long download = trafficInfo.getDownload()
						- preTrafficInfo.getDownload() + dbInfo.getDownload();
				dbInfo.setUpdate(update);
				dbInfo.setDownload(download);
				database.updateIntoAppFlowsDB(dbInfo);
			}
		}
		// traffic里可以存在数据库里有单traffic里没有
		// 该现象代表该应用已经没删除
		for (FlowsStatisticsAppItemInfo dbInfo : dbInfos) {
			if (itemFindInList(dbInfo, trafficInfos) == null) {
				// 该条目数据库中有，traffic中没有
				database.deleteFromAppFlowsDB(dbInfo.getUid());
			}
		}
		preAppTrafficFlowsInfos = trafficInfos;
	}

	/**
	 * 从list集合中找到一个和item有相同uid的条目
	 * 
	 * @param item
	 *            条目
	 * @param infos
	 *            集合
	 * @return null代表没有找到
	 */
	private FlowsStatisticsAppItemInfo itemFindInList(
			FlowsStatisticsAppItemInfo item,
			List<FlowsStatisticsAppItemInfo> infos) {
		for (FlowsStatisticsAppItemInfo info : infos) {
			if (item.getUid() == info.getUid()) {
				return info;
			}
		}
		return null;
	}

	// 更新值为 trafficinfo - trafficDayClock0FlowsInfo + preDayFlowsInfo
	private void updateDayTotalFlowsDBInfo() {
		if (trafficStats == null) {
			trafficStats = new LoadFlowsDataFromTrafficStats(
					getApplicationContext());
		}
		if (database == null) {
			database = new FlowsDatabase(getApplicationContext());
		}

		FlowsStatisticsDayItemInfo trafficinfo = trafficStats.getMobileTotalFlowsData();
		FlowsStatisticsDayItemInfo dbInfo = database
				.queryCurrentDayFromTotalFlowsDB();
		if (dbInfo == null) {
			// 数据库中没有当天的数据流量信息,添加一条数据为0的条目
			dbInfo = new FlowsStatisticsDayItemInfo(
					FormatDate.getCurrentFormatIntDate(), 0, 0);
			database.addIntoTotalFlowsDB(dbInfo);
		}
		if (trafficDayClock0FlowsInfo == null) {
			trafficDayClock0FlowsInfo = trafficinfo;
			return;
		}

		long update = trafficinfo.getUpdate()
				- trafficDayClock0FlowsInfo.getUpdate() + dbInfo.getUpdate();
		long download = trafficinfo.getDownload()
				- trafficDayClock0FlowsInfo.getDownload()
				+ dbInfo.getDownload();
		dbInfo.setUpdate(update);
		dbInfo.setDownload(download);
		database.updateIntoTotalFlowsDB(dbInfo);

		trafficDayClock0FlowsInfo = trafficinfo;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	class MyTimerTask extends TimerTask {
		

		@Override
		public void run() {
			// 更新两个数据库的数据
			updateAppFlowsDBInfo();
			updateDayTotalFlowsDBInfo();
			Log.i(TAG, "FlowsRecordService");
		}
	}
}
