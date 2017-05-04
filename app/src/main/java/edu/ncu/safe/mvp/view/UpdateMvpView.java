package edu.ncu.safe.mvp.view;

import edu.ncu.safe.service.UpdateAppService;

/**
* Created by Mr_Yang on 2016/9/16.
        */
public interface UpdateMvpView extends MvpView{
    void checkFailure();
    void checkSame();
    void checkNewVersion(UpdateAppService.VersionBean bean);

    void loadFailure();
    void loadProgress(int progress,int total);
    void loadSucceed(String path);

}
