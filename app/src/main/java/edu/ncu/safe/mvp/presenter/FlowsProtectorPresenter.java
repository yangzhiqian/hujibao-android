package edu.ncu.safe.mvp.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import edu.ncu.safe.MyApplication;
import edu.ncu.safe.db.dao.FlowsDatabase;
import edu.ncu.safe.mvp.view.FlowsProtectorMvpView;

/**
 * Created by Mr_Yang on 2016/9/22.
 */
public class FlowsProtectorPresenter {
    private FlowsProtectorMvpView view;
    private FlowsDatabase database;

    public FlowsProtectorPresenter(@NonNull  FlowsProtectorMvpView view, @NonNull  Context context) {
        this.view = view;
        this.database =  new FlowsDatabase(context);
    }

    public void init(){
        SharedPreferences sp = MyApplication.getSharedPreferences();
        //获取流量计算的偏差值
        long dbFlowsOffset = sp.getLong(MyApplication.SP_LONG_DB_OFFSET, 0);
        //查询本月记录的流量使用量+偏差就是正确的本月流量
        long monthFlows = database.queryCurrentMonthTotalFlows() + dbFlowsOffset;
        //本日的流量不设置偏差值，直接获取
        long dayFlows = database.queryCurrentDayTotalFlows();
        monthFlows = monthFlows <= 0 ? 0 : monthFlows;
        dayFlows = dayFlows <= 0 ? 0 : dayFlows;
        view.onCurrentMonthFlowsGet(monthFlows);
        view.onCurrentDayFlowsGet(dayFlows);
        //获取用户设置的流量上线值
        long total = sp.getLong(MyApplication.SP_LONG_TOTAL_FLOWS,0);
        if(total<=0) {
            //0表示用户没有设置流量上线
            view.onFlowsPercentGet(0);
        }else{
            view.onFlowsPercentGet((int) (0.5+monthFlows * 100 / total));
        }
    }
    public void refresh(){
        init();
    }
}
