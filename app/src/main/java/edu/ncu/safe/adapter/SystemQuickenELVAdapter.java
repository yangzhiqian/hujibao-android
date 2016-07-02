package edu.ncu.safe.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.ncu.safe.R;
import edu.ncu.safe.domain.CacheInfo;
import edu.ncu.safe.domain.ELVParentItemInfo;
import edu.ncu.safe.domain.RunningApplicationInfo;
import edu.ncu.safe.myinterface.ChildItemData;
import edu.ncu.safe.util.FlowsFormartUtil;

/**
 * Created by Mr_Yang on 2016/5/27.
 */
public class SystemQuickenELVAdapter extends BaseExpandableListAdapter implements CompoundButton.OnCheckedChangeListener {
    private static final int GROUP = 0;
    private static final int CHILD = 1;
    private Context context;
    private List<ELVParentItemInfo> infos;
    private OnItemCheckedListener listener;

    public SystemQuickenELVAdapter(Context context) {
        this(context,new ArrayList<ELVParentItemInfo>());

    }
    public SystemQuickenELVAdapter(Context context, List<ELVParentItemInfo> infos) {
        this.context = context;
        this.infos = infos;
    }
    public void setOnItemCheckedListener(OnItemCheckedListener listener){
        this.listener = listener;
    }

    public List<ELVParentItemInfo> getInfos() {
        return infos;
    }

    public void setInfos(List<ELVParentItemInfo> infos) {
        this.infos = infos;
    }

    @Override
    public int getGroupCount() {
        return infos.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return infos.get(groupPosition).getChilds().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return infos.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return infos.get(groupPosition).getChilds().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return formatID(groupPosition+1,0);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return formatID(groupPosition+1,childPosition+1);
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View view, ViewGroup parent) {
        ViewHolderParent holder;
        if(view==null){
            holder = new ViewHolderParent();
            view = View.inflate(context, R.layout.item_expandablelistview_systemquicken,null);
            holder.tv_parentName = (TextView) view.findViewById(R.id.tv_parentname);
            holder.tv_parentSize = (TextView) view.findViewById(R.id.tv_parentsize);
            holder.cb_parentCheck = (CheckBox) view.findViewById(R.id.cb_parentcheck);

            view.setTag(holder);
        }else{
            holder = (ViewHolderParent) view.getTag();
        }

        holder.cb_parentCheck.setOnCheckedChangeListener(null);
        holder.tv_parentName.setText(infos.get(groupPosition).getItemName());
        holder.tv_parentSize.setText(FlowsFormartUtil.toFlowsFormart(infos.get(groupPosition).getSize()));
        holder.cb_parentCheck.setChecked(infos.get(groupPosition).isChecked());

        holder.cb_parentCheck.setTag(R.id.tag_group, groupPosition);
        holder.cb_parentCheck.setTag(R.id.tag_child,-1);
        holder.cb_parentCheck.setOnCheckedChangeListener(this);
        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {
        ViewHolderChild holder;
        if(view==null){
            holder = new ViewHolderChild();
            view = View.inflate(context,R.layout.item_expandablelistview_systemquicken_child,null);
            holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
            holder.tv_childName = (TextView) view.findViewById(R.id.tv_childname);
            holder.tv_childNote = (TextView) view.findViewById(R.id.tv_childnote);
            holder.tv_childSize = (TextView) view.findViewById(R.id.tv_childsize);
            holder.cb_childCheck = (CheckBox) view.findViewById(R.id.cb_childcheck);

            view.setTag(holder);
        }else{
            holder = (ViewHolderChild) view.getTag();
        }


        holder.cb_childCheck.setOnCheckedChangeListener(null);
        holder.iv_icon.setImageDrawable(infos.get(groupPosition).getChilds().get(childPosition).getItemIcon());
        holder.tv_childName.setText(infos.get(groupPosition).getChilds().get(childPosition).getItemName());
        holder.tv_childNote.setText(infos.get(groupPosition).getChilds().get(childPosition).getItemNote());
        holder.tv_childSize.setText(FlowsFormartUtil.toFlowsFormart(infos.get(groupPosition).getChilds().get(childPosition).getItemSize()));
        holder.cb_childCheck.setChecked(infos.get(groupPosition).getChilds().get(childPosition).isItemChecked());

        holder.cb_childCheck.setTag(R.id.tag_group, groupPosition);
        holder.cb_childCheck.setTag(R.id.tag_child,childPosition);
        holder.cb_childCheck.setOnCheckedChangeListener(this);
        return view;
    }

    class ViewHolderParent{

        TextView tv_parentName;
        TextView tv_parentSize;
        CheckBox cb_parentCheck;
    }
    class ViewHolderChild{
        ImageView iv_icon;
        TextView tv_childName;
        TextView tv_childNote;
        TextView tv_childSize;
        CheckBox cb_childCheck;
    }
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int group = (Integer) buttonView.getTag(R.id.tag_group);
        int child = (Integer) buttonView.getTag(R.id.tag_child);
        if(child==-1){
            //点击的是父checkbox
            List<? extends ChildItemData> childs = infos.get(group).getChilds();
            for(ChildItemData data:childs){
                data.setItemChecked(isChecked);
            }
            infos.get(group).setIsChecked(isChecked);
            infos.get(group).setSize();
        }else{
            //只点击了子条目的checkbox
            infos.get(group).getChilds().get(child).setItemChecked(isChecked);
            infos.get(group).setSize();

            List<? extends ChildItemData> childs = infos.get(group).getChilds();
            boolean hasItemChecked = false;
            for(ChildItemData data:childs){
                if(data.isItemChecked()){
                    hasItemChecked = true;
                }
            }
            infos.get(group).setIsChecked(hasItemChecked);
        }
        notifyDataSetChanged();

        List<String> appItems = getCheckAppProcessNames();
        List<String> rubbishItems = getCheckRubbishNames();
        if(listener!=null){
            listener.OnItemChecked(appItems.size(),rubbishItems.size());
        }
    }

    public List<String> getCheckAppProcessNames(){
        List<String> res = new ArrayList<String>();

        for(ChildItemData childItemData:infos.get(0).getChilds()){
            if(childItemData.isItemChecked()){
                res.add(((RunningApplicationInfo)childItemData).getProcessName());
            }
        }
        return res;
    }

    public List<String> getCheckRubbishNames(){
        List<String> res = new ArrayList<String>();

        for(ChildItemData childItemData:infos.get(1).getChilds()){
            if(childItemData.isItemChecked()){
                res.add(((CacheInfo)childItemData).getPackageName());
            }
        }
        return res;
    }


    private int formatID(int groupPosition,int childPosition){
        return groupPosition << 16  +childPosition;
    }

    private int getGroupPosition(int formatID){
        return formatID>>>16;
    }

    private int getChildPosition(int formatID){
        return (formatID<<16)>>>16;
    }

    public interface OnItemCheckedListener{
        public void   OnItemChecked(int... parms);
    }
}
