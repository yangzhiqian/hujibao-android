package edu.ncu.safe.mvp.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import edu.ncu.safe.mvp.view.UpdateMvpView;
import edu.ncu.safe.service.UpdateAppService;

/**
 * Created by Mr_Yang on 2016/9/16.<br/>
 * 软件更新的presenter
 */
public class UpdateAppPresenter {
    private UpdateMvpView view;
    private Context context;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action){
                case UpdateAppService.ACTION_CHECKVERSION_FAILURE:
                    view.checkFailure();
                    break;
                case UpdateAppService.ACTION_CHECKVERSION_SAME:
                    view.checkSame();
                    break;
                case UpdateAppService.ACTION_CHECKVERSION_NEWVERSION:
                    view.checkNewVersion((UpdateAppService.VersionBean) intent.getSerializableExtra("VersionBean"));
                    break;

                case UpdateAppService.ACTION_LOADAPK_FAILURE:
                    view.loadFailure();
                    break;
                case UpdateAppService.ACTION_LOADAPK_PROGRESS:
                    view.loadProgress(intent.getIntExtra("progress",0),intent.getIntExtra("totalLength",0));
                    break;
                case UpdateAppService.ACTION_LOADAPK_SUCCEED:
                    view.loadSucceed(intent.getStringExtra("path"));
                    break;
            }
        }
    };
    public UpdateAppPresenter(UpdateMvpView view,Context context) {
        this.view = view;
        this.context = context;
        IntentFilter filter = new IntentFilter();
        filter.addAction(UpdateAppService.ACTION_CHECKVERSION_FAILURE);
        filter.addAction(UpdateAppService.ACTION_CHECKVERSION_SAME);
        filter.addAction(UpdateAppService.ACTION_CHECKVERSION_NEWVERSION);
        filter.addAction(UpdateAppService.ACTION_LOADAPK_FAILURE);
        filter.addAction(UpdateAppService.ACTION_LOADAPK_PROGRESS);
        filter.addAction(UpdateAppService.ACTION_LOADAPK_SUCCEED);
        this.context.registerReceiver(receiver,filter);
    }

    public void startCheck(){
        // 检查是否有新版本信息，有则弹出升级对话框
        UpdateAppService.startCheck(context);
    }

    public void startLoad(UpdateAppService.VersionBean newVersion){
        UpdateAppService.startLoadApk(context,newVersion);
    }

    /**
     * 用于注销服务
     */
    public void destory(){
        context.unregisterReceiver(receiver);
    }
}
