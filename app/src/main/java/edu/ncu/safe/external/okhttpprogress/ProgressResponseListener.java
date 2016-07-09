package edu.ncu.safe.external.okhttpprogress;

/**
 * Created by Mr_Yang on 2016/7/5.
 */
public interface ProgressResponseListener {
    void onResponseProgress(long bytesRead, long contentLength, boolean done);
}