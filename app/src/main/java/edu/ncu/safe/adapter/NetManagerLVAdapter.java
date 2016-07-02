package edu.ncu.safe.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.ncu.safe.R;
import edu.ncu.safe.domain.UserAppNetInfo;
import edu.ncu.safe.engine.IpTable;

/**
 * Created by Mr_Yang on 2016/5/21.
 */
public class NetManagerLVAdapter extends BaseAdapter implements View.OnClickListener {
    private Context context;
    private List<UserAppNetInfo> infos;
    private SharedPreferences sp;

    public NetManagerLVAdapter(Context context,SharedPreferences sp) {
        this(context,sp,new ArrayList<UserAppNetInfo>());
    }

    public NetManagerLVAdapter(Context context,SharedPreferences sp, List<UserAppNetInfo> infos) {
        this.context = context;
        this.infos = infos;
        this.sp = sp;
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
        if(!IpTable.hasRootAccess(context)){
            Toast.makeText(context,"护机宝没有取得root权限，无法使用防火墙功能！",Toast.LENGTH_LONG).show();
            return;
        }
        int position = (int) v.getTag();
        switch (v.getId()){
            case R.id.iv_GPRS:
                String GPRSS = sp.getString(IpTable.PERFS_GPRS,"");
                if(!infos.get(position).isGPRS()){
                    GPRSS = GPRSS.replace(infos.get(position).getUid() + "", "");
                    GPRSS = GPRSS.replace("||", "|");
                }else{
                    GPRSS=GPRSS+"|"+infos.get(position).getUid();
                }
                infos.get(position).setIsGPRS(!infos.get(position).isGPRS());
                ((ImageView)v).setImageResource(infos.get(position).isGPRS() ? R.drawable.yes : R.drawable.no);
                SharedPreferences.Editor edit = sp.edit();
                edit.putString(IpTable.PERFS_GPRS,GPRSS);
                edit.apply();
                break;
            case R.id.iv_WIFI:
                String WIFIS = sp.getString(IpTable.PERFS_WIFI,"");
                if(!infos.get(position).isWIFI()){
                    WIFIS = WIFIS.replace(infos.get(position).getUid() + "","");
                    WIFIS =WIFIS.replace("||","|");
                }else{
                    WIFIS=WIFIS+"|"+infos.get(position).getUid();
                }
                infos.get(position).setIsWIFI(!infos.get(position).isWIFI());
                ((ImageView)v).setImageResource(infos.get(position).isWIFI() ? R.drawable.yes : R.drawable.no);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(IpTable.PERFS_WIFI,WIFIS);
                editor.apply();
                break;
        }
    }

    class ViewHolder{
        public ImageView iv_icon;
        public TextView tv_appName;
        public TextView tv_flows;
        public ImageView iv_GPRS;
        public ImageView iv_WIFI;
    }
}
