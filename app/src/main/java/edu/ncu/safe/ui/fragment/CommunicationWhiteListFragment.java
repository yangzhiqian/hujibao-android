package edu.ncu.safe.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.ncu.safe.R;
import edu.ncu.safe.domain.WhiteBlackNumberInfo;
import edu.ncu.safe.base.fragment.CommunicationBWListViewFragment;
import edu.ncu.safe.util.ContactUtil;

public class CommunicationWhiteListFragment extends CommunicationBWListViewFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //初始化显示的图片
        phoneActivtId = R.drawable.phonegreen;
        phoneInactivtId = R.drawable.phoneacceptgray;
        messageAcitvityID = R.drawable.messagegreen;
        messageInactivityID = R.drawable.messageacceptgray;
        layout_id = R.layout.fragment_whitelist;
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected  void flash(){
        infos = database.queryWhiteNumberInfos();
        initList();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_phone:
                ivPhoneClicked((Integer) view.getTag());
                break;
            case R.id.iv_message:
                ivMessageClicked((Integer) view.getTag());
                break;
            case R.id.ll_delete:
                showConfirmDialog(getResources().getString(R.string.dialog_title_normal_tip),getResources().getString(R.string.dialog_message_sure_to_del_msg), (Integer) view.getTag());
                break;
            case R.id.ll_edit:
                showEditListDialog(getResources().getString(R.string.dialog_title_edit_white_list), infos.get((Integer) view.getTag()));
                break;
            case R.id.ll_more:
                setDialogInfos((Integer) view.getTag());
                showMoreDialog(getResources().getString(R.string.dialog_title_more),(Integer) view.getTag());
                break;
            case R.id.ll_add:
                showEditListDialog(getResources().getString(R.string.dialog_title_add_white_list),null);
                break;
            default:
                itemClicked(view);
                break;
        }
    }
    @Override
    protected void doWhileButton3ItemClicked(int position, int innerPosition) {
        switch (innerPosition) {
            case 0://删除
                showConfirmDialog(getResources().getString(R.string.dialog_title_normal_tip),
                        getResources().getString(R.string.dialog_message_sure_to_del_white_list), position);
                break;
            case 1://编辑
                showEditListDialog(getResources().getString(R.string.dialog_title_edit_white_list), infos.get(position));
                break;
            case 2://回短信
                ContactUtil.sendMessageTo(getActivity(),infos.get(position).getNumber());
                break;
            case 3://回拨
                ContactUtil.callTo(getActivity(), infos.get(position).getNumber());
                break;
            case 4://设为黑名单
                boolean del = database.deleteWhiteNumber(infos.get(position).getNumber());
                boolean ins = database.insertBlackNumber(infos.get(position));
                if (del && ins) {
                    makeToast(String.format(getResources().getString(R.string.toast_succeed_to_add_black_list),infos.get(position).getNumber()));
                    flash();
                } else {
                    makeToast(String.format(getResources().getString(R.string.toast_fail_to_add_white_list),infos.get(position).getNumber()));
                    database.insertWhiteNumber(infos.get(position));
                }
                break;
            case 5:
                //取消
                break;
        }
    }

    private void ivPhoneClicked(int index) {
        WhiteBlackNumberInfo info = infos.get(index);
        info.setPhoneCall(!info.isPhoneCall());
        database.updateWhiteNumber(info);
        ViewHolder holder = (ViewHolder) items.get(index).getTag();
        holder.iv_phone
                .setImageResource(info.isPhoneCall() ? phoneActivtId : phoneInactivtId);
    }

    private void ivMessageClicked(int index) {
        WhiteBlackNumberInfo info = infos.get(index);
        info.setSms(!info.isSms());
        database.updateWhiteNumber(info);
        ViewHolder holder = (ViewHolder) items.get(index).getTag();
        holder.iv_message
                .setImageResource(info.isSms() ? messageAcitvityID:messageInactivityID);
    }
    private void setDialogInfos(int position) {
        dialogInfos.clear();
        ItemInfo info1 = new ItemInfo(R.drawable.delete, getResources().getString(R.string.dialog_del));
        ItemInfo info2 = new ItemInfo(R.drawable.edit, getResources().getString(R.string.dialog_edit));
        ItemInfo info3 = new ItemInfo(R.drawable.message, String.format(getResources().getString(R.string.dialog_back_message_to),infos.get(position).getNumber()));
        ItemInfo info4 = new ItemInfo(R.drawable.phone, String.format(getResources().getString(R.string.dialog_back_call_to),infos.get(position).getNumber()));
        ItemInfo info5 = new ItemInfo(R.drawable.blacklist, String.format(getResources().getString(R.string.dialog_set_to_black_list),infos.get(position).getNumber()));
        ItemInfo info6 = new ItemInfo(R.drawable.cancel,getResources().getString(R.string.dialog_cancle));

        dialogInfos.add(info1);
        dialogInfos.add(info2);
        dialogInfos.add(info3);
        dialogInfos.add(info4);
        dialogInfos.add(info5);
        dialogInfos.add(info6);
    }

    @Override
    protected void doWhileButton1OKClicked(int position) {
        if (database.deleteWhiteNumber(infos.get(position).getNumber())) {
            // 删除成功
            makeToast(getResources().getString(R.string.toast_del_succeed));
            flash();
        } else {
            // 删除失败
            makeToast(getResources().getString(R.string.toast_del_fail));
        }

    }

    @Override
    protected void doWhileButton2OKClicked(WhiteBlackNumberInfo info) {
        //编辑
        if (database.updateWhiteNumber(info)) {
            makeToast(getResources().getString(R.string.toast_modify_succeed));
            initList();
        } else {
            makeToast(getResources().getString(R.string.toast_modify_fail));
        }
    }

    @Override
    protected void doWhileListAdd(WhiteBlackNumberInfo info) {
        if (database.insertWhiteNumber(info)) {
            makeToast(getResources().getString(R.string.toast_add_succeed));
            flash();
        } else {
            makeToast(String.format(getResources().getString(R.string.toast_add_fail), info.getNumber()));
        }
    }
}
