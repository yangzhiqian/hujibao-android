package edu.ncu.safe.mvp.view;

import java.util.List;

import edu.ncu.safe.domain.MainGVItemInfo;

/**
 * Created by Mr_Yang on 2016/9/17.
 */
public interface MainGVMvpView extends MvpView {
    void onMainGVItemsGet(List<MainGVItemInfo> mainGVItemInfos);
    void onMainGVItemsUpdate(List<MainGVItemInfo> mainGVItemInfos);
    void onMainGVItemAdded(MainGVItemInfo info);
    void onMainGVItemDel(MainGVItemInfo info);
    void toAntherActivity(Class clazz);
}
