package edu.ncu.safe.mvp.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import edu.ncu.safe.MyApplication;
import edu.ncu.safe.db.dao.FlowsDatabase;
import edu.ncu.safe.mvp.view.FlowsCalibrationMvpView;
import edu.ncu.safe.util.FormatDate;

/**
 * Created by Mr_Yang on 2016/9/22.
 */
public class FlowsCalibrationPresenter {
    private FlowsCalibrationMvpView view;
    private FlowsDatabase database;

    public FlowsCalibrationPresenter(@NonNull FlowsCalibrationMvpView view,@NonNull Context context) {
        this.view = view;
        database = new FlowsDatabase(context);
    }

    public void init(){
        SharedPreferences sp = MyApplication.getSharedPreferences();
        long total = sp.getLong(MyApplication.SP_LONG_TOTAL_FLOWS, 0);
        if (total <= 0) {
            // 当前没有设置数据
            view.onCurrentMonthFlowsRemainedGet(0);
            view.onTotalFlowsGet(0);
        } else {
            long offset = sp.getLong(MyApplication.SP_LONG_DB_OFFSET, 0);
            // 该处不用检测offset的更新时间，应为在上一个界面一定会更新
            long dbFlows = database.queryCurrentMonthTotalFlows();
            long used = dbFlows + offset;
            long remain = total - used;
            view.onCurrentMonthFlowsRemainedGet(remain);
            view.onTotalFlowsGet(total);
        }
    }

    public void refresh(){
        init();
    }

    /**
     * 用户正确输入总流量后的回调，用于从新设定流量数据
     * @param strRemain  用户输入的剩余流量字符串
     */
    public void resetFlowsRemain(String strRemain) {
        long remain  = (long)(Float.parseFloat(strRemain)*1024*1024);
        view.onCurrentMonthFlowsRemainedGet(remain);//跟新
        // 更新显示数据
        SharedPreferences sp = MyApplication.getSharedPreferences();
        long total = sp.getLong(MyApplication.SP_LONG_TOTAL_FLOWS, 0);
        if (total == 0) {
            // 没有设置过数据流量的值
            long db = database.queryCurrentMonthTotalFlows();
            total = db + remain;
            view.onTotalFlowsGet(total);
            //保存到sp中
            SharedPreferences.Editor editor = sp.edit();
            editor.putLong(MyApplication.SP_LONG_TOTAL_FLOWS, total);
            //未有误差
            editor.putLong(MyApplication.SP_LONG_DB_OFFSET, 0);
            editor.putInt(
                    MyApplication.SP_INT_OFFSET_UPDATE,
                    FormatDate.getCurrentFormatIntDate());
            editor.apply();
        } else {
            // 已经设置过了
            long db = database.queryCurrentMonthTotalFlows();
            long offset = total - remain - db;

            SharedPreferences.Editor editor = sp.edit();
            //计算误差值并保存
            editor.putLong(MyApplication.SP_LONG_DB_OFFSET, offset);
            editor.putInt(
                    MyApplication.SP_INT_OFFSET_UPDATE,
                    FormatDate.getCurrentFormatIntDate());
            editor.apply();
        }
    }

    /**
     * 用户正确输入总流量后的回调，用于从新设定流量数据
     * @param strTotal  用户输入的总流量字符串
     */
    public void resetTotalFlows(String strTotal) {
        long total  = (long)(Float.parseFloat(strTotal)*1024*1024);
        // 更新显示数据
        view.onTotalFlowsGet(total);
        //已经使用了
        long db = database.queryCurrentMonthTotalFlows();
        //误差值
        SharedPreferences sp = MyApplication.getSharedPreferences();
        long offset = sp.getLong(MyApplication.SP_LONG_DB_OFFSET, 0);
        long remain = total - db - offset;
        view.onCurrentMonthFlowsRemainedGet(remain);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(MyApplication.SP_LONG_TOTAL_FLOWS, total);
        editor.putInt(MyApplication.SP_INT_OFFSET_UPDATE, FormatDate.getCurrentFormatIntDate());
        editor.apply();
    }
}
