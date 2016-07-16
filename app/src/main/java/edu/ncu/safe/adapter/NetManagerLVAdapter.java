package edu.ncu.safe.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.ncu.safe.R;
import edu.ncu.safe.domain.UserAppNetInfo;

/**
 * Created by Mr_Yang on 2016/5/21.
 */
public class NetManagerLVAdapter extends BaseAdapter implements View.OnClickListener {
    private Context context;
    private List<UserAppNetInfo> infos;
    private OnNetChangeClickedListener listener;

    public NetManagerLVAdapter(Context context) {
        this(context,new ArrayList<UserAppNetInfo>());
    }

    public NetManagerLVAdapter(Context context,List<UserAppNetInfo> infos) {
        this.context = context;
        this.infos = infos;
    }

    public void setOnNetChangeClickedListener(OnNetChangeClickedListener listener){
        this.listener = listener;
    }

    public List<UserAppNetInfo> getInfos() {
        return infos;
    }

    public void setInfos(List<UserAppNetInfo> infos) {
        this.infos = infos;
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
            view = View.inflate(context, R.layout.item_listview_netmanager,null);
            holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
            holder.tv_appName = (TextView) view.findViewById(R.id.tv_appname);
            holder.tv_flows = (TextView) view.findViewById(R.id.tv_flows);
            holder.iv_GPRS = (ImageView) view.findViewById(R.id.iv_GPRS);
            holder.iv_WIFI = (ImageView) view.findViewById(R.id.iv_WIFI);


            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        holder.iv_GPRS.setOnClickListener(this);
        holder.iv_WIFI.setOnClickListener(this);
        holder.iv_GPRS.setTag(position);
        holder.iv_WIFI.setTag(position);

        holder.iv_icon.setImageDrawable(infos.get(position).getIcon());
        holder.tv_appName.setText(infos.get(position).getAppName() + "");
        holder.tv_flows.setText(String.format("%5.2f", infos.get(position).getFlows() / (1024.0 * 1024.0)));
        holder.iv_GPRS.setImageResource(infos.get(position).isGPRS() ? R.drawable.yes : R.drawable.no);
        holder.iv_WIFI.setImageResource(infos.get(position).isWIFI()?R.drawable.yes:R.drawable.no);
        return view;
    }

    @Override
    public void onClick(View v) {
       if(listener!=null){
           listener.onNetChangedClicked(v);
       }
    }

    public interface OnNetChangeClickedListener{
        void onNetChangedClicked(View v);
    }

    class ViewHolder{
        public ImageView iv_icon;
        public TextView tv_appName;
        public TextView tv_flows;
        public ImageView iv_GPRS;
        public ImageView iv_WIFI;
    }
}
