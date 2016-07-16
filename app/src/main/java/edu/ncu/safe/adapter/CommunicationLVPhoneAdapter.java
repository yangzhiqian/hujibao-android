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
import edu.ncu.safe.db.dao.CommunicationDatabase;
import edu.ncu.safe.domain.InterceptionInfo;
import edu.ncu.safe.myadapter.MyLIstViewBaseAdapter;
import edu.ncu.safe.util.ContactUtil;

public class CommunicationLVPhoneAdapter extends MyLIstViewBaseAdapter
        implements OnClickListener {

    private List<InterceptionInfo> infos;

    public CommunicationLVPhoneAdapter(Context context) {
        this(context, new ArrayList<InterceptionInfo>());
    }

    public CommunicationLVPhoneAdapter(Context context, List<InterceptionInfo> infos) {
        super(context);
        this.infos = infos;
        database = new CommunicationDatabase(context);
    }

    public void setInfos(List<InterceptionInfo> infos) {
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
                    R.layout.item_listview_phone_communication, null);

            holder.tv_address = (TextView) convertView
                    .findViewById(R.id.tv_address);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);

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

        // 设置显示内容
        String type = "";
        switch (infos.get(position).getNumberType()) {
            case CommunicationDatabaseHelper.NUMBERTYPE_BLACK:
                type += context.getResources().getString(R.string.communication_interceptor_note_black_list);
                break;
            case CommunicationDatabaseHelper.NUMBERTYPE_WHITE:
                type += context.getResources().getString(R.string.communication_interceptor_note_white_list);
                break;
            default:
                type += context.getResources().getString(R.string.communication_interceptor_note_normal_list);
                break;
        }

        // 设置号码
        holder.tv_address.setText(infos.get(position).getNumber() + type);
        // 设置日期
        Date date = new Date(infos.get(position).getInterceptionTime());
        String str = date.getMonth() + "-" + date.getDate() + " "
                + date.getHours() + ":" + date.getMinutes();
        holder.tv_time.setText(str);

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
                showConfirmDialog(context.getResources().getString(R.string.dialog_title_normal_tip),
                        context.getResources().getString(R.string.dialog_message_sure_to_del_call), BUTTON1, position);
                break;
            case R.id.ll_recovery:
                showConfirmDialog(context.getResources().getString(R.string.dialog_title_normal_tip),
                        String.format(context.getResources().getString(R.string.dialog_message_sure_to_call_to), infos.get(position).getNumber()), BUTTON2, position);
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
        ItemInfo info2 = new ItemInfo(R.drawable.phone, context.getResources().getString(R.string.dialog_callback));
        ItemInfo info3 = new ItemInfo(R.drawable.message, String.format(context.getResources().getString(R.string.dialog_back_message_to), infos.get(position).getNumber()));
        ItemInfo info4 = new ItemInfo(R.drawable.whitelist, String.format(context.getResources().getString(R.string.dialog_set_to_white_list), infos.get(position).getNumber()));
        ItemInfo info5 = new ItemInfo(R.drawable.blacklist, String.format(context.getResources().getString(R.string.dialog_set_to_black_list), infos.get(position).getNumber()));
        ItemInfo info6 = new ItemInfo(R.drawable.cancel, context.getResources().getString(R.string.dialog_cancle));

        dialogInfos.add(info1);
        dialogInfos.add(info2);
        dialogInfos.add(info3);
        dialogInfos.add(info4);
        dialogInfos.add(info5);
        dialogInfos.add(info6);
    }

    // 删除
    @Override
    protected void doWhileButton1OKClicked(int position) {
        if (database.deleteOneInterceptionPhoneInfo(infos.get(position).getId())) {
            infos.remove(position);
            this.notifyDataSetChanged();
            dataChanged();
            makeToast(context.getResources().getString(R.string.toast_del_succeed));
        } else {
            makeToast(context.getResources().getString(R.string.toast_del_fail));
        }
    }

    // 回拨
    @Override
    protected void doWhileButton2OKClicked(int position) {
        ContactUtil.callTo(context, infos.get(position).getNumber());
    }

    @Override
    protected void doWhileButton3ItemClicked(int position, int innerPosition) {
        switch (innerPosition) {
            case 0:
                //删除
                showConfirmDialog(context.getResources().getString(R.string.dialog_title_normal_tip),
                        context.getResources().getString(R.string.dialog_message_sure_to_del_call), BUTTON1, position);
                break;
            case 1:
                //回拨
                showConfirmDialog(context.getResources().getString(R.string.dialog_title_normal_tip),
                        String.format(context.getResources().getString(R.string.dialog_message_sure_to_call_to), infos.get(position).getNumber()), BUTTON2, position);

                break;
            case 2:
                // 回信
                ContactUtil.sendMessageTo(context, infos.get(position).getNumber());
                break;

            case 3:
                // 添加为白名单
                showEditList(context.getResources().getString(R.string.dialog_title_add_white_list), infos.get(position).getNumber(), TYPE_WHITE);
                break;
            case 4:
                // 添加为黑名单
                showEditList(context.getResources().getString(R.string.dialog_title_add_black_list), infos.get(position).getNumber(), TYPE_BLACK);
                break;
            case 5:
                // 取消
                break;
        }
    }
}
