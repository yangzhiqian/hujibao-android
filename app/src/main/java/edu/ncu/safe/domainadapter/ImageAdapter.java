package edu.ncu.safe.domainadapter;

import edu.ncu.safe.domain.ImageInfo;
import edu.ncu.safe.util.FlowsFormartUtil;
import edu.ncu.safe.util.FormatDate;

/**
 * Created by Mr_Yang on 2016/7/2.
 */
public class ImageAdapter extends ImageInfo implements ITarget {
    private boolean isSucceed = false;
    private boolean isInDownload = false;
    private int id;
    private int percent = 0;
    public ImageAdapter(ImageInfo info){
        super(info.getPath(),info.getName(),info.getLastModified(),info.getSize());
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
        return getPath();
    }

    @Override
    public String getTitle() {
        return getName();
    }

    @Override
    public String getNote() {
        return FormatDate.getFormatDate(getLastModified());
    }

    @Override
    public String getDateOrSize() {
        return FlowsFormartUtil.toFlowsFormart(getSize());
    }

    @Override
    public boolean isSelected() {
        return isSucceed;
    }

    @Override
    public void setSelected(boolean b) {
        isSucceed =b;
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
