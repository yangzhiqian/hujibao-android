package edu.ncu.safe.myadapter;

import android.app.Dialog;
import android.content.Context;
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

public abstract class MyLIstViewBaseAdapter extends BaseAdapter {
	protected static final int TYPE_WHITE = 0;
	protected static final int TYPE_BLACK = 1;
	public  static final int BUTTON1 = 1;
	public  static final int BUTTON2 = 2;

	protected Context context;//上下文
	protected List<OnDataChangedListener> listeners = new ArrayList<OnDataChangedListener>();//观察者模式

	protected List<ItemInfo> dialogInfos;//对话框中listview的主体内容
	private MyDialogListViewAdapter myDialogListViewAdapter;//对话框listview的适配器
	protected CommunicationDatabase database ;
	public MyLIstViewBaseAdapter(Context context) {
		this.context = context;
		dialogInfos = new ArrayList<ItemInfo>();
		database = new CommunicationDatabase(context);
	}

	/**
	 * 当用户点击第一个按钮弹出后的对话框的确定按钮时调用，由对应的adapter完成相应的逻辑处理
	 * @param position  //拦截信息/电话的信息listview表中的position
	 */
	protected abstract void doWhileButton1OKClicked(int position);
	/**
	 * 当用户点击第二个按钮弹出后的对话框的确定按钮时调用，由对应的adapter完成相应的逻辑处理
	 * @param position  //拦截信息/电话的信息listview表中的position
	 */
	protected abstract void doWhileButton2OKClicked(int position);
	/**
	 * 当用户点击第三个按钮弹出后的对话框的某个项目后，由对应的adapter完成相应的逻辑处理
	 * @param position  //拦截信息/电话的信息listview表中的position
	 * @param innerPosition //对话框中的listview的position
	 */
	protected abstract void doWhileButton3ItemClicked(int position,
			int innerPosition);


	/**
	 * 显示确定取消按钮的对话框
	 * @param title 		对话框的题目
	 * @param message  	提示的内容
	 * @param button      按钮的标号  第一个按钮为1  。。。
	 * @param position		对话框给那一条信息的提示
	 */
	protected void showConfirmDialog(String title,String message, final int button,
			final int position) {
		final Dialog dialog = new Dialog(context, R.style.MyDialog);
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_confirm, null);
		TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
		tv_title.setText(title);
		TextView tv_message = (TextView) view.findViewById(R.id.tv_message);
		tv_message.setText(message);
		LinearLayout ll_yes = (LinearLayout) view.findViewById(R.id.ll_YES);
		LinearLayout ll_no = (LinearLayout) view.findViewById(R.id.ll_NO);
		ll_yes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (button == BUTTON1) {
					doWhileButton1OKClicked(position);
				}
				if (button == BUTTON2) {
					doWhileButton2OKClicked(position);
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
	 * 当点击更多时弹出对话框
	 * @param position   对话框对应的信息position
	 */
	protected void showMoreDialog(final int position) {
		final Dialog dialog = new Dialog(context, R.style.MyDialog);
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_more,
				null);
		ListView lv_more = (ListView) view.findViewById(R.id.lv_more);

		myDialogListViewAdapter = new MyDialogListViewAdapter();
		lv_more.setAdapter(myDialogListViewAdapter);
		lv_more.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> viewGroup, View view, int posit,
					long id) {
				doWhileButton3ItemClicked(position,posit);
				dialog.dismiss();
			}
		});
		dialog.setContentView(view);
		dialog.show();
	}
	
	protected void showEditList(String title, String number,final int type) {
		final Dialog dialog = new Dialog(context, R.style.MyDialog);
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_whiteblacklist_number, null);

		TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
		tv_title.setText(title);
		final EditText et_number = (EditText) view.findViewById(R.id.et_number);
		et_number.setText(number);
		final EditText et_note = (EditText) view.findViewById(R.id.et_note);
		final CheckBox cb_message = (CheckBox) view.findViewById(R.id.cb_msg);
		final CheckBox cb_phone = (CheckBox) view.findViewById(R.id.cb_phone);
		LinearLayout ll_yes = (LinearLayout) view.findViewById(R.id.ll_YES);
		LinearLayout ll_no = (LinearLayout) view.findViewById(R.id.ll_NO);

		ll_yes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 添加
				String number = et_number.getText().toString().trim();
				if ("".equals(number)) {
					Toast.makeText(context, "号码不能为空", 1);
					return;
				}
				String note = et_note.getText().toString().trim();
				boolean isPhoneCall = cb_phone.isChecked();
				boolean isSms = cb_message.isChecked();
				WhiteBlackNumberInfo info = new WhiteBlackNumberInfo(number,
						note, isSms, isPhoneCall);


				if(type==TYPE_WHITE){
					if(database.insertWhiteNumber(info)) {
						Toast.makeText(context, "号码" + info.getNumber() + "已添加到白名单中", 1).show();
					}else{
						Toast.makeText(context, "添加失败，可能号码" + info.getNumber() + "已经在黑白白名单中", 1).show();
					}
				}
				if(type==TYPE_BLACK){
					if(database.insertBlackNumber(info)) {
						Toast.makeText(context, "号码" + info.getNumber() + "已添加到黑名单中", 1).show();
					} else{
						Toast.makeText(context, "添加失败，可能号码" + info.getNumber() + "已经在黑白白名单中", 1).show();
					}
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


	public void addOnDataChangedListener(OnDataChangedListener listener) {
		listeners.add(listener);
	}

	protected void dataChanged() {
		for (OnDataChangedListener listener : listeners) {
			listener.dataChanged();
		}
	}

	public interface OnDataChangedListener {
		public void dataChanged();
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
				convertView = LayoutInflater.from(context).inflate(R.layout.item_listview_dialog,
						null);
				holder.iv = (ImageView) convertView.findViewById(R.id.iv);
				holder.tv = (TextView) convertView.findViewById(R.id.tv);
				convertView.setTag(holder);
			} else {
				holder = (MyDialogViewHolder) convertView.getTag();
			}

			holder.iv.setImageResource(dialogInfos.get(position)
					.getResourse_id());
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


	//listview的item被点击后的隐藏view的显示
	private LinearLayout ll_pre = null;
	public void onItemClick( LinearLayout ll_now ,ListView lv) {
		if (ll_pre != null) {
			ll_pre.setVisibility(View.GONE);
		}
		ll_now.setVisibility(View.VISIBLE);
		ll_pre=ll_now;
		//adjustListViewHeight(lv,ll_now);
	}

	public void hideTheHideView(){
		if(ll_pre!=null){
			ll_pre.setVisibility(View.GONE);
			ll_pre = null;
		}
	}

	private void adjustListViewHeight(ListView lv,LinearLayout ll_hide) {
		BaseAdapter adapter = (BaseAdapter) lv.getAdapter();
		ll_hide.measure(0,0);
		int total = ll_hide.getMeasuredHeight();
		makeToast(total+"");
		for (int i = 0; i < adapter.getCount(); i++) {
			View view = adapter.getView(i, null, lv);
			view.measure(0, 0);
			total += view.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = lv.getLayoutParams();
		params.height = total
				+ (lv.getDividerHeight() * (adapter.getCount() - 1))+100;
		lv.setLayoutParams(params);
	}

	public void makeToast(String message){
		Toast.makeText(context,message,Toast.LENGTH_LONG);
	}
}
