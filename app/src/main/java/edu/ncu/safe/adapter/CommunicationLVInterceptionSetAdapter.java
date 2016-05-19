package edu.ncu.safe.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.ncu.safe.R;

/**
 * Created by Mr_Yang on 2016/5/17.
 */
public class CommunicationLVInterceptionSetAdapter extends BaseAdapter {
    private List<ItemInfo> infos;
    private Context context;
    private int checkedPostion;

    public CommunicationLVInterceptionSetAdapter(int checkedPostion, Context context) {
        this.context = context;
        this.infos = getItemInfos();
        this.checkedPostion = checkedPostion;
        infos.get(checkedPostion).isCheck = true;
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
            view = View.inflate(context, R.layout.item_listview_interceptionset,null);
            holder.tv_name = (TextView) view.findViewById(R.id.tv_mode);
            holder.tv_note = (TextView) view.findViewById(R.id.tv_note);
            holder.cb_check = (CheckBox) view.findViewById(R.id.cb_check);

            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }

        holder.tv_name.setText(infos.get(position).name);
        holder.tv_note.setText(infos.get(position).note);
        holder.cb_check.setChecked(infos.get(position).isCheck);
        return view;
    }
    class ViewHolder{
        public TextView tv_name;
        public TextView tv_note;
        public CheckBox cb_check;
    }
     class ItemInfo{
        public String name;
        public String note;
        public boolean isCheck;
        public ItemInfo(String name, String note, boolean isCheck) {
            this.name = name;
            this.note = note;
            this.isCheck = isCheck;
        }
    }

    public void setItemSelected(View view,int position){
        infos.get(checkedPostion).isCheck = false;
        infos.get(position).isCheck = true;
        checkedPostion = position;
        notifyDataSetChanged();
    }

    private List<ItemInfo> getItemInfos(){
        List<ItemInfo> infos = new ArrayList<ItemInfo>();
        ItemInfo info1  = new ItemInfo("默认模式(模式1)","关闭拦截，接收所有来电和短信",false);
        ItemInfo info2  = new ItemInfo("普通模式(模式2)","只拦截黑名单，其他号码均不拦截",false);
        ItemInfo info3  = new ItemInfo("白名单模式(模式3)","只接受白名单的来电和短信（需设置白名单权限）",false);
        ItemInfo info4  = new ItemInfo("联系人模式(模式4)","只接受联系人和白名单来电和短信",false);
        ItemInfo info5  = new ItemInfo("会议模式(模式5)","拦截所有人的电话和短信",false);
        infos.add(info1);
        infos.add(info2);
        infos.add(info3);
        infos.add(info4);
        infos.add(info5);
        return infos;
    }


}
