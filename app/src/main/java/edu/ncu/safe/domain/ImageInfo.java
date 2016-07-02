package edu.ncu.safe.domain;

/**
 * Created by Mr_Yang on 2016/7/1.
 */
public class ImageInfo {
    private String path;
    private String name;
    private long lastModified;
    private long size;

    public ImageInfo(String path, String name, long lastModified, long size) {
        this.path = path;
        this.name = name;
        this.lastModified = lastModified;
        this.size = size;
    }

    public ImageInfo() {

    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
