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
import edu.ncu.safe.domain.FlowsStatisticsDayItemInfo;
import edu.ncu.safe.util.FlowsFormartUtil;
import edu.ncu.safe.util.FormatIntDate;

public class FlowsStatisticsLV_DayAdapter extends BaseAdapter {

	private List<FlowsStatisticsDayItemInfo> infos = new ArrayList<FlowsStatisticsDayItemInfo>();
	private Context context;
	
	public FlowsStatisticsLV_DayAdapter(List<FlowsStatisticsDayItemInfo> infos,
			Context context) {
		super();
		this.infos = infos;
		this.context = context;
	}
	public List<FlowsStatisticsDayItemInfo> getInfos() {
		return infos;
	}
	public void setInfos(List<FlowsStatisticsDayItemInfo> infos) {
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
			view = LayoutInflater.from(context).inflate(R.layout.item_listview_dayflows, null);
			holder = new ViewHolder();
			holder.tv_date = (TextView) view.findViewById(R.id.tv_date);
			holder.tv_flows = (TextView) view.findViewById(R.id.tv_flows);
			view.setTag(holder);
		}else{
			holder = (ViewHolder) view.getTag();
		}
		
		int month = FormatIntDate.getMonthFromFormatIntDate(infos.get(position).getDate());
		int day = FormatIntDate.getDayFromFormatIntDate(infos.get(position).getDate());
		holder.tv_date.setText(month+"月"+day+"日");
		long update = infos.get(position).getUpdate();
		long download = infos.get(position).getDownload();
		long total = update+download;
		holder.tv_flows.setText(FlowsFormartUtil.toFlowsFormart(total));
		return view;
	}
	
	
	class ViewHolder{
		public TextView tv_date;
		public TextView tv_flows;
	}
}
