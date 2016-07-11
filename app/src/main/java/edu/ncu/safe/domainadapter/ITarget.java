package edu.ncu.safe.domainadapter;

/**
 * Created by Mr_Yang on 2016/7/2.
 */
public interface ITarget {
    void setID(int id);
    int getID();
    String getIconPath();
    String getTitle();
    String getNote();
    String getDateOrSize();
    boolean isSelected();
    void setSelected(boolean b);
    boolean isInDownload();
    void setIsInDownload(boolean b);
    int getPercent();
    void setPercent(int percent);
}
