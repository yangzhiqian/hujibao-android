package edu.ncu.safe.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.ncu.safe.R;
import edu.ncu.safe.domain.WhiteBlackNumberInfo;
import edu.ncu.safe.myadapter.MyLIstViewFragment;
import edu.ncu.safe.util.ContactUtil;
import edu.ncu.safe.util.MyLog;

public class CommunicationBlackListFragment extends MyLIstViewFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		//初始化显示的图片
		phoneActivtId = R.drawable.phonered;
		phoneInactivtId = R.drawable.phonegintercepteray;
		messageAcitvityID = R.drawable.messagered;
		messageInactivityID = R.drawable.messageinterceptegray;
		View view = super.onCreateView(inflater,container,savedInstanceState);
		flash();
		return view;
	}

	private void flash(){
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
			showConfirmDialog("护机宝提示", "确定要删除该黑名单吗？", (Integer) view.getTag());
			break;
		case R.id.ll_edit:
			showEditListDialog("修改黑名单", infos.get((Integer) view.getTag()));
			break;
		case R.id.ll_more:
			setDialogInfos((Integer) view.getTag());
			showMoreDialog("更多",(Integer) view.getTag());
			break;
		case R.id.ll_add:
			showEditListDialog("添加黑名单",null);
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
				showConfirmDialog("护机宝提示", "确定要删除该黑名单吗？", position);
				break;
			case 1:
				//编辑
				showEditListDialog("修改黑名单", infos.get(position));
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
					makeToast("号码" + infos.get(position).getNumber() + "已成功到黑白名单表中");
					initList();
				}else {
					makeToast("添加失败");
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
		ItemInfo info1 = new ItemInfo(R.drawable.delete, "删除");
		ItemInfo info2 = new ItemInfo(R.drawable.edit, "编辑");
		ItemInfo info3 = new ItemInfo(R.drawable.message, "给"
				+ infos.get(position).getNumber() + "回复短信");
		ItemInfo info4 = new ItemInfo(R.drawable.phone, "给"
				+ infos.get(position).getNumber() + "回拨电话");
		ItemInfo info5 = new ItemInfo(R.drawable.whitelist, "更改"
				+ infos.get(position).getNumber() + "为白名单");
		ItemInfo info6 = new ItemInfo(R.drawable.cancel, "取消");

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
			makeToast("号码" + infos.get(position).getNumber() + "已成功移除黑名单！");
			ll_list.removeView(items.get(position));
		} else {
			// 删除失败
			makeToast("号码" + infos.get(position).getNumber() + "移除失败！");
		}
	}

	@Override
	protected void doWhileButton2OKClicked(WhiteBlackNumberInfo info) {
		//编辑
		if (database.updateBlackNumber(info)) {
			makeToast("修改成功");
			initList();
		} else {
			makeToast("修改失败,原因未知");
		}
	}
	@Override
	protected void doWhileListAdd(WhiteBlackNumberInfo info) {
		if (database.insertBlackNumber(info)) {
			makeToast("添加成功");
			flash();
		} else {
			makeToast("添加失败,号码" + info.getNumber() + "可能已在黑白名单表中");
		}
	}

	private void logi(String message){
		MyLog.i("CommunicationBlackListFragment", message);
	}
}
