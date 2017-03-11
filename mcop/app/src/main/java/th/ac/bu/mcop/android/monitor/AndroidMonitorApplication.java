package th.ac.bu.mcop.android.monitor;

import io.realm.Realm;
import th.ac.bu.mcop.android.monitor.core.AndroidEvent;
import th.ac.bu.mcop.android.monitor.core.AndroidWatchdog;
import th.ac.bu.mcop.android.monitor.observer.AndroidWatcher;
import th.ac.bu.mcop.http.core.Logger;
import th.ac.bu.mcop.mobile.monitor.core.Event;
import th.ac.bu.mcop.mobile.monitor.core.Observer;
import th.ac.bu.mcop.mobile.monitor.core.Watchdog;
import th.ac.bu.mcop.utils.Settings;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * <p>This class implements {@link BroadcastReceiver} to receive broadcast events
 * from Android device. Client must extend this receiver and add a receiver
 * entry to the AndroidManifest.xml file. Please noted that in the intent-filter
 * area, BOOT_COMPLETED & USER_PRESENT intent are necessary to initialize the
 * receiver as early as possible.</p>
 * For example:
 * <pre>
 * &lt;receiver android:name="th.ac.bu.science.mit.allappstatscollector.android.monitor.MyAndroidBroadcastReceiver"&gt;
 *     &lt;intent-filter&gt;
 *         &lt;action android:name="android.intent.action.BOOT_COMPLETED" /&gt;
 *         &lt;action android:name="android.intent.action.USER_PRESENT" /&gt;
 *         &lt;action android:name="android.intent.action.NEW_OUTGOING_CALL" /&gt;
 *         &lt;action android:name="android.intent.action.PHONE_STATE" /&gt;
 *         &lt;action android:name="android.provider.Telephony.SMS_RECEIVED" /&gt;
 *     &lt;/intent-filter&gt;
 * &lt;/receiver&gt;
 * </pre>
 */
public abstract class AndroidMonitorApplication extends Application {
	/**
	 * A {@link Watchdog} may be accessed from multiple places for retrieving
	 * {@link Observer} objects.
	 */
	private AndroidWatchdog watchdog;
	private BroadcastReceiver receiver;

	protected abstract void initialize(Watchdog watchdog, IntentFilter filter);

	public AndroidWatchdog getWatchdog() {
		return watchdog;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(Settings.TAG, "onCreate AndroidMonitorApplication");
		Realm.init(this);
		watchdog = new AndroidWatchdog();
		IntentFilter filter = new IntentFilter();
		initialize(watchdog, filter);
		
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				AndroidEvent event = new AndroidEvent(this, context, intent);
				watchdog.watch(event);
			}
		};
		registerReceiver(receiver, filter);
		startService(new Intent(this, AndroidWatchdogService.class));
		
		Logger.getDefault().debug("Initialized successfully!");
	}

	protected final void register(AndroidWatcher watcher, IntentFilter filter) {
		watchdog.register(watcher.getObserver());
		Event event = new AndroidEvent(this, this, null);
		for (String action : watcher.getIntents()) {
			filter.addAction(action);
			watcher.start(event);
		}
	}
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}
	
	@Override
	public void onTerminate() {
		watchdog.clear();
		unregisterReceiver(receiver);
		super.onTerminate();
	}
}
