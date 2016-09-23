package edu.ncu.safe.mvp.view;

/**
 * Created by Mr_Yang on 2016/9/22.
 */
public interface FlowsCalibrationMvpView extends MvpView {

    void onCurrentMonthFlowsRemainedGet(long flows);
    void onTotalFlowsGet(long flows);
}
