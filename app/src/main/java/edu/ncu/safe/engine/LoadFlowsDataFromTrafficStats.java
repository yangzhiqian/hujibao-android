package edu.ncu.safe.engine;

import android.content.Context;
import android.net.TrafficStats;

import java.util.ArrayList;
import java.util.List;

import edu.ncu.safe.domain.FlowsStatisticsAppItemInfo;
import edu.ncu.safe.domain.FlowsStatisticsDayItemInfo;
import edu.ncu.safe.domain.TotalFlowsData;
import edu.ncu.safe.domain.UserAppSimpleInfo;
import edu.ncu.safe.util.FormatDate;
import edu.ncu.safe.util.NetTypeUtil;

public class LoadFlowsDataFromTrafficStats {
	private Context context;
	public LoadFlowsDataFromTrafficStats(Context context) {
		this.context = context;
	}


	public List<FlowsStatisticsAppItemInfo> getAppFlowsData(){
		List<FlowsStatisticsAppItemInfo> infos = new ArrayList<FlowsStatisticsAppItemInfo>();
		TrafficStats traffic = new TrafficStats();
		LoadAppInfos loadAppInfos = new LoadAppInfos(context);
		List<UserAppSimpleInfo> userAppSimpleInfos = loadAppInfos.getUserAppSimpleInfos();
		for(UserAppSimpleInfo userAppSimpleInfo : userAppSimpleInfos){
			long totalUpdate = traffic.getUidTxBytes(userAppSimpleInfo.getUid());
			long totalDownload = traffic.getUidRxBytes(userAppSimpleInfo.getUid());
			FlowsStatisticsAppItemInfo info = new FlowsStatisticsAppItemInfo(userAppSimpleInfo.getUid(), userAppSimpleInfo.getAppName(), totalUpdate, totalDownload);
			infos.add(info);
		}
		
		
		//计算其他没有名字或系统使用的流量数据
		long totalUpdate = TrafficStats.getTotalTxBytes();
		long totalDownload = TrafficStats.getTotalRxBytes();
		
		long totalAppUpdate = 0;
		long totalAppDownload = 0;
		for(FlowsStatisticsAppItemInfo info:infos){
			totalAppUpdate+=info.getUpdate();
			totalAppDownload+= info.getDownload();
		}
		
		long otherUpdate = totalUpdate - totalAppUpdate;
		long otherDownload = totalDownload - totalAppDownload;
		FlowsStatisticsAppItemInfo info = new FlowsStatisticsAppItemInfo(-1,"其他", otherUpdate, otherDownload);
		infos.add(info);
		return infos;
	}
	
	/**
	 * 从traffic里面获取此时的moblietotal信息
	 * @return 包含了 手机流量上传量和下载量的FlowsStatisticsDayItemInfo对象
	 */
	public FlowsStatisticsDayItemInfo getMobileTotalFlowsData(){
		
		int date = FormatDate.getCurrentFormatIntDate();
		long download = TrafficStats.getMobileRxBytes();
		long update = TrafficStats.getMobileTxBytes();
		FlowsStatisticsDayItemInfo info = new FlowsStatisticsDayItemInfo(date, update, download);
		return info;
	}
	
	
	/**
	 * 获取当前traffic里的总上传和总下载数据 包括gprs和wifi还有其他的数据
	 * @return 包含了总上传、总下载和当前的联网类型的TotalFlowsData对象
	 */
	public TotalFlowsData getTotalFlowsData(){
		int type = NetTypeUtil.getCurrentNetType(context);
		long download = TrafficStats.getTotalRxBytes();
		long update = TrafficStats.getTotalTxBytes();
		TotalFlowsData data = new TotalFlowsData(type, update, download);
		return data;
	}
}