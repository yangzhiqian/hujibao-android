package edu.ncu.safe.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.ncu.safe.R;
import edu.ncu.safe.customerview.CircleImageView;
import edu.ncu.safe.domain.MainMenuInfo;

/**
 * Created by Mr_Yang on 2016/9/17.
 */
public class MainMenuRecyclerViewAdapter extends RecyclerView.Adapter<MainMenuRecyclerViewAdapter.MyViewHolder> {
    private List<MainMenuInfo> list;
    private LayoutInflater inflater;
    private OnItemClickListener listener;
    public MainMenuRecyclerViewAdapter(Context context){
        this(context,new ArrayList<MainMenuInfo>());
    }
    public MainMenuRecyclerViewAdapter(Context context,@NonNull List<MainMenuInfo> list){
        this.list = list;
        inflater = LayoutInflater.from(context);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public void setList(List<MainMenuInfo> list) {
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(inflater.inflate(R.layout.item_listview_menu,null));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.img.setImageResource(list.get(position).getImgID());
        holder.title.setText(list.get(position).getTitle());
        String anotation = list.get(position).getAnotation();
        if(anotation==null||anotation.equals("")){
            holder.anotation.setVisibility(View.GONE);
        }else{
            holder.anotation.setText(anotation);
        }
        if(!list.get(position).isHasDirection()){
            holder.direction.setVisibility(View.GONE);
        }
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.itemClicked(holder.view,list.get(position),position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder{
        public View view;
        public CircleImageView img;
        public TextView title;
        public TextView anotation;
        public TextView direction;
        public MyViewHolder(View view) {
            super(view);
            this.view = view;
            this.img = (CircleImageView) view.findViewById(R.id.main_menu_igm);
            this.title = (TextView) view.findViewById(R.id.main_menu_title);
            this.anotation = (TextView) view.findViewById(R.id.main_menu_anotation);
            this.direction = (TextView) view.findViewById(R.id.main_menu_direction);
        }
    }

    public static interface OnItemClickListener{
        void itemClicked(View view,MainMenuInfo data,int position);
    }
}
