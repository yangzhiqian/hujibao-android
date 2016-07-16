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
                note += context.getResources().getString(R.string.communication_interceptor_note_black_list);
                break;
            case CommunicationDatabaseHelper.NUMBERTYPE_WHITE:
                note += context.getResources().getString(R.string.communication_interceptor_note_white_list);
                break;
            default:
                note += context.getResources().getString(R.string.communication_interceptor_note_normal_list);
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
                showConfirmDialog(context.getResources().getString(R.string.dialog_title_normal_tip), context.getResources().getString(R.string.dialog_message_sure_to_del_msg), BUTTON1, position);
                break;
            case R.id.ll_recovery:
                showConfirmDialog(context.getResources().getString(R.string.dialog_title_normal_tip), context.getResources().getString(R.string.dialog_message_sure_to_recovery_msg), BUTTON2, position);
                break;
            case R.id.ll_more:
                setDialogInfos(position);
                showMoreDialog(position);
                break;
        }
    }

    private void setDialogInfos(int position) {
        dialogInfos.clear();
        ItemInfo info1 = new ItemInfo(R.drawable.delete, context.getResources().getString(R.string.dialog_del));
        ItemInfo info2 = new ItemInfo(R.drawable.message_back, context.getResources().getString(R.string.dialog_recovery));
        ItemInfo info3 = new ItemInfo(R.drawable.message, String.format(context.getResources().getString(R.string.dialog_back_message_to),infos.get(position).getNumber()));
        ItemInfo info4 = new ItemInfo(R.drawable.phone,  String.format(context.getResources().getString(R.string.dialog_back_call_to),infos.get(position).getNumber()));
        ItemInfo info5 = new ItemInfo(R.drawable.whitelist, String.format(context.getResources().getString(R.string.dialog_set_to_white_list),infos.get(position).getNumber()));
        ItemInfo info6 = new ItemInfo(R.drawable.blacklist,  String.format(context.getResources().getString(R.string.dialog_set_to_black_list),infos.get(position).getNumber()));
        ItemInfo info7 = new ItemInfo(R.drawable.cancel, context.getResources().getString(R.string.dialog_cancle));

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
            makeToast(context.getResources().getString(R.string.toast_del_succeed));
        }else{
            makeToast(context.getResources().getString(R.string.toast_del_fail));
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
            makeToast(context.getResources().getString(R.string.toast_succeed_to_recovery_msg));
        }else{
            makeToast(context.getResources().getString(R.string.toast_fail_to_recovery_msg));
        }
    }

    @Override
    protected void doWhileButton3ItemClicked(int position, int innerPosition) {
        switch (innerPosition) {
            case 0:
                // 删除
                showConfirmDialog(context.getResources().getString(R.string.dialog_title_normal_tip),
                        context.getResources().getString(R.string.dialog_message_sure_to_del_msg), BUTTON1, position);
                break;
            case 1:
                // 恢复
                showConfirmDialog(context.getResources().getString(R.string.dialog_title_normal_tip),
                        context.getResources().getString(R.string.dialog_message_sure_to_recovery_msg), BUTTON2, position);
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
                showEditList(context.getResources().getString(R.string.dialog_title_add_white_list), infos.get(position).getNumber(), TYPE_WHITE);
                break;
            case 5:
                showEditList(context.getResources().getString(R.string.dialog_title_add_black_list), infos.get(position).getNumber(), TYPE_BLACK);
                // 添加为黑名单
                break;
            case 6:
                // 取消
                break;
        }
    }
}
