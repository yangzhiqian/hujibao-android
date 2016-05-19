package edu.ncu.safe.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetTypeUtil {

	public static final int UNKNOW = 0;
	public static final int MOBILE_GPRS = 1;
	public static final int WIFI = 2;

	public static int getCurrentNetType(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info != null) {
			// 有联网的信息
			if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
				return MOBILE_GPRS;
			}
			if (info.getType() == ConnectivityManager.TYPE_WIFI) {
				return WIFI;
			}
		}
		return UNKNOW;
	}
}
