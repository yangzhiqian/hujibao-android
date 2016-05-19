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
import edu.ncu.safe.domain.ContactsInfo;

public class ContactsDialogAdapter extends BaseAdapter {
	
	private List<ContactsInfo> infos = new ArrayList<ContactsInfo>();
	private Context context;
	
	public ContactsDialogAdapter( List<ContactsInfo> infos,Context context){
		this.infos = infos;
		this.context = context;
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
		return position;
	}

	
	static TextView view ;
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder holder;
		if(view==null){
			view = LayoutInflater.from(context).inflate(R.layout.item_listview_contactsl, null);
			holder = new ViewHolder();
			holder.tv_name = (TextView) view.findViewById(R.id.name);
			holder.tv_number = (TextView) view.findViewById(R.id.number);
			
			view.setTag(holder);
		}else{
			holder = (ViewHolder) view.getTag();
		}
		holder.tv_name.setText(infos.get(position).getName());
		holder.tv_number.setText(infos.get(position).getPhoneNumber());
		return view;
	}
	
	public String getNumber(int position){
		return infos.get(position).getPhoneNumber();
	}
	class ViewHolder{
		TextView tv_name;
		TextView tv_number;
	}
}
