package edu.ncu.safe.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import edu.ncu.safe.R;
import edu.ncu.safe.adapter.CommunicationLVPhoneAdapter;
import edu.ncu.safe.db.dao.CommunicationDatabase;
import edu.ncu.safe.base.adapter.CommunicationBWLIstViewBaseAdapter.OnDataChangedListener;

public class CommunicationPhoneFragment extends Fragment implements
		OnDataChangedListener, OnItemClickListener {

	private TextView tv_numbers;
	private ListView lv;
	private CommunicationLVPhoneAdapter adapter;
	private CommunicationDatabase database;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_phone_interception, null);
		tv_numbers = (TextView) view.findViewById(R.id.tv_number);
		lv = (ListView) view.findViewById(R.id.lv_phone_interception);
		database = new CommunicationDatabase(getActivity());
		adapter = new CommunicationLVPhoneAdapter(getActivity(),database.queryInterceptionPhoneInfos(30, 0));

		//改变拦截的数量
		tv_numbers.setText(database.queryInterceptionPhoneCount() + "");
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);
		adapter.addOnDataChangedListener(this);
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		tv_numbers.setText(database.queryInterceptionPhoneCount() + "");//改变拦截的数量
		adapter.setInfos(database.queryInterceptionPhoneInfos(30, 0));
		adapter.notifyDataSetChanged();
	}

	@Override
	public void dataChanged() {
		tv_numbers.setText(database.queryInterceptionPhoneCount() + "");
		adapter.hideTheHideView();
	}

	@Override
	public void onItemClick(AdapterView<?> vg, View view, int p, long i) {
		LinearLayout ll_now = ((CommunicationLVPhoneAdapter.ViewHolder)(view.getTag())).ll_hideView;
		adapter.onItemClick(ll_now, lv);
	}
}
