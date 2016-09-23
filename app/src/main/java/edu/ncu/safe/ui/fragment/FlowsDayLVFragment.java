package edu.ncu.safe.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import edu.ncu.safe.R;
import edu.ncu.safe.adapter.FlowsStatisticsLV_DayAdapter;
import edu.ncu.safe.db.dao.FlowsDatabase;
import edu.ncu.safe.domain.FlowsStatisticsDayItemInfo;

public class FlowsDayLVFragment extends Fragment {
	
	private ListView lv_datFlows;
	private FlowsDatabase database;
	private FlowsStatisticsLV_DayAdapter adapter;
	private List<FlowsStatisticsDayItemInfo> infos;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_flowsstatistics_day, null);
		lv_datFlows = (ListView) view.findViewById(R.id.lv_flowsday);

		adapter = new FlowsStatisticsLV_DayAdapter(getActivity());
		lv_datFlows.setAdapter(adapter);

		database = new FlowsDatabase(getActivity());
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		infos = database.queryAllFromTotalFlowsDB();
		adapter.setInfos(infos);
		adapter.notifyDataSetChanged();
	}
}
