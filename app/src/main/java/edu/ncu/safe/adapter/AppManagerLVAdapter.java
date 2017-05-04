package edu.ncu.safe.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.ncu.safe.R;
import edu.ncu.safe.domain.UserAppBaseInfo;

/**
 * Created by Mr_Yang on 2016/5/21.
 */
public class AppManagerLVAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener {
    private Context context;
    private List<UserAppBaseInfo> infos;
    public AppManagerLVAdapter(Context context, List<UserAppBaseInfo> infos) {
        this.context = context;
        this.infos = infos;
    }
    public AppManagerLVAdapter(Context context) {
        this(context,new ArrayList<UserAppBaseInfo>());
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
            view = View.inflate(context, R.layout.item_listview_appmanager,null);
            holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
            holder.tv_appName = (TextView) view.findViewById(R.id.tv_appname);
            holder.tv_memory = (TextView) view.findViewById(R.id.tv_memory);
            holder.iv_privacy = (ImageView) view.findViewById(R.id.iv_privacy);
            holder.iv_cost = (ImageView) view.findViewById(R.id.iv_cost);
            holder.iv_location = (ImageView) view.findViewById(R.id.iv_location);
            holder.iv_wifi = (ImageView) view.findViewById(R.id.iv_wifi);
            holder.cb_check = (CheckBox) view.findViewById(R.id.cb_check);

            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        holder.cb_check.setTag(position);
        holder.cb_check.setOnCheckedChangeListener(this);

        holder.iv_icon.setImageDrawable(infos.get(position).getIcon());
        holder.tv_appName.setText(infos.get(position).getAppName()+"");
        holder.tv_memory.setText(String.format("%5.2f", infos.get(position).getRunMemory() / (1024f*1024)));

        holder.iv_privacy.setVisibility(infos.get(position).isPrivacy()?View.VISIBLE:View.GONE);
        holder.iv_cost.setVisibility(infos.get(position).isCost()?View.VISIBLE:View.GONE);
        holder.iv_location.setVisibility(infos.get(position).isLocation()?View.VISIBLE:View.GONE);
        holder.iv_wifi.setVisibility(infos.get(position).isWifi()?View.VISIBLE:View.GONE);

        holder.cb_check.setChecked(infos.get(position).isCheck());
        return view;
    }
    class ViewHolder{
        public ImageView iv_icon;
        public TextView tv_appName;
        public TextView tv_memory;
        public ImageView iv_privacy;
        public ImageView iv_cost;
        public ImageView iv_location;
        public ImageView iv_wifi;
        public CheckBox cb_check;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int position = (int) buttonView.getTag();
        infos.get(position).setIsCheck(isChecked);
    }
    public void setInfos(List<UserAppBaseInfo> infos) {
        this.infos = infos;
    }
    public List<UserAppBaseInfo> getInfos() {
        return infos;
    }
    /**
     * 返回用户已经勾选的要卸载的程序的包名
     * @return  List<Map.Entry<String,String>> 包名的集合
     */
    public List<Map.Entry<String,String>> getCheckedAppPackageName(){
        List<Map.Entry<String,String>> pkns = new ArrayList<Map.Entry<String,String>>();
        for (UserAppBaseInfo info : infos) {
            if (info.isCheck()) {
                Map.Entry entry = new AbstractMap.SimpleEntry(info.getAppName(),info.getPackName());
                pkns.add(entry);
            }
        }
        return pkns;
    }
    public void removeAndFlash(String packName){
        for (UserAppBaseInfo info : infos) {
            if (info.getPackName().equals(packName)) {
                infos.remove(info);
                notifyDataSetChanged();
                return;
            }
        }
    }
}
