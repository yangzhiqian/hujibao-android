package edu.ncu.safe.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import edu.ncu.safe.R;
import edu.ncu.safe.domain.FlowsStatisticsAppItemInfo;
import edu.ncu.safe.util.FlowsFormartUtil;

public class FlowsStatisticsLV_AppAdapter extends BaseAdapter {

	private List<FlowsStatisticsAppItemInfo> infos = new ArrayList<FlowsStatisticsAppItemInfo>();
	private Context context;
	
	public FlowsStatisticsLV_AppAdapter(List<FlowsStatisticsAppItemInfo> infos,
			Context context) {
		super();
		this.infos = infos;
		this.context = context;
	}
	public List<FlowsStatisticsAppItemInfo> getInfos() {
		return infos;
	}
	public void setInfos(List<FlowsStatisticsAppItemInfo> infos) {
		this.infos = infos;
	}
	@Override
	public int getCount() {
		return infos.size();
	}
	@Override
	public Object getItem(int position) {
		return null;
	}
	@Override
	public long getItemId(int position) {
		return 0;
	}
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder holder;
		if(view == null){
			view = LayoutInflater.from(context).inflate(R.layout.item_listview_appflows, null);
			holder = new ViewHolder();
			holder.tv_appName = (TextView) view.findViewById(R.id.tv_appname);
			holder.tv_update = (TextView) view.findViewById(R.id.tv_update);
			holder.tv_download = (TextView) view.findViewById(R.id.tv_download);
			holder.tv_total = (TextView) view.findViewById(R.id.tv_total);
			
			view.setTag(holder);
		}else{
			holder = (ViewHolder) view.getTag();
		}
		
		holder.tv_appName.setText(infos.get(position).getAppName());
		long update = infos.get(position).getUpdate();
		if(update<=0){
			update = 0;
		}
		holder.tv_update.setText(FlowsFormartUtil.toFlowsFormart(update));
		long download = infos.get(position).getDownload();
		if(download<=0){
			download = 0;
		}
		holder.tv_download.setText(FlowsFormartUtil.toFlowsFormart(download));
		holder.tv_total.setText(FlowsFormartUtil.toFlowsFormart(update+download));
		return view;
	}
	
	
	class ViewHolder{
		public TextView tv_appName;
		public TextView tv_update;
		public TextView tv_download;
		public TextView tv_total;
	}
}
