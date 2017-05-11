package edu.ncu.safe.domain;

import java.util.List;

import edu.ncu.safe.base.bean.SystemQuickerItemInfo;

/**
 * Created by Mr_Yang on 2016/5/28.
 */
public class ELVParentItemInfo {
    private String itemName="";
    private long size=0;
    private boolean isChecked = true;
    private List<? extends SystemQuickerItemInfo> childs ;

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setSize(){
        size = 0;
        for(SystemQuickerItemInfo item:childs){
            if(item.isChecked())
                size+=item.getCacheSize();
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

    public List<? extends SystemQuickerItemInfo> getChilds() {
        return childs;
    }

    public void setChilds(List<? extends SystemQuickerItemInfo> childs) {
        this.childs = childs;
        setSize();
    }
}
