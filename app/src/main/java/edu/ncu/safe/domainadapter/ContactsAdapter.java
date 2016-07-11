package edu.ncu.safe.domainadapter;

import edu.ncu.safe.domain.ContactsInfo;

/**
 * Created by Mr_Yang on 2016/7/2.
 */
public class ContactsAdapter extends ContactsInfo implements ITarget{
    private boolean isSelected = false;
    private boolean isInDownload = false;
    private int id;
    private int percent = 0;
    public ContactsAdapter(ContactsInfo info){
        super(info.getName(),info.getPhoneNumber());
    }
    @Override
    public void setID(int id) {
        this.id=id;
    }

    @Override
    public int getID() {
        return this.id;
    }

    @Override
    public String getIconPath() {
        return null;
    }

    @Override
    public String getTitle() {
        return getName();
    }

    @Override
    public String getNote() {
        return getPhoneNumber();
    }

    @Override
    public String getDateOrSize() {
        return null;
    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public void setSelected(boolean b) {
        isSelected = b;
    }

    @Override
    public boolean isInDownload() {
        return isInDownload;
    }

    @Override
    public void setIsInDownload(boolean b) {
        isInDownload = b;
    }

    @Override
    public int getPercent() {
        return percent;
    }

    @Override
    public void setPercent(int percent) {
        this.percent = percent;
    }
}
