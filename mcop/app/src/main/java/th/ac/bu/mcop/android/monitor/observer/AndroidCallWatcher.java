package th.ac.bu.mcop.android.monitor.observer;

import java.util.Date;

import th.ac.bu.mcop.android.monitor.core.AndroidEvent;
import th.ac.bu.mcop.http.core.Logger;
import th.ac.bu.mcop.mobile.monitor.core.Event;
import th.ac.bu.mcop.mobile.monitor.core.Reporter;
import th.ac.bu.mcop.mobile.monitor.report.Call;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.provider.CallLog;
import android.telephony.TelephonyManager;

public class AndroidCallWatcher extends AndroidWatcher {

	private static final String[] INTENTS = {TelephonyManager.ACTION_PHONE_STATE_CHANGED};

	private String phoneNumber;

	private Handler handler;
	
	public AndroidCallWatcher(Reporter reporter) {
		setReporter(reporter);
	}
	
	@Override
	public String[] getIntents() {
		return INTENTS;
	}

	@Override
	public void start(Event dc) {
		super.start(dc);

		Context context = ((AndroidEvent)dc).getContext();
		TelephonyManager telephony = (TelephonyManager) context.getSystemService(
				Context.TELEPHONY_SERVICE);
		// This will create a handler in service thread.
		handler = new Handler();
		phoneNumber = telephony.getLine1Number();
	}
	
	@Override
	public void service(final AndroidEvent dc) {
		String phoneState = dc.getIntent().getExtras().getString(TelephonyManager.EXTRA_STATE);
		// An idle state means the call has just been ended. We should proceed
		// to extract calling information from call log.
		if (TelephonyManager.EXTRA_STATE_IDLE.equals(phoneState)) {
			final Context context = dc.getContext();
			// To ensure the data is fully updated & stable to be correctly
			// read, we defer the processing a little bit, 500ms...
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					Logger.getDefault().debug("Handler Thread: " + Thread.currentThread());
					Call call = getCall(context);
					if (call != null) {
						getReporter().report(dc, call);
					}
				}
			}, 500);
		}
	}

	/**
	 * Get the most recently called data from the call log.
	 * 
	 * @param context
	 * @return
	 */
	private Call getCall(Context context) {
		Cursor cursor = context.getContentResolver().query(
				CallLog.Calls.CONTENT_URI,
				null, null, null,
				CallLog.Calls.DEFAULT_SORT_ORDER
		); 
		
		int numberColumn = cursor.getColumnIndex(CallLog.Calls.NUMBER);
		int typeColumn = cursor.getColumnIndex(CallLog.Calls.TYPE);
		int dateColumn = cursor.getColumnIndex(CallLog.Calls.DATE);
		int durationColumn = cursor.getColumnIndex(CallLog.Calls.DURATION);

		Call call = null;
		if (cursor.moveToFirst()) {
			String number = cursor.getString(numberColumn);
			Date now = new Date(cursor.getLong(dateColumn));
			long duration = cursor.getLong(durationColumn);
	
			String from = number, to = phoneNumber;
			int type = Call.CALL_TYPE_UNKNOWN_DIRECTION;
			switch (cursor.getInt(typeColumn)) {
			case CallLog.Calls.INCOMING_TYPE:
				type = Call.CALL_TYPE_INCOMING;
				break;
				
			case CallLog.Calls.OUTGOING_TYPE:
				from = phoneNumber;
				to = number;
				type = Call.CALL_TYPE_OUTGOING;
				break;
				
			case CallLog.Calls.MISSED_TYPE:
				type = Call.CALL_TYPE_MISSEDCALL;
				break;
			}
			call = new Call(from, to, duration, type, now);
		}
		cursor.close();
		return call;
	}
}