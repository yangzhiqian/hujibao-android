package edu.ncu.safe.engine;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import edu.ncu.safe.util.MyLog;

/**
 * Created by Mr_Yang on 2016/7/16.
 */
public class LocationFinder {
    private static final String TAG = "LocationFinder";
    private Context context;
    private List<LocationFinderListener> listeners;
    private LocationManager manager;
    private static LocationFinder finder;
    private LocationFinder(Context context){
        this.context = context;
        manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }
    public static synchronized  LocationFinder getInstance(Context context){
        if(finder==null){
            finder = new LocationFinder(context);
        }
        return finder;
    }

    public void start(){
        // 判断GPS是否正常启动
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //gps未开启
            invokeOnFail("GPS未开启！");
            return;
        }
        try {
            // 为获取地理位置信息时设置查询条件
            String bestProvider = manager.getBestProvider(getCriteria(), true);
            invokeOnStartLocation("开始定位："+bestProvider);
            // 获取位置信息
            // 如果不设置查询要求，getLastKnownLocation方法传人的参数为LocationManager.GPS_PROVIDER
            Location location = manager.getLastKnownLocation(bestProvider);
            invokeOnLastLocationObtained(location);//上次的定位
            // 绑定监听，有4个参数
            // 参数1，设备：有GPS_PROVIDER和NETWORK_PROVIDER两种
            // 参数2，位置信息更新周期，单位毫秒
            // 参数3，位置变化最小距离：当位置距离变化超过此值时，将更新位置信息
            // 参数4，监听
            // 备注：参数2和3，如果参数3不为0，则以参数3为准；参数3为0，则通过时间来定时更新；两者为0，则随时刷新

            // 1秒更新一次，或最小位移变化超过1米更新一次；
            // 注意：此处更新准确度非常低，推荐在service里面启动一个Thread，在run中sleep(10000);然后执行handler.sendMessage(),更新位置
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // 位置监听
    private LocationListener locationListener = new LocationListener() {
        /**
         * 位置信息变化时触发
         */
        public void onLocationChanged(Location location) {
            invokeOnLocationObtained(location);
            manager.removeUpdates(this);
        }
        /**
         * GPS状态变化时触发
         */
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                // GPS状态为可见时
                case LocationProvider.AVAILABLE:
                    MyLog.i(TAG, "当前GPS状态为可见状态");
                    break;
                // GPS状态为服务区外时
                case LocationProvider.OUT_OF_SERVICE:
                    MyLog.i(TAG, "当前GPS状态为服务区外状态");
                    break;
                // GPS状态为暂停服务时
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    MyLog.i(TAG, "当前GPS状态为暂停服务状态");
                    break;
            }
        }
        /**
         * GPS开启时触发
         */
        public void onProviderEnabled(String provider) {
        }
        /**
         * GPS禁用时触发
         */
        public void onProviderDisabled(String provider) {
        }
    };


    /**
     * 返回查询条件
     *
     * @return
     */
    private Criteria getCriteria() {
        Criteria criteria = new Criteria();
        // 设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // 设置是否要求速度
        criteria.setSpeedRequired(false);
        // 设置是否允许运营商收费
        criteria.setCostAllowed(false);
        // 设置是否需要方位信息
        criteria.setBearingRequired(false);
        // 设置是否需要海拔信息
        criteria.setAltitudeRequired(false);
        // 设置对电源的需求
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }

    public void invokeOnFail(String message){
        if(listeners!=null){
            for(LocationFinderListener listener:listeners){
                listener.onFail(message);
            }
        }
    }

    public void invokeOnStartLocation(String message){
        if(listeners!=null){
            for(LocationFinderListener listener:listeners){
                listener.onStartLoacate(message);
            }
        }
    }
    public void invokeOnLocationObtained(Location location){
        if(listeners!=null){
            for(LocationFinderListener listener:listeners){
                listener.onLocationObtained(location);
            }
        }
    }
    public void invokeOnLastLocationObtained(Location location){
        if(listeners!=null){
            for(LocationFinderListener listener:listeners){
                listener.onLastLocationObtained(location);
            }
        }
    }
    public void addLocationFinderListener(LocationFinderListener listener){
        if(listeners==null){
            listeners = new ArrayList<LocationFinderListener>();
        }
        listeners.add(listener);
    }
    private void removeLocationFinderListener(LocationFinderListener listener){
        if(listeners!=null){
            listeners.remove(listener);
        }
    }
    public static interface LocationFinderListener{
        void onFail(String error);
        void onStartLoacate(String type);
        void onLastLocationObtained(Location location);
        void onLocationObtained(Location location);
    }

}
