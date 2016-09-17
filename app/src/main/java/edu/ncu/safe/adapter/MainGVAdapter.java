package edu.ncu.safe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import edu.ncu.safe.R;
import edu.ncu.safe.customerview.CircleImageView;
import edu.ncu.safe.domain.MainGVItemInfo;

public class MainGVAdapter extends BaseAdapter {
	private LayoutInflater flater;
	private List<MainGVItemInfo> infos;
	public MainGVAdapter(Context context){
		this(context,new LinkedList<MainGVItemInfo>());
	}
	public MainGVAdapter(Context context,List<MainGVItemInfo> infos){
		this.infos = infos;
		flater = LayoutInflater.from(context);
	}

	public void setInfos(List<MainGVItemInfo> infos) {
		this.infos = infos;
	}

	@Override
	public int getCount() {
		return infos.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		ViewHolder holder;
		
		if(view==null){
			view = flater.inflate(R.layout.item_grideview_mainview, null);
			
			holder = new ViewHolder();
			holder.img = (CircleImageView) view.findViewById(R.id.main_gv_item_img);
			holder.title = (TextView) view.findViewById(R.id.main_gv_item_title);
			holder.anotation = (TextView) view.findViewById(R.id.main_gv_item_anotation);
			
			view.setTag(holder);
		}else{
			holder = (ViewHolder)view.getTag();
		}
		holder.img.setImageResource(infos.get(position).getIconR());
		holder.title.setText(infos.get(position).getTitle());
		holder.anotation.setText(infos.get(position).getNote());
		holder.anotation.setTextColor(infos.get(position).getColor());
		return view;
	}
	
	class ViewHolder{
		public CircleImageView img;
		public TextView title;
		public TextView anotation;
	}
}
