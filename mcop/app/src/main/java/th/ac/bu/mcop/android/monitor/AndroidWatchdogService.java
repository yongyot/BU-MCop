package th.ac.bu.mcop.android.monitor;

import th.ac.bu.mcop.activities.HomeActivity;
import th.ac.bu.mcop.android.monitor.core.AndroidEvent;
import th.ac.bu.mcop.android.monitor.core.AndroidWatchdog;
import th.ac.bu.mcop.android.monitor.observer.AndroidWatcher;
import th.ac.bu.mcop.mobile.monitor.core.Watcher;
import th.ac.bu.mcop.utils.Settings;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.util.Log;

/**
 * This Android service is spawned when an {@link AndroidWatcher} want to
 * execute its processing in a service. Therefore, developers must register
 * this service to the Application node in the AndroidManifest.xml file.
 * <p>
 * &lt;service android:name="th.ac.bu.science.mit.allappstatscollector.android.monitor.AndroidWatchdogService"
 * /&gt;
 * </p>
 * There are only 2 types of invocation of this service. One is in the receiver
 * initialization and one is when an interested event occurs. The first one
 * allows all the registered watchers to start running (monitoring) whereas the
 * second one is triggered by a watcher to delegate the event processing from
 * a short-live broadcast receiver to a long-running service. Please noted that
 * the watcher object is only available in the second one and we can retrieve it
 * in the intent extra/bundle.
 *
 */
public final class AndroidWatchdogService extends Service {
	private static final String EXTRA_KEY_OBJECT_HASH_CODE = "object.hashcode";

	/**
	 * Start the watchdog service to handle this event.
	 * 
	 * @param context
	 * @param intent
	 */
	public static void start(Context context, Intent intent, Watcher watcher) {
		Intent service = new Intent(context, AndroidWatchdogService.class);
		Bundle extras = intent.getExtras();
		if (extras != null) {
			service.putExtras(extras);
		}
		service.putExtra(EXTRA_KEY_OBJECT_HASH_CODE, watcher.hashCode());
		context.startService(service);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		//setForeground(true);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		AndroidEvent event = new AndroidEvent(this, this, intent);
		Bundle extras = intent.getExtras();
		Object key = extras != null ? extras.get(EXTRA_KEY_OBJECT_HASH_CODE) : null;
		AndroidWatchdog watchdog = HomeActivity.getWatchdog();
		if (key != null) {
			// The service looks up the appropriate watcher by using
			// information in the given intent and delegating the
			// processing to this watcher.
			AndroidWatcher watcher = watchdog.getWatcher((Integer) key);
			watcher.service(event);
		}
		else {
			// Start all watchers
			watchdog.start(event);
		}

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
