package edu.ncu.safe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import edu.ncu.safe.R;
import edu.ncu.safe.service.FLoatDesktopWindow;
import edu.ncu.safe.service.FlowsRecordService;

public class RelefeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent) {
		// 首次开启
		Intent floatWindowIntent = new Intent(context, FLoatDesktopWindow.class);
		floatWindowIntent.setAction("edu.ncu.safe.service.showFloatWindod");
		context.startService(floatWindowIntent);

		Intent flowsRecorderIntent = new Intent(context,
				FlowsRecordService.class);
		flowsRecorderIntent.setAction(context.getResources().getString(R.string.action_service_refrash_flows));
		context.startService(flowsRecorderIntent);
	}
}
