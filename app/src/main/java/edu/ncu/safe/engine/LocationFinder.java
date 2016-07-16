package edu.ncu.safe.engine;

/**
 * Created by Mr_Yang on 2016/7/15.
 */
public class LocationFinder {
//    private Context context;
//    private static LocationFinder finder;
//
//    private LocationFinder(Context context) {
//        this.context = context;
//    }
//
//    public static synchronized LocationFinder getInstance(Context context) {
//        if (finder == null) {
//            finder = new LocationFinder(context);
//        }
//        return finder;
//    }
//
//
//    private void getLocation() {
//        // 获取位置管理服务
//        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//        // 查找到服务信息
//        Criteria criteria = new Criteria();
//        criteria.setAccuracy(Criteria.ACCURACY_FINE);// 高精度
//        criteria.setAltitudeRequired(false);
//        criteria.setBearingRequired(false);
//        criteria.setCostAllowed(true);
//        criteria.setPowerRequirement(Criteria.POWER_LOW); // 低功耗
//        String provider = locationManager.getBestProvider(criteria, true); // 获取GPS信息
//
//        if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            //有定位权限
//            Location location = locationManager.getLastKnownLocation(provider); // 通过GPS获取位置             updateToNewLocation(location);
//        }
//        //设置监听*器，自动更新的最小时间为间隔N秒(1秒为1*1000，这样写主要为了方便)或最小位移变化超过N米
//    //    locationManager.requestLocationUpdates(provider, 100 * 1000, 500, locationListener);
//    }
//
//
//    /**
//     * 检查是否开启gps
//     */
//    private boolean openGPSSettings() {
//        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//        if (manager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
//            //GPS模块正常
//            return true;
//        }
//        //gps未开启
//        Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
//        context.startActivity(intent); //此为设置完成后返回到获取界面           }
//        return false;
//    }
}
