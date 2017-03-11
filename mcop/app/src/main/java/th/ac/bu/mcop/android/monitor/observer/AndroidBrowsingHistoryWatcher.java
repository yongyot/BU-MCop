package th.ac.bu.mcop.android.monitor.observer;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import th.ac.bu.mcop.android.monitor.core.AndroidEvent;
import th.ac.bu.mcop.mobile.monitor.core.Event;
import th.ac.bu.mcop.mobile.monitor.core.Reporter;
import th.ac.bu.mcop.mobile.monitor.report.BrowsingHistory;
import th.ac.bu.mcop.mobile.monitor.report.BrowsingUrl;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Browser;

public final class AndroidBrowsingHistoryWatcher extends AndroidWatcher {
	private static final String[] INTENTS = {};
	private static final Uri CONTENT_URI = Uri.parse("content://browser/bookmarks");//Browser.BOOKMARKS_URI;

	// Aggregating & only sending the list until we have more than the following
	// number of items.
	private static final int NUMBER_OF_ITEMS_TO_BE_SENT = 10;
	private static final Map<String, BrowsingUrl> HISTORIES = new HashMap<String, BrowsingUrl>(
			NUMBER_OF_ITEMS_TO_BE_SENT * 2);

	private ContentObserver observer;
	public final int HISTORY_PROJECTION_URL_INDEX = 1;
	public final int HISTORY_PROJECTION_DATE_INDEX = 3;

	public AndroidBrowsingHistoryWatcher(Reporter reporter) {
		setReporter(reporter);
	}

	@Override
	public String[] getIntents() {
		return INTENTS;
	}

	@Override
	public void start(Event event) {
		super.start(event);
		registerContentObserver(((AndroidEvent) event));
	}

	@Override
	public void stop(Event event) {
		super.stop(event);
		Context context = ((AndroidEvent) event).getContext();
		context.getContentResolver().unregisterContentObserver(observer);
		observer = null;
	}

	@Override
	public void service(AndroidEvent event) {
		BrowsingHistory history = readHistory(event.getContext());
		if (history != null) {
			getReporter().report(event, history);
		}
	}

	/**
	 * Register an observer for data changed events.
	 * 
	 * @param event
	 */
	private void registerContentObserver(final AndroidEvent event) {
		if (observer != null) {
			return;
		}
		final Context context = event.getContext();
		observer = new ContentObserver(null) {
			public void onChange(boolean selfChange) {
				service(event);
			}
		};
		context.getContentResolver().registerContentObserver(
				CONTENT_URI, true, observer);
	}

	/**
	 * Read the latest modification from the content database.
	 * 
	 * @see #registerContentObserver(AndroidEvent)
	 * @param context
	 */
	private BrowsingHistory readHistory(Context context) {
		Cursor cursor = context.getContentResolver().query(CONTENT_URI,
				null/*Browser.HISTORY_PROJECTION*/, null, null, null);
		BrowsingHistory history = null;
		if (cursor.moveToLast()) {
			String url = cursor.getString(HISTORY_PROJECTION_URL_INDEX/*Browser.HISTORY_PROJECTION_URL_INDEX*/);
			Date now = new Date(cursor.getLong(HISTORY_PROJECTION_DATE_INDEX/*Browser.HISTORY_PROJECTION_DATE_INDEX*/));
			if (!HISTORIES.containsKey(url)) {
				HISTORIES.put(url, new BrowsingUrl(now, url));
				if (HISTORIES.size() == NUMBER_OF_ITEMS_TO_BE_SENT) {
					history = new BrowsingHistory(HISTORIES.values());
					HISTORIES.clear();
				}
			}
		}
		cursor.close();
		return history;
	}
}