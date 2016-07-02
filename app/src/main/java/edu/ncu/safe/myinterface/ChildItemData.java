package edu.ncu.safe.myinterface;

import android.graphics.drawable.Drawable;

/**
 * Created by Mr_Yang on 2016/5/28.
 */
public abstract class ChildItemData {
    private boolean isChecked = true;
    abstract public Drawable getItemIcon();
    abstract public String getItemName();
    abstract public String getItemNote();
    abstract public long getItemSize();
     public boolean isItemChecked(){
        return isChecked;
    }
     public void setItemChecked(boolean isChecked){
         this.isChecked = isChecked;
     }
}
