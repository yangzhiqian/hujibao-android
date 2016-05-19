package edu.ncu.safe.ui.fragment;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import edu.ncu.safe.R;
import edu.ncu.safe.adapter.FlowsStatisticsLV_AppAdapter;
import edu.ncu.safe.db.dao.FlowsDatabase;
import edu.ncu.safe.domain.FlowsStatisticsAppItemInfo;
import edu.ncu.safe.engine.LoadFlowsDataFromTrafficStats;

public class FlowsAppLVFragment extends Fragment {
	String tag = "FlowsAppLVFragment";
	private ListView lv_app;
	private FlowsStatisticsLV_AppAdapter adapter;
	private List<FlowsStatisticsAppItemInfo> dbInfos;
	private List<FlowsStatisticsAppItemInfo> preTrafficInfos;
	private LoadFlowsDataFromTrafficStats trafficStats;
	private FlowsDatabase database;
	private Timer timer;

	Handler myHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			List<FlowsStatisticsAppItemInfo> infos = loadNewestFlowsData();
			adapter.setInfos(infos);
			adapter.notifyDataSetChanged();
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_flowsstatistics_app,
				null);
		lv_app = (ListView) view.findViewById(R.id.lv_flowsapp);

		trafficStats = new LoadFlowsDataFromTrafficStats(getActivity());
		database = new FlowsDatabase(getActivity());
		preTrafficInfos = trafficStats.getAppFlowsData();
		dbInfos = database.queryFromAppFlowsDB();
		adapter = new FlowsStatisticsLV_AppAdapter(dbInfos, this.getActivity());
		timer = new Timer();

		lv_app.setAdapter(adapter);
		timer.scheduleAtFixedRate(new MyTimerTask(), 0, 1000);
		return view;
	}

	@Override
	public void onDestroy() {
		timer.cancel();
		super.onDestroy();
	}

	/**
	 * 初始化时获取一次数据库（数据库值）的作为第一次显示，并记录一下当前traffic里的数据（初始traffic值）
	 * 每次更新listview数据时，再次获取最新traffic数据
	 * 
	 * 显示的结果数据 = 最新traffic数据 - 初始traffic值 + 数据库值
	 * 
	 * @return 返回最新要显示的结果集
	 */
	private List<FlowsStatisticsAppItemInfo> loadNewestFlowsData() {
		List<FlowsStatisticsAppItemInfo> trafficInfos = trafficStats
				.getAppFlowsData();
		for (FlowsStatisticsAppItemInfo trafficInfo : trafficInfos) {
			for (FlowsStatisticsAppItemInfo preTrafficInfo : preTrafficInfos) {
				for (FlowsStatisticsAppItemInfo dbInfo : dbInfos) {
					if (trafficInfo.getUid() == preTrafficInfo.getUid()
							&& preTrafficInfo.getUid() == dbInfo.getUid()) {
						long newestUpdate = trafficInfo.getUpdate()
								- preTrafficInfo.getUpdate()
								+ dbInfo.getUpdate();
						long newestDownload = trafficInfo.getDownload()
								- preTrafficInfo.getDownload()
								+ dbInfo.getDownload();
						trafficInfo.setUpdate(newestUpdate);
						trafficInfo.setDownload(newestDownload);
					}
				}
			}
		}

		Collections.sort(trafficInfos,
				new Comparator<FlowsStatisticsAppItemInfo>() {
					@Override
					public int compare(FlowsStatisticsAppItemInfo o1,
							FlowsStatisticsAppItemInfo o2) {
						long total1 = o1.getUpdate() + o1.getDownload();
						long total2 = o2.getUpdate() + o2.getDownload();
						//降序
						if(total1 > total2){
							return -1;
						}
						if(total1 == total2){
							return 0;
						}
						return 1;
					}
				});
		return trafficInfos;
	}

	int index = 0;

	class MyTimerTask extends TimerTask {
		@Override
		public void run() {
			myHandler.sendEmptyMessage(0);
			Log.i(tag, "message:" + index++);
		}
	}
}
