package edu.ncu.safe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import edu.ncu.safe.R;
import edu.ncu.safe.View.CircleImageView;
import edu.ncu.safe.domain.MainMenuInfo;

public class MainMenuAdapter extends BaseAdapter {
	List<MainMenuInfo> list;
	LayoutInflater inflater;
	Context context;

	public MainMenuAdapter(Context context,List<MainMenuInfo> list) {
		this.list = list;
		this.context = context;
		this.inflater = LayoutInflater.from(context);
	}

	public void setList(List<MainMenuInfo> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list.size();
	}
	@Override
	public Object getItem(int arg0) {
		return null;
	}
	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		ViewHodler hodler;
		if(view == null){
			view = inflater.inflate(R.layout.item_listview_menu, null);
			
			hodler = new ViewHodler();
			hodler.img = (CircleImageView) view.findViewById(R.id.main_menu_igm);
			hodler.title = (TextView) view.findViewById(R.id.main_menu_title);
			hodler.anotation = (TextView) view.findViewById(R.id.main_menu_anotation);
			hodler.direction = (TextView) view.findViewById(R.id.main_menu_direction);
			
			view.setTag(hodler);
		}else{
			hodler = (ViewHodler)view.getTag();
		}
		
		hodler.img.setImageResource(list.get(position).getImgID());
		hodler.title.setText(list.get(position).getTitle());
		
		
		String anotation = list.get(position).getAnotation();
		if(anotation==null||anotation.equals("")){
			hodler.anotation.setVisibility(View.GONE);
		}else{
			hodler.anotation.setText(anotation);
		}
		
		if(!list.get(position).isHasDirection()){
			hodler.direction.setVisibility(View.GONE);
		}
		
		return view;
	}
	
	class ViewHodler{
		public CircleImageView img;
		public TextView title;
		public TextView anotation;
		public TextView direction;
	}

}
