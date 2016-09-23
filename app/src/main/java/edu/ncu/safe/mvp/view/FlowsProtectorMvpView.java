package edu.ncu.safe.mvp.view;

/**
 * Created by Mr_Yang on 2016/9/22.
 */
public interface FlowsProtectorMvpView extends MvpView {

    void onCurrentMonthFlowsGet(long flows);
    void onCurrentDayFlowsGet(long flows);
    void onFlowsPercentGet(float percent);
}
