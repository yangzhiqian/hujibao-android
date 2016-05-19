package edu.ncu.safe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import edu.ncu.safe.R;
import edu.ncu.safe.View.SlideView;
import edu.ncu.safe.db.dao.CommunicationDatabase;
import edu.ncu.safe.engine.LoadPhoneNumberOwnerPalce;

/**
 * Created by Mr_Yang on 2016/5/16.
 */
public class CommunicationLVNumberPlaceAdapter  extends BaseAdapter implements View.OnTouchListener, View.OnClickListener, SlideView.OnSlideListener {
    private List<LoadPhoneNumberOwnerPalce.NumberPlaceInfo> infos;
    private Context context;
    private CommunicationDatabase db;

    public CommunicationLVNumberPlaceAdapter(List<LoadPhoneNumberOwnerPalce.NumberPlaceInfo> infos, Context context) {
        this.infos = infos;
        this.context = context;
        db = new CommunicationDatabase(context);
    }

    @Override
    public int getCount() {
        return infos.size();
    }

    @Override
    public Object getItem(int position) {
        return infos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder = null;
        if(view==null){
            holder = new ViewHolder();
            view =  LayoutInflater.from(context).inflate(R.layout.item_listview_numberplace,null);
            holder.slideView = (SlideView) view.findViewById(R.id.sv);
            holder.rl_delete = (RelativeLayout) holder.slideView.findViewById(R.id.holder);
            View contentView = LayoutInflater.from(context).inflate(R.layout.content_numberplaceitem,null);
            holder.tv_number = (TextView) contentView.findViewById(R.id.tv_number);
            holder.tv_place = (TextView) contentView.findViewById(R.id.tv_place);
            holder.slideView.setContentView(contentView);

            view.setOnTouchListener(this);
            holder.slideView.setOnSlideListener(this);
            holder.rl_delete.setOnClickListener(this);
            holder.rl_delete.setTag(position);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        holder.tv_number.setText(infos.get(position).getAddress());
        holder.tv_place.setText(infos.get(position).toString());
        return view;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        System.out.println(status+" ");
        if(status==SLIDE_STATUS_ON){
            slideView.smoothScrollTo(0, 0);
            status = SLIDE_STATUS_OFF;
            return true;
        }

        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                ((ViewHolder)v.getTag()).slideView.onRequireTouchEvent(event);
                break;
            default:
                event.setAction(MotionEvent.ACTION_UP);
                ((ViewHolder)v.getTag()).slideView.onRequireTouchEvent(event);
                break;
        }
        return true;
    }
    @Override
    public void onClick(View v) {
        slideView.smoothScrollTo(0,0);
        this.status = SLIDE_STATUS_OFF;
        int position = (int) v.getTag();
        db.deleteNumberPlace(infos.get(position).getAddress());
        infos.remove(position);
        notifyDataSetChanged();
    }

    private int status = SLIDE_STATUS_OFF;
    private SlideView slideView = null;
    // TODO: 2016/5/16  
    @Override
    public void onSlide(View view, int status) {
        this.status = status;
        slideView = (SlideView) view;
    }

    class ViewHolder{
        public SlideView slideView;
        public RelativeLayout rl_delete;
        public TextView tv_number;
        public TextView tv_place;
    }
}
