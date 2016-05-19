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
import android.widget.Toast;

import edu.ncu.safe.R;
import edu.ncu.safe.adapter.CommunicationLVMASGAdapter;
import edu.ncu.safe.db.dao.CommunicationDatabase;
import edu.ncu.safe.myadapter.MyLIstViewBaseAdapter.OnDataChangedListener;
import edu.ncu.safe.util.MyLog;

public class CommunicationMsgFragment extends Fragment implements
        OnItemClickListener, OnDataChangedListener {
    private TextView tv_numbers;
    private ListView lv;
    private CommunicationLVMASGAdapter adapter;
    private CommunicationDatabase database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_msg_interception, null);
        //初始化
        tv_numbers = (TextView) view.findViewById(R.id.tv_msg_numbers);
        lv = (ListView) view.findViewById(R.id.lv_msg_interception);
        database = new CommunicationDatabase(getActivity());
        adapter = new CommunicationLVMASGAdapter(getActivity(),database.queryInterceptionMSGInfos(30,0));


        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
        adapter.addOnDataChangedListener(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        tv_numbers.setText(database.queryInterceptionMSGCount() + "");//改变拦截的数量
        adapter.setInfos(database.queryInterceptionMSGInfos(30,0));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void dataChanged() {
        tv_numbers.setText(database.queryInterceptionMSGCount() + "");
        adapter.hideTheHideView();
    }

    @Override
    public void onItemClick(AdapterView<?> vg, View view, int p, long i) {
        LinearLayout ll_now = ((CommunicationLVMASGAdapter.ViewHolder)(view.getTag())).ll_hideView;
        adapter.onItemClick(ll_now, lv);
    }

    private void makeToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }
    private void logi(String message) {
        MyLog.i("CommunicationMsgFragment", message);
    }
}
