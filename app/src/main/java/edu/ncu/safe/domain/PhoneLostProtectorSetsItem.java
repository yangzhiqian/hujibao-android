package edu.ncu.safe.domain;

/**
 * Created by Mr_Yang on 2016/9/18.
 */
public class PhoneLostProtectorSetsItem {
    private  int iconId;
    private String setName;
    private String setNote;
    private boolean isChecked;
    public PhoneLostProtectorSetsItem(){}
    public PhoneLostProtectorSetsItem(int iconId, String setName, String setNote, boolean isChecked) {
        this.iconId = iconId;
        this.setName = setName;
        this.setNote = setNote;
        this.isChecked = isChecked;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public String getSetName() {
        return setName;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }

    public String getSetNote() {
        return setNote;
    }

    public void setSetNote(String setNote) {
        this.setNote = setNote;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
