package edu.ncu.safe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.ncu.safe.R;
import edu.ncu.safe.db.CommunicationDatabaseHelper;
import edu.ncu.safe.domain.InterceptionInfo;
import edu.ncu.safe.engine.SmsService;
import edu.ncu.safe.myadapter.MyLIstViewBaseAdapter;
import edu.ncu.safe.util.ContactUtil;

public class CommunicationLVMASGAdapter extends MyLIstViewBaseAdapter implements
        OnClickListener {
    private List<InterceptionInfo> infos;//拦截信息的信息

    public CommunicationLVMASGAdapter(Context context) {
        this(context, new ArrayList<InterceptionInfo>());
    }

    public CommunicationLVMASGAdapter(Context context, List<InterceptionInfo> infos) {
        super(context);
        this.infos = infos;
    }

    public void setInfos(List<InterceptionInfo> info) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.item_listview_msg_communication, null);

            holder.tv_address = (TextView) convertView
                    .findViewById(R.id.tv_address);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tv_note = (TextView) convertView.findViewById(R.id.tv_note);
            holder.ll_hideView = (LinearLayout) convertView
                    .findViewById(R.id.ll_hideview);
            holder.ll_delete = (LinearLayout) convertView
                    .findViewById(R.id.ll_delete);
            holder.ll_recovery = (LinearLayout) convertView
                    .findViewById(R.id.ll_recovery);
            holder.ll_more = (LinearLayout) convertView
                    .findViewById(R.id.ll_more);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        // 设置号码
        holder.tv_address.setText(infos.get(position).getNumber());
        // 设置日期
        Date date = new Date(infos.get(position).getInterceptionTime());
        String str = date.getMonth() + "-" + date.getDate() + " "
                + date.getHours() + ":" + date.getMinutes();
        holder.tv_time.setText(str);
        // 设置显示内容
        String note = infos.get(position).getName();
        switch (infos.get(position).getNumberType()) {
            case CommunicationDatabaseHelper.NUMBERTYPE_BLACK:
                note += "(黑名单):";
                break;
            case CommunicationDatabaseHelper.NUMBERTYPE_WHITE:
                note += "(白名单):";
                break;
            default:
                note += "(普通):";
                break;
        }
        note += infos.get(position).getMessageBody();
        holder.tv_note.setText(note);

        holder.ll_delete.setOnClickListener(this);
        holder.ll_delete.setTag(position);
        holder.ll_recovery.setOnClickListener(this);
        holder.ll_recovery.setTag(position);
        holder.ll_more.setOnClickListener(this);
        holder.ll_more.setTag(position);

        return convertView;
    }

    public class ViewHolder {
        public TextView tv_address;
        public TextView tv_time;
        public TextView tv_note;
        public LinearLayout ll_hideView;
        public LinearLayout ll_delete;
        public LinearLayout ll_recovery;
        public LinearLayout ll_more;
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        switch (v.getId()) {
            case R.id.ll_delete:
                showConfirmDialog("护机宝提示", "确定要删除该条信息吗？", BUTTON1, position);
                break;
            case R.id.ll_recovery:
                showConfirmDialog("护机宝提示", "确定要恢复该条数据到信息里吗？", BUTTON2, position);
                break;
            case R.id.ll_more:
                setDialogInfos(position);
                showMoreDialog(position);
                break;
        }
    }

    private void setDialogInfos(int position) {
        dialogInfos.clear();
        ItemInfo info1 = new ItemInfo(R.drawable.delete, "删除");
        ItemInfo info2 = new ItemInfo(R.drawable.message_back, "恢复");
        ItemInfo info3 = new ItemInfo(R.drawable.message, "给"
                + infos.get(position).getNumber() + "回复短信");
        ItemInfo info4 = new ItemInfo(R.drawable.phone, "给"
                + infos.get(position).getNumber() + "回拨电话");
        ItemInfo info5 = new ItemInfo(R.drawable.whitelist, "添加"
                + infos.get(position).getNumber() + "为白名单");
        ItemInfo info6 = new ItemInfo(R.drawable.blacklist, "添加"
                + infos.get(position).getNumber() + "为黑名单");
        ItemInfo info7 = new ItemInfo(R.drawable.cancel, "取消");

        dialogInfos.add(info1);
        dialogInfos.add(info2);
        dialogInfos.add(info3);
        dialogInfos.add(info4);
        dialogInfos.add(info5);
        dialogInfos.add(info6);
        dialogInfos.add(info7);
    }

    // 删除
    @Override
    protected void doWhileButton1OKClicked(int position) {
        if (deleteItem(position)) {
            makeToast("删除完成");
        }else{
            makeToast("删除失败，请重试！");
        }
    }

    private boolean deleteItem(int position) {
        // 删除此条记录
        if (database.deleteOneInterceptionMSGInfo(infos.get(position).getId())) {
            infos.remove(position);
            // 通知adapter改变数据了
            CommunicationLVMASGAdapter.this.notifyDataSetChanged();
            // 产生datachanged事件
            dataChanged();
            return true;
        } else {
            return false;
        }
    }

    // 恢复
    @Override
    protected void doWhileButton2OKClicked(int position) {
        // 恢复短信
        SmsService sms = new SmsService(context);
        if(sms.recoveryOneSms(infos.get(position).toSmsInfo())){
            // 删除此条记录
            deleteItem(position);
            makeToast("短信已经恢复到短信列表");
        }else{
            makeToast("恢复失败，请重试！");
        }
    }

    @Override
    protected void doWhileButton3ItemClicked(int position, int innerPosition) {
        switch (innerPosition) {
            case 0:
                // 删除
                showConfirmDialog("护机宝提示", "确定要删除该条信息吗？", BUTTON1, position);
                break;
            case 1:
                // 恢复
                showConfirmDialog("护机宝提示", "确定要恢复该条数据到信息里吗？", BUTTON2, position);
                break;
            case 2:
                // 回信
                ContactUtil.sendMessageTo(context, infos.get(position).getNumber());
                break;
            case 3:
                // 回拨
                ContactUtil.callTo(context, infos.get(position).getNumber());
                break;
            case 4:
                // 添加为白名单
                showEditList("添加白名单", infos.get(position).getNumber(), TYPE_WHITE);
                break;
            case 5:
                showEditList("添加黑名单", infos.get(position).getNumber(), TYPE_BLACK);
                // 添加为黑名单
                break;
            case 6:
                // 取消
                break;
        }
    }
}
