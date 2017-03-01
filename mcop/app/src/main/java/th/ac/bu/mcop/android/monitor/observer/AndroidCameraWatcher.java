package th.ac.bu.mcop.android.monitor.observer;

import java.io.File;

import th.ac.bu.mcop.android.monitor.core.AndroidEvent;
import th.ac.bu.mcop.mobile.monitor.core.Event;
import th.ac.bu.mcop.mobile.monitor.core.Reporter;
import th.ac.bu.mcop.mobile.monitor.report.Media;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;

public class AndroidCameraWatcher extends AndroidWatcher {
	private static final String[] INTENTS = { };
	
	public AndroidCameraWatcher(Reporter reporter) {
		setReporter(reporter);
	}
	
	@Override
	public String[] getIntents() {
		return INTENTS;
	}
	
	@Override
	public void start(Event dc) {
		super.start(dc);
		registerContentObserver((AndroidEvent)dc);
	}

	/**
	 * This will register 2 media store observers, one for image and the other
	 * for video capturing.
	 * 
	 * @param dc
	 */
	private void registerContentObserver(final AndroidEvent dc) {
		registerContentObserver(dc, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		registerContentObserver(dc, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
	}
	
	/**
	 * Register an observer on the given {@link Uri}.
	 * 
	 * @param dc
	 * @param uri
	 */
	private void registerContentObserver(final AndroidEvent dc, final Uri uri) {
		final Context context = dc.getContext();
		ContentObserver observer = new ContentObserver(null) {
			public void onChange(boolean selfChange) {
				Media media = readFromMediaStore(context, uri);
				if (media != null) {
					getReporter().report(dc, media);
				}
			}
		};
		context.getContentResolver().registerContentObserver(uri, false, observer);
	}

	private Media readFromMediaStore(Context context, Uri uri) {
		Cursor cursor = context.getContentResolver().query(
				uri, null, null, null, "date_added DESC");
		Media media = null;
		if (cursor.moveToNext()) {
			int dataColumn = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
			String filePath = cursor.getString(dataColumn);
			int mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaColumns.MIME_TYPE);
			String mimeType = cursor.getString(mimeTypeColumn);
			media = new Media(new File(filePath), mimeType);
		}
		cursor.close();
		return media;
	}
}
