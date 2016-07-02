package edu.ncu.safe.domain;

/**
 * Created by Mr_Yang on 2016/6/25.
 */
public class MainGVItemInfo {
    private int iconR;
    private String title;
    private String note;
    private int color;
    private String invokeMethod;
    private Class clazz;

    public String getInvokeMethod() {
        return invokeMethod;
    }

    public void setInvokeMethod(String invokeMethod) {
        this.invokeMethod = invokeMethod;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public MainGVItemInfo(int iconR, String title, String note, int color,String invokeMethod,Class clazz) {
        this.iconR = iconR;
        this.title = title;
        this.note = note;
        this.color = color;
        this.invokeMethod = invokeMethod;
        this.clazz = clazz;

    }

    public int getIconR() {
        return iconR;
    }

    public void setIconR(int iconR) {
        this.iconR = iconR;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
