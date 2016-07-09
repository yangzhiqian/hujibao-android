package edu.ncu.safe.domainadapter;

import edu.ncu.safe.domain.SmsInfo;
import edu.ncu.safe.util.FormatDate;

/**
 * Created by Mr_Yang on 2016/7/2.
 */
public class MessageAdapter extends SmsInfo implements ITarget {
    private boolean isSelected=false;
    private boolean isInDownload = false;
    private int percent = 0;
    public MessageAdapter(SmsInfo info){
        super(info.getAddress(),info.getDate(),info.getType(),info.getBody());
    }
    @Override
    public String getIconPath() {
        return null;
    }

    @Override
    public String getTitle() {
        return (getType() == IN ? "接收:" : "发送：") + getAddress();
    }

    @Override
    public String getNote() {
        return getBody();
    }

    @Override
    public String getDateOrSize() {
        return FormatDate.getFormatDate(getDate());
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
