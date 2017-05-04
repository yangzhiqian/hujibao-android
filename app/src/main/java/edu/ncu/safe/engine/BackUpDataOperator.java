package edu.ncu.safe.engine;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import edu.ncu.safe.domainadapter.ITarget;
import edu.ncu.safe.engine.NetDataOperator.BACKUP_TYPE;

/**
 * Created by Yang on 2016/11/2.
 */

public abstract class BackUpDataOperator<DATA, DATAADAPTER extends ITarget> {
    protected Executor executor;
    protected BACKUP_TYPE type;
    protected NetDataOperator operator;
    protected Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            handleCallBack(msg);
            super.handleMessage(msg);
        }
    };
    protected BackUpDataOperator(Context context, BACKUP_TYPE type){
        this.type = type;
        operator = new NetDataOperator(context);
        executor = new ThreadPoolExecutor(3,6,5, TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(100));
    }

    public abstract   boolean handleCallBack(Message msg);
    public abstract  void storeDataToCloud(final DATA data,final  OnStoreDatasResponseListener<DATA> listener) ;
    public abstract  void storeDatasToCloud(final List<DATA> datas, final OnStoreDatasResponseListener<DATA> listener) ;
    public abstract   void loadCloudDatas(final int offset,  final int size,final OnLoadDatasResponseListener<DATAADAPTER> listener);
    public abstract  void deleteDataFromCloud(final int id,final OnDeleteDatasResponseListener listener);
    public abstract  void deleteDatasFromCloud(final List<Integer> ids,final OnDeleteDatasResponseListener listener);

    public interface OnStoreDatasResponseListener<D>{
        void onError(List<D> datas,String message);
        void onFailure(D data,String message);
        void onSucceed(D data,String message);
        void onProgressUpdated(D data,int progress);
    }
    public interface OnLoadDatasResponseListener<D>{
        void onFailure(String message);
        void onDatasGet(List<D> datas,int requestSize);
    }
    public interface OnDeleteDatasResponseListener{
        void onFailure(int id,String errorMessage);
        void onSucceed(int id,String message);
    }
}
