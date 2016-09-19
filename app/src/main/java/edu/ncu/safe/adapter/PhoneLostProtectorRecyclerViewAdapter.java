package edu.ncu.safe.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.ncu.safe.R;
import edu.ncu.safe.domain.PhoneLostProtectorSetsItem;

/**
 * Created by Mr_Yang on 2016/9/18.
 */
public class PhoneLostProtectorRecyclerViewAdapter extends RecyclerView.Adapter<PhoneLostProtectorRecyclerViewAdapter.MyViewHolder> {
    private LayoutInflater inflater;
    private List<PhoneLostProtectorSetsItem> infos;
    private OnItemCheckBoxClickedListener listener;


    public PhoneLostProtectorRecyclerViewAdapter(Context context) {
        this(context,new ArrayList<PhoneLostProtectorSetsItem>());
    }

    public PhoneLostProtectorRecyclerViewAdapter(Context context,List<PhoneLostProtectorSetsItem> infos) {
        inflater = LayoutInflater.from(context);
        this.infos = infos;
    }

    public void setInfos(List<PhoneLostProtectorSetsItem> infos) {
        this.infos = infos;
    }

    public void setOnItemClickedListener(OnItemCheckBoxClickedListener listener) {
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(inflater.inflate(R.layout.item_recyclerview_phonelostprotector_set,null));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.iv_icon.setImageResource(infos.get(position).getIconId());
        holder.tv_setName.setText(infos.get(position).getSetName());
        holder.tv_setNote.setText(infos.get(position).getSetNote());
        holder.checkBox.setChecked(infos.get(position).isChecked());

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(listener!=null){
                    listener.onCheckedChanged(infos.get(position),position, holder.checkBox,isChecked);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return infos.size();
    }

     class MyViewHolder extends RecyclerView.ViewHolder{
        View view;
        public ImageView iv_icon;
        TextView tv_setName;
        TextView tv_setNote;
        CheckBox checkBox;

        public MyViewHolder(View view) {
            super(view);
            this.view = view;
            iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
            tv_setName = (TextView) view.findViewById(R.id.tv_sets_name);
            tv_setNote = (TextView) view.findViewById(R.id.tv_sets_note);
            checkBox = (CheckBox) view.findViewById(R.id.cb);
        }
    }

    public interface OnItemCheckBoxClickedListener {
        void onCheckedChanged( PhoneLostProtectorSetsItem data, int position,CheckBox cb,boolean isChecked);
    }
}
