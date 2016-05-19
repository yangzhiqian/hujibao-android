package edu.ncu.safe.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import edu.ncu.safe.R;
import edu.ncu.safe.View.CircleIamgeView;
import edu.ncu.safe.domain.MainGVInfo;

public class MainGVAdapter extends BaseAdapter {

	private static final String TAG = "MainGVAdapter";
	MainGVInfo info;
	LayoutInflater flater;

	public void setInfo(MainGVInfo info) {
		this.info = info;
	}

	public MainGVAdapter(Context context,MainGVInfo info){
		this.info = info;
		flater = LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		return info.getSize();
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
			holder.img = (CircleIamgeView) view.findViewById(R.id.main_gv_item_img);
			holder.title = (TextView) view.findViewById(R.id.main_gv_item_title);
			holder.anotation = (TextView) view.findViewById(R.id.main_gv_item_anotation);
			
			view.setTag(holder);
		}else{
			holder = (ViewHolder)view.getTag();
		}
		holder.img.setImageResource(info.getIcons().get(position));
		holder.title.setText(info.getTitles().get(position));
		int ano = info.getAnotations().get(position);
		if(ano == 0){
			holder.anotation.setTextColor(Color.RED);
		}else{
			holder.anotation.setTextColor(Color.GREEN);
		}
		holder.anotation.setText(MainGVInfo.ano[position][ano]);
		return view;
	}
	
	class ViewHolder{
		public CircleIamgeView img;
		public TextView title;
		public TextView anotation;
	}

}
