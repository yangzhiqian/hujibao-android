package edu.ncu.safe.domain;

import java.util.List;

import edu.ncu.safe.myinterface.ChildItemData;

/**
 * Created by Mr_Yang on 2016/5/28.
 */
public class ELVParentItemInfo {
    private String itemName="";
    private long size=0;
    private boolean isChecked = true;
    private List<? extends ChildItemData> childs ;

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setSize(){
        size = 0;
        for(ChildItemData item:childs){
            if(item.isItemChecked())
                size+=item.getItemSize();
        }
    }
    public long getSize() {
        return size;
    }
    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public List<? extends ChildItemData> getChilds() {
        return childs;
    }

    public void setChilds(List<? extends ChildItemData> childs) {
        this.childs = childs;
        setSize();
    }
}
