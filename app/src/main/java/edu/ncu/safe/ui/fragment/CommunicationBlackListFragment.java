package edu.ncu.safe.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.ncu.safe.R;
import edu.ncu.safe.domain.WhiteBlackNumberInfo;
import edu.ncu.safe.myadapter.MyLIstViewFragment;
import edu.ncu.safe.util.ContactUtil;

public class CommunicationBlackListFragment extends MyLIstViewFragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//初始化显示的图片
		phoneActivtId = R.drawable.phonered;
		phoneInactivtId = R.drawable.phonegintercepteray;
		messageAcitvityID = R.drawable.messagered;
		messageInactivityID = R.drawable.messageinterceptegray;
		layout_id = R.layout.fragment_blacklist;
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	protected  void flash(){
		infos = database.queryBlackNumberInfos();
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
			showConfirmDialog(getContext().getResources().getString(R.string.dialog_title_normal_tip),
					getContext().getResources().getString(R.string.dialog_message_sure_to_del_black_list), (Integer) view.getTag());
			break;
		case R.id.ll_edit:
			showEditListDialog(getContext().getResources().getString(R.string.dialog_title_edit_black_list), infos.get((Integer) view.getTag()));
			break;
		case R.id.ll_more:
			setDialogInfos((Integer) view.getTag());
			showMoreDialog(getContext().getResources().getString(R.string.dialog_title_more),(Integer) view.getTag());
			break;
		case R.id.ll_add:
			showEditListDialog(getContext().getResources().getString(R.string.dialog_title_add_black_list),null);
			break;
		default:
			itemClicked(view);
			break;
		}
	}

	@Override
	protected void doWhileButton3ItemClicked(int position, int innerPosition) {
		switch (innerPosition) {
			case 0:
				//删除
				showConfirmDialog(getContext().getResources().getString(R.string.dialog_title_normal_tip),
						getContext().getResources().getString(R.string.dialog_message_sure_to_del_black_list),position);
				break;
			case 1:
				//编辑
				showEditListDialog(getContext().getResources().getString(R.string.dialog_title_edit_black_list), infos.get(position));
				break;
			case 2:
				//回短信
				ContactUtil.sendMessageTo(getActivity(), infos.get(position).getNumber());
				break;
			case 3:
				//回拨
				ContactUtil.callTo(getActivity(), infos.get(position).getNumber());
				break;
			case 4:
				//改为白名单
				boolean del = database.deleteBlackNumber(infos.get(position).getNumber());
				boolean ins = database.insertWhiteNumber(infos.get(position));
				if (del && ins) {
					makeToast(String.format(getResources().getString(R.string.toast_succeed_to_add_white_list), infos.get(position).getNumber()));
					flash();
				}else {
					makeToast(String.format(getResources().getString(R.string.toast_fail_to_add_white_list),infos.get(position).getNumber()));
					database.insertBlackNumber(infos.get(position));
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
		database.updateBlackNumber(info);
		ViewHolder holder = (ViewHolder) items.get(index).getTag();
		holder.iv_phone
				.setImageResource(info.isPhoneCall() ? R.drawable.phonered
						: R.drawable.phonegintercepteray);
	}

	private void ivMessageClicked(int index) {
		WhiteBlackNumberInfo info = infos.get(index);
		info.setSms(!info.isSms());
		database.updateBlackNumber(info);
		ViewHolder holder = (ViewHolder) items.get(index).getTag();
		holder.iv_message
				.setImageResource(info.isSms() ? R.drawable.messagered
						: R.drawable.messageinterceptegray);
	}
	private void setDialogInfos(int position) {
		dialogInfos.clear();
		ItemInfo info1 = new ItemInfo(R.drawable.delete, getContext().getResources().getString(R.string.dialog_del));
		ItemInfo info2 = new ItemInfo(R.drawable.edit, getContext().getResources().getString(R.string.dialog_edit));
		ItemInfo info3 = new ItemInfo(R.drawable.message, String.format(getContext().getResources().getString(R.string.dialog_back_message_to),infos.get(position).getNumber()));
		ItemInfo info4 = new ItemInfo(R.drawable.phone, String.format(getContext().getResources().getString(R.string.dialog_back_call_to),infos.get(position).getNumber()));
		ItemInfo info5 = new ItemInfo(R.drawable.whitelist, String.format(getContext().getResources().getString(R.string.dialog_change_to_white_list),infos.get(position).getNumber()));
		ItemInfo info6 = new ItemInfo(R.drawable.cancel, getContext().getResources().getString(R.string.dialog_cancle));

		dialogInfos.add(info1);
		dialogInfos.add(info2);
		dialogInfos.add(info3);
		dialogInfos.add(info4);
		dialogInfos.add(info5);
		dialogInfos.add(info6);
	}

	@Override
	protected void doWhileButton1OKClicked(int position) {
		if (database.deleteBlackNumber(infos.get(position).getNumber())) {
			// 删除成功
			makeToast(getContext().getResources().getString(R.string.toast_del_succeed));
			flash();
		} else {
			// 删除失败
			makeToast(getContext().getResources().getString(R.string.toast_del_fail));
		}
	}

	@Override
	protected void doWhileButton2OKClicked(WhiteBlackNumberInfo info) {
		//编辑
		if (database.updateBlackNumber(info)) {
			makeToast(getContext().getResources().getString(R.string.toast_modify_succeed));
			initList();
		} else {
			makeToast(getContext().getResources().getString(R.string.toast_modify_fail));
		}
	}
	@Override
	protected void doWhileListAdd(WhiteBlackNumberInfo info) {
		if (database.insertBlackNumber(info)) {
			makeToast(getContext().getResources().getString(R.string.toast_add_succeed));
			flash();
		} else {
			makeToast(String.format(getContext().getResources().getString(R.string.toast_add_fail),info.getNumber()));
		}
	}
}
