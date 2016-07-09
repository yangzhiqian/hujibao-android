package edu.ncu.safe.external.okhttpprogress;

/**
 * Created by Mr_Yang on 2016/7/5.
 */
public interface ProgressRequestListener {
    void onRequestProgress(long bytesWritten,long contentLength, boolean done);
}
