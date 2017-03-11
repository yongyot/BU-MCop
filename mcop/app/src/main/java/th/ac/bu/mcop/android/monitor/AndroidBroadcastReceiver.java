package th.ac.bu.mcop.android.monitor;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import th.ac.bu.mcop.http.core.Logger;

/**
 * This dummy receiver is for receiving some special Android events such
 * as booting, user interaction, phone state changing... and trigger the
 * initialization of the Application object. Developers should define this
 * receiver in the AndroidManifest.xml in advance.<br/>
 * <pre>
 * &lt;receiver android:name="th.ac.bu.science.mit.allappstatscollector.android.monitor.AndroidBroadcastReceiver"&gt;
 * 	&lt;intent-filter&gt;
 * 		&lt;action android:name="android.intent.action.BOOT_COMPLETED" /&gt;
 * 		&lt;action android:name="android.intent.action.USER_PRESENT" /&gt;
 * 		&lt;action android:name="android.intent.action.PHONE_STATE" /&gt;
 * 	&lt;/intent-filter&gt;
 * &lt;/receiver&gt;
 * </pre>
 *
 */
public class AndroidBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		ComponentName receiver = new ComponentName(context,
				AndroidBroadcastReceiver.class);
		PackageManager pm = context.getPackageManager();
		pm.setComponentEnabledSetting(receiver,
				PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
				PackageManager.DONT_KILL_APP);
		Logger.getDefault().debug("Never called again??");
	}
}
