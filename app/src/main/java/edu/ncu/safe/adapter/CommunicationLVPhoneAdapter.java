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
	public CommunicationLVPhoneAdapter(Context context,List<InterceptionInfo> infos) {
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
			type += "(黑名单):";
			break;
		case CommunicationDatabaseHelper.NUMBERTYPE_WHITE:
			type += "(白名单):";
			break;
		default:
			type += "(普通):";
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
			showConfirmDialog("护机宝提示","确定要删除该条信息吗",BUTTON1,position);
			break;
			case R.id.ll_recovery:
				showConfirmDialog("护机宝提示", "确定要给号码" + infos.get(position).getNumber()
						+ "打电话吗？", BUTTON2, position);
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
		ItemInfo info2 = new ItemInfo(R.drawable.phone, "回拨");
		ItemInfo info3 = new ItemInfo(R.drawable.message, "给"
				+ infos.get(position).getNumber() + "回复短信");
		ItemInfo info4 = new ItemInfo(R.drawable.whitelist, "添加"
				+ infos.get(position).getNumber() + "为白名单");
		ItemInfo info5 = new ItemInfo(R.drawable.blacklist, "添加"
				+ infos.get(position).getNumber() + "为黑名单");
		ItemInfo info6 = new ItemInfo(R.drawable.cancel, "取消");

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
			makeToast("删除成功！");
		}else{
			makeToast("删除失败！请重试！");
		}
	}

	// 回拨
	@Override
	protected void doWhileButton2OKClicked(int position) {
		ContactUtil.callTo(context,infos.get(position).getNumber());
	}

	@Override
	protected void doWhileButton3ItemClicked(int position, int innerPosition) {
		switch (innerPosition) {
		case 0:
			//删除
			showConfirmDialog("护机宝提示", "确定要删除该条信息吗", BUTTON1, position);
			break;
		case 1:
			//回拨
			showConfirmDialog("护机宝提示", "确定要给号码" + infos.get(position).getNumber()
					+ "打电话吗？", BUTTON2, position);
			break;
		case 2:
			// 回信
			ContactUtil.sendMessageTo(context, infos.get(position).getNumber());
			break;

		case 3:
			showEditList("添加白名单", infos.get(position).getNumber(), TYPE_WHITE);
			// 添加为白名单
			break;
		case 4:
			showEditList("添加黑名单", infos.get(position).getNumber(), TYPE_BLACK);
			// 添加为黑名单
			break;
		case 5:
			// 取消
			break;
		}
	}
}
