package edu.ncu.safe.myadapter;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.ncu.safe.R;
import edu.ncu.safe.db.dao.CommunicationDatabase;
import edu.ncu.safe.domain.WhiteBlackNumberInfo;
import edu.ncu.safe.util.BitmapUtil;

public abstract class MyLIstViewFragment extends Fragment implements OnClickListener{
	protected int phoneActivtId = R.drawable.phonered;
	protected int phoneInactivtId = R.drawable.phonegintercepteray;
	protected int messageAcitvityID = R.drawable.messagered;
	protected  int messageInactivityID = R.drawable.messageinterceptegray;

	protected TextView tv_numbers;
	protected LinearLayout ll_add;
	protected LinearLayout ll_list;

	protected CommunicationDatabase database;
	protected List<View> items;

	protected List<WhiteBlackNumberInfo> infos;
	protected List<ItemInfo> dialogInfos = new ArrayList<ItemInfo>();
	private MyDialogListViewAdapter myDialogListViewAdapter;
	private LinearLayout ll_pre = null;

	protected abstract void doWhileButton1OKClicked(int position);

	protected abstract void doWhileButton2OKClicked(WhiteBlackNumberInfo info);

	protected abstract void doWhileButton3ItemClicked(int position,
			int innerPosition);
	protected abstract void doWhileListAdd(WhiteBlackNumberInfo info);

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_whitelist, null);
		tv_numbers = (TextView) view.findViewById(R.id.tv_msg_numbers);
		ll_add = (LinearLayout) view.findViewById(R.id.ll_add);
		ll_list = (LinearLayout) view.findViewById(R.id.ll_list);

		database = new CommunicationDatabase(getActivity());
		ll_add.setOnClickListener(this);
		return view;
	}

	protected void initList() {
		tv_numbers.setText(infos.size() + "");
		if (items == null) {
			items = new ArrayList<View>();
		} else {
			for (View v : items) {
				ll_list.removeView(v);
			}
			items.clear();
		}
		ViewHolder holder;
		int index = 0;
		for (WhiteBlackNumberInfo info : infos) {
			View view = LayoutInflater.from(getActivity()).inflate(
					R.layout.item_mylist_whiteblacknumber, null);
			holder = new ViewHolder();
			// 查找控件
			holder.tv_address = (TextView) view.findViewById(R.id.tv_address);
			holder.iv_phone = (ImageView) view.findViewById(R.id.iv_phone);
			holder.iv_message = (ImageView) view.findViewById(R.id.iv_message);
			holder.ll_hideView = (LinearLayout) view
					.findViewById(R.id.ll_hideview);
			holder.ll_delete = (LinearLayout) view.findViewById(R.id.ll_delete);
			holder.ll_edit = (LinearLayout) view.findViewById(R.id.ll_edit);
			holder.ll_more = (LinearLayout) view.findViewById(R.id.ll_more);

			// 设置数据
			holder.tv_address.setText(infos.get(index).getNote() + ":"
					+ infos.get(index).getNumber());
			holder.iv_phone
					.setImageResource(infos.get(index).isPhoneCall() ? phoneActivtId:phoneInactivtId);
			holder.iv_message
					.setImageResource(infos.get(index).isSms() ? messageAcitvityID:messageInactivityID);

			// 添加事件
			view.setOnClickListener(this);
			holder.iv_phone.setOnClickListener(this);
			holder.iv_message.setOnClickListener(this);
			holder.ll_delete.setOnClickListener(this);
			holder.ll_edit.setOnClickListener(this);
			holder.ll_more.setOnClickListener(this);
			// 设置下标
			holder.iv_phone.setTag(index);
			holder.iv_message.setTag(index);
			holder.ll_delete.setTag(index);
			holder.ll_edit.setTag(index);
			holder.ll_more.setTag(index);

			view.setTag(holder);

			items.add(view);
			ll_list.addView(view);
			index++;
		}
	}

	/**
	 * 显示确认取消按钮
	 * @param title 对话框标题
	 * @param message  对话框消息
	 * @param  position 对话框对应的item
	 */
	protected void showConfirmDialog(String title,String message ,final int position) {
		final Dialog dialog = new Dialog(getActivity(), R.style.MyDialog);
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.dialog_confirm, null);
		TextView tv_title = (TextView) view.findViewById(R.id.tv_title	);
		tv_title.setText(title);
		TextView tv_message = (TextView) view.findViewById(R.id.tv_message);
		tv_message.setText(message);
		LinearLayout ll_yes = (LinearLayout) view.findViewById(R.id.ll_YES);
		LinearLayout ll_no = (LinearLayout) view.findViewById(R.id.ll_NO);
		ll_yes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doWhileButton1OKClicked(position);
				dialog.dismiss();
			}
		});
		ll_no.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.setContentView(view);
		dialog.show();
	}

	/**
	 * 添加或修改黑白名单表的对话框
	 * @param title 对话框的标题
	 * @param info  如果info为null则表示添加，否则为编辑
	 */
	protected void showEditListDialog(String title,final WhiteBlackNumberInfo info){
		final Dialog dialog = new Dialog(getActivity(), R.style.MyDialog);
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.dialog_whiteblacklist_number, null);
		
		TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
		tv_title.setText(title);
		final EditText et_number = (EditText) view.findViewById(R.id.et_number);
		final EditText et_note = (EditText) view.findViewById(R.id.et_note);
		final CheckBox cb_message = (CheckBox) view.findViewById(R.id.cb_msg);
		final CheckBox cb_phone = (CheckBox) view.findViewById(R.id.cb_phone);
		LinearLayout ll_yes = (LinearLayout) view.findViewById(R.id.ll_YES);
		LinearLayout ll_no = (LinearLayout) view.findViewById(R.id.ll_NO);
		
		if(info!=null){
			et_number.setText(info.getNumber());
			et_number.setEnabled(false);
			et_note.setText(info.getNote());
			cb_phone.setChecked(info.isPhoneCall());
			cb_message.setChecked(info.isSms());
		}
		
		ll_yes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(info==null){
					//添加
					String number = et_number.getText().toString().trim();
					if("".equals(number)){
						makeToast("号码不能为空！");
						return;
					}
					String note = et_note.getText().toString().trim();
					boolean isPhoneCall = cb_phone.isChecked();
					boolean isSms = cb_message.isChecked();
					WhiteBlackNumberInfo info = new WhiteBlackNumberInfo(number, note, isSms, isPhoneCall);
					doWhileListAdd(info);
				}else{
					String note = et_note.getText().toString().trim();
					boolean isPhoneCall = cb_phone.isChecked();
					boolean isSms = cb_message.isChecked();
					info.setNote(note);
					info.setPhoneCall(isPhoneCall);
					info.setSms(isSms);
					doWhileButton2OKClicked(info);
				}
				dialog.dismiss();
			}
		});
		ll_no.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.setContentView(view);
		dialog.show();
	}

	/**
	 * 显示更多的对话框
	 * @param title   对话框的标题
	 * @param position  对话框对应的item
	 */
	protected void showMoreDialog(String title,final int position) {
		final Dialog dialog = new Dialog(getActivity(), R.style.MyDialog);
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.dialog_more, null);

		TextView tv_title = (TextView) view.findViewById(R.id.tv_title	);
		tv_title.setText(title);
		ListView lv_more = (ListView) view.findViewById(R.id.lv_more);

		myDialogListViewAdapter = new MyDialogListViewAdapter();
		lv_more.setAdapter(myDialogListViewAdapter);
		lv_more.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> viewGroup, View view,
					int posit, long id) {
				doWhileButton3ItemClicked(position, posit);
				dialog.dismiss();
			}
		});
		dialog.setContentView(view);
		dialog.show();
	}

	class MyDialogListViewAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return dialogInfos.size();
		}

		@Override
		public Object getItem(int position) {
			return dialogInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			MyDialogViewHolder holder;
			if (convertView == null) {
				holder = new MyDialogViewHolder();
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.item_listview_dialog, null);
				holder.iv = (ImageView) convertView.findViewById(R.id.iv);
				holder.tv = (TextView) convertView.findViewById(R.id.tv);
				convertView.setTag(holder);
			} else {
				holder = (MyDialogViewHolder) convertView.getTag();
			}

			Bitmap bitmap = BitmapUtil.readBitMap(getActivity(),dialogInfos.get(position)
					.getResourse_id());
			Drawable drawable = new BitmapDrawable(bitmap);
			holder.iv.setImageDrawable(drawable);

			holder.tv.setText(dialogInfos.get(position).getTitle());
			return convertView;
		}

		class MyDialogViewHolder {
			public ImageView iv;
			public TextView tv;
		}
	}

	public static class ItemInfo {
		private int resourse_id;
		private String title;

		public ItemInfo(int id, String title) {
			this.resourse_id = id;
			this.title = title;
		}

		public int getResourse_id() {
			return resourse_id;
		}

		public void setResourse_id(int resourse_id) {
			this.resourse_id = resourse_id;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}
	}

	public class ViewHolder {
		public TextView tv_address;
		public ImageView iv_phone;
		public ImageView iv_message;

		public LinearLayout ll_hideView;
		public LinearLayout ll_delete;
		public LinearLayout ll_edit;
		public LinearLayout ll_more;
	}

	/**
	 * 点击item的事件处理
	 * @param view  点击的item
	 */
	protected void itemClicked(View view) {
		if (ll_pre != null) {
			ll_pre.setVisibility(View.GONE);
		}
		LinearLayout ll_now = ((ViewHolder) (view.getTag())).ll_hideView;
		if (ll_pre == ll_now) {
			ll_pre = null;
			return;
		}
		ll_now.setVisibility(View.VISIBLE);
		ll_pre = ll_now;
	}
	protected void makeToast(String message){
		Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
	}
	
}
