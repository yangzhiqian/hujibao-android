package edu.ncu.safe.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.ncu.safe.R;
import edu.ncu.safe.customerview.MyProgressBar;
import edu.ncu.safe.domainadapter.ITarget;
import edu.ncu.safe.engine.DataLoader;
import edu.ncu.safe.util.BitmapUtil;

/**
 * Created by Mr_Yang on 2016/6/1.
 */
public class BackupLVAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private Context context;
    private List<ITarget> infos;
    private boolean isShowMultiChoice = false;

    public BackupLVAdapter(Context context) {
        this(new ArrayList<ITarget>(), context);
    }

    public BackupLVAdapter(List<ITarget> infos, Context context) {
        this.infos = infos;
        this.context = context;
    }

    public void setInfos(List<ITarget> infos) {
        if(infos==null){
            infos=new ArrayList<ITarget>();
        }
        this.infos = infos;
    }

    public List<ITarget> getInfos() {
        return infos;
    }

    public boolean isShowMultiChoice() {
        return isShowMultiChoice;
    }

    public void setIsShowMultiChoice(boolean isShowMultiChoice) {
        this.isShowMultiChoice = isShowMultiChoice;
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
        ViewHolder holder;
        if (view == null) {
            view = View.inflate(context, R.layout.item_listview_backup, null);
            holder = new ViewHolder();
            holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
            holder.tv_title = (TextView) view.findViewById(R.id.tv_title);
            holder.tv_note = (TextView) view.findViewById(R.id.tv_note);
            holder.tv_size = (TextView) view.findViewById(R.id.tv_size);
            holder.iv_showPopup = (ImageView) view.findViewById(R.id.iv_showpopup);
            holder.cb_check = (CheckBox) view.findViewById(R.id.cb_check);
            holder.ll_content = (LinearLayout) view.findViewById(R.id.ll_content);
            holder.mpb_downloadProgress = (MyProgressBar) view.findViewById(R.id.mpb_downloadprogress);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (infos.get(position).getIconPath() != null) {
            holder.iv_icon.setVisibility(View.VISIBLE);
            holder.iv_icon.setImageResource(R.drawable.appicon);
            BitmapUtil.loadImageToImageView(context,infos.get(position).getIconPath(), DataLoader.TYPE_SMALL, holder.iv_icon,null);
        } else {
            holder.iv_icon.setVisibility(View.GONE);
        }
        holder.tv_title.setText(infos.get(position).getTitle());
        holder.tv_note.setText(infos.get(position).getNote());
        if(infos.get(position).getDateOrSize()!=null){
            holder.tv_size.setVisibility(View.VISIBLE);
            holder.tv_size.setText(infos.get(position).getDateOrSize());
        }else{
            holder.tv_size.setVisibility(View.GONE);
        }
        holder.iv_showPopup.setImageResource(R.drawable.close);
        holder.iv_showPopup.setTag(R.id.tag_position, position);
        holder.iv_showPopup.setTag(R.id.tag_view, view);
        holder.iv_showPopup.setOnClickListener(this);

        if (isShowMultiChoice) {
            holder.iv_showPopup.setVisibility(View.GONE);
            holder.cb_check.setVisibility(View.VISIBLE);
            holder.cb_check.setOnCheckedChangeListener(null);
            holder.cb_check.setChecked(infos.get(position).isSelected());
            holder.cb_check.setTag(R.id.tag_position, position);
            holder.cb_check.setTag(R.id.tag_view, view);
            holder.cb_check.setOnCheckedChangeListener(this);
        } else {
            holder.iv_showPopup.setVisibility(View.VISIBLE);
            holder.cb_check.setVisibility(View.GONE);
        }

        if (infos.get(position).isInDownload()) {
            holder.mpb_downloadProgress.setVisibility(View.VISIBLE);
            holder.mpb_downloadProgress.setTag(R.id.tag_position, position);
            holder.mpb_downloadProgress.setTag(R.id.tag_view, view);
            holder.mpb_downloadProgress.setOnClickListener(this);
        } else {
            holder.mpb_downloadProgress.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void onClick(View view) {
        int position = (int) view.getTag(R.id.tag_position);
        View parent = (View) view.getTag(R.id.tag_view);
        switch (view.getId()) {
            case R.id.iv_showpopup:
                if (listener != null) {
                    listener.onShowPopupClicked(parent, view, position, infos.get(position));
                }
                break;
            case R.id.mpb_downloadprogress:
                if (listener != null) {
                    listener.onDownloadProgressBarClicked(parent, position, infos.get(position));
                }
                break;
        }
    }
//    private void showPopup(final View parent,final View view,final int position){
//        ((ImageView)view).setImageResource(R.drawable.expand);
//
//        View contentView = View.inflate(context, R.layout.popupwindow_backup_tiem, null);
//        TextView tv_recovery = (TextView) contentView.findViewById(R.id.tv_recovery);
//        TextView tv_delete = (TextView) contentView.findViewById(R.id.tv_delete);
//        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
//
//        final PopupWindow popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        //popupWindow设置animation一定要在show之前
//        popupWindow.setTouchable(true);
//        popupWindow.setFocusable(true);
//        popupWindow.setOutsideTouchable(true);
//        //一定要设置背景，否则无法自动消失
//        Drawable background = context.getResources().getDrawable(R.drawable.popupbgright);
//        popupWindow.setBackgroundDrawable(background);
//        view.measure(0, 0);
//        popupWindow.showAsDropDown(view,
//                -1 * popupWindow.getContentView().getMeasuredWidth() - 30,
//                -1 * (popupWindow.getContentView().getMeasuredHeight() + view.getHeight()) / 2 - 10);
//
//        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                ((ImageView)view).setImageResource(R.drawable.close);
//            }
//        });
//        tv_recovery.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(listener!=null) {
//                    listener.onRecoveryClick(parent, position, infos.get(position));
//                }
//                popupWindow.dismiss();
//            }
//        });
//        tv_delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(listener!=null) {
//                    listener.onDeleteClick(parent, position, infos.get(position));
//                }
//                popupWindow.dismiss();
//            }
//        });
//    }

    public void setSelectedAll(boolean b) {
        for (ITarget info : infos) {
            if (!info.isInDownload()) {
                info.setSelected(b);
            }
        }
        notifyDataSetChanged();
    }


    public class ViewHolder {
        public ImageView iv_icon;
        public TextView tv_title;
        public TextView tv_note;
        public TextView tv_size;
        public ImageView iv_showPopup;
        public CheckBox cb_check;
        public LinearLayout ll_content;
        public MyProgressBar mpb_downloadProgress;
    }

    public void setItemInDownloading(View view,int position,boolean b){
        infos.get(position).setIsInDownload(b);
        ((ViewHolder)view.getTag()).mpb_downloadProgress.setVisibility(b?View.VISIBLE:View.GONE);
    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int position = (int) buttonView.getTag(R.id.tag_position);
        infos.get(position).setSelected(isChecked);
        if (listener != null) {
            View view = (View) buttonView.getTag(R.id.tag_view);
            listener.onCheckBoxCheckedChanged(view, position, infos.get(position), isChecked);
        }
    }


    public List<ITarget> getCheckInfo() {
        List<ITarget> checkInfos = new ArrayList<ITarget>();
        for (ITarget info : infos) {
            if (info.isSelected()) {
                checkInfos.add(info);
            }
        }
        return checkInfos;
    }

    //观察者模式
    private OnAdapterEventListener listener;

    public OnAdapterEventListener getOnAdapterEventListener() {
        return listener;
    }

    public void setOnAdapterEventListener(OnAdapterEventListener listener) {
        this.listener = listener;
    }

    public interface OnAdapterEventListener {
//        public void onRecoveryClick(View parent,int position,BackupInfo info);
//        public void onDeleteClick(View parent,int position,BackupInfo info);

        public void onShowPopupClicked(View parent, View view, int position, ITarget info);

        public void onDownloadProgressBarClicked(View parent, int position, ITarget info);

        public void onCheckBoxCheckedChanged(View parent, int position, ITarget data, boolean isChecked);
    }
}
