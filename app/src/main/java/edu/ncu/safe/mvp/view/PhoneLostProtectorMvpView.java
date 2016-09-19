package edu.ncu.safe.mvp.view;

import java.util.List;

import edu.ncu.safe.domain.PhoneLostProtectorSetsItem;

/**
 * Created by Mr_Yang on 2016/9/18.
 */
public interface PhoneLostProtectorMvpView  extends MvpView{
    void onProtectOpened();

    void onProtectClosed();

    void onSetsItemGet(List<PhoneLostProtectorSetsItem> itemsData);

    void onNoSimCardFind();

    void onNumbersModified();

    void onDeviceAdminInactivitied();

    void onPasswordModified();
}
