package edu.ncu.safe.engine;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LoadLocation {
	private LoadLocation() {
	};

	private static List<OnLoacationChangedListener> listeners = new ArrayList<OnLoacationChangedListener>();
	private static LoadLocation loadLocation;
	public static MyLocationListener myLocationListener = new MyLocationListener();
	static LocationManager manager;
	public static synchronized LoadLocation getInstance(Context context) {
		if (loadLocation == null) {
			manager = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);
			// String provider = getBestProvider(manager);
			if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
						0, myLocationListener);
			}
			if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,
						0, myLocationListener);
			}
			if (manager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
				manager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0,
						0, myLocationListener);
			}
		}
		return loadLocation;
	}

	static class MyLocationListener implements LocationListener {
		public void onLocationChanged(Location location) {
			for (OnLoacationChangedListener listener : listeners) {
				listener.locationChanged(location);
			}
			manager.removeUpdates(myLocationListener);
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onProviderDisabled(String provider) {
		}
	}

	private static String getBestProvider(LocationManager manager) {
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);// 精确
		criteria.setAltitudeRequired(false);// 海拔
		criteria.setBearingAccuracy(Criteria.ACCURACY_HIGH);
		criteria.setCostAllowed(true);// 可付费
		criteria.setSpeedRequired(false);
		return manager.getBestProvider(criteria, true);
	}

	public void addOnLocationChangeListener(OnLoacationChangedListener listener) {
		listeners.add(listener);
	}

	public void removeOnLocationChangeListener(
			OnLoacationChangedListener listener) {
		listeners.remove(listener);
	}

	public interface OnLoacationChangedListener {
		void locationChanged(Location location);
	}
}
