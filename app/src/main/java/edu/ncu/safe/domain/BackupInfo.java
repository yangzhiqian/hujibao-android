package edu.ncu.safe.domain;

import java.io.Serializable;

/**
 * Created by Mr_Yang on 2016/6/1.
 */
public class BackupInfo implements Serializable{
    public static final int MESSAGE = 0;
    public static final int PICTURE= 1;
    public static final int CONTACTS =2;
    //隐式信息
    private int id;//信息的id(message、picture、conta cts三张表中的id值)
    private int type;//信息的类型
    //显示信息
    private String pic;
    private String title;
    private String note;
    private long size;
    private boolean isChecked = false;
    //使用与照片的下载
    private boolean isInDownload = false;
    private float downloadPercent = 0;


    private Object extra;

    public BackupInfo() {
    }

    public BackupInfo(int id,int type,String pic, String title, String note, long size) {
        this.id = id;
        this.type = type;
        this.pic = pic;
        this.title = title;
        this.note = note;
        this.size = size;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
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

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isInDownload() {
        return isInDownload;
    }

    public void setIsInDownload(boolean isInDownload) {
        this.isInDownload = isInDownload;
    }

    public float getDownloadPercent() {
        return downloadPercent;
    }

    public void setDownloadPercent(float downloadPercent) {
        this.downloadPercent = downloadPercent;
    }

    public Object getExtra() {
        return extra;
    }

    public void setExtra(Object extra) {
        this.extra = extra;
    }
}
