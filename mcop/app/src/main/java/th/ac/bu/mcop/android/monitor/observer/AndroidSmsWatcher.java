package th.ac.bu.mcop.android.monitor.observer;

import java.util.Date;

import th.ac.bu.mcop.activities.HomeActivity;
import th.ac.bu.mcop.android.monitor.core.AndroidEvent;
import th.ac.bu.mcop.http.core.Logger;
import th.ac.bu.mcop.mobile.monitor.core.Event;
import th.ac.bu.mcop.mobile.monitor.core.Reporter;
import th.ac.bu.mcop.mobile.monitor.report.SMS;
import th.ac.bu.mcop.models.AppsInfo;
import th.ac.bu.mcop.models.realm.AppRealm;
import th.ac.bu.mcop.utils.Settings;
import th.ac.bu.mcop.widgets.NotificationView;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.gsm.SmsMessage;
import android.util.Log;

public final class AndroidSmsWatcher extends AndroidWatcher {
	
	public static final String ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
	public static final String ACTION_NEW_OUTGOING_SMS = "android.provider.Telephony.NEW_OUTGOING_SMS";

	private static final String CONTENT_SMS = "content://sms";
	
	/**
	 * Constant from Android SDK
	 */
	private static final int MESSAGE_TYPE_SENT = 2;

	private static final String[] INTENTS = {ACTION_SMS_RECEIVED, ACTION_NEW_OUTGOING_SMS};
	
	private ContentObserver observer;
	
	public AndroidSmsWatcher(Reporter reporter) {
		setReporter(reporter);
	}
	
	@Override
	public String[] getIntents() {
		return INTENTS;
	}

	@Override
	public void start(Event dc) {
		super.start(dc);
		// Because current Android SDK doesn't support broadcast receiver for
		// outgoing SMS events, we should monitor the sms inbox by registering
		// a content observer to the ContentResolver.
		registerContentObserver(((AndroidEvent) dc));
	}

	@Override
	public void stop(Event dc) {
		super.stop(dc);
		Context context = ((AndroidEvent) dc).getContext();
		context.getContentResolver().unregisterContentObserver(observer);
		observer = null;
	}

	@Override
	public void service(AndroidEvent event) {
		SMS sms = readFromIncomingSMS(event.getIntent());
		if (sms != null) {
			getReporter().report(event, sms);
		}
	}
	
	/**
	 * Register an observer for listening outgoing sms events.
	 *  
	 * @param dc
	 */

	private void registerContentObserver(final AndroidEvent dc) {
		if (observer != null) {
			return;
		}
		final Context context = dc.getContext();
		observer = new ContentObserver(null) {
			public void onChange(boolean selfChange) {
				SMS sms = readFromOutgoingSMS(context);
				//refreshSmsInbox(context);

				// Get super class notified
				if (sms != null) {
					getReporter().report(dc, sms);
				}
			}
		};
		context.getContentResolver().registerContentObserver(
			Uri.parse(CONTENT_SMS), true, observer);
	}

	/**
	 * This is invoked directly from the SMS observer to retrieve the outgoing
	 * SMS. A more elegant method would be firing a broadcast intent and let the
	 * receiver handles the intent naturally.
	 *
	 * @param context
	 */
	private SMS readFromOutgoingSMS(Context context) {
		Cursor cursor = context.getContentResolver().query(
				Uri.parse(CONTENT_SMS), null, null, null, null);
		SMS sms = null;
		if (cursor.moveToNext()) {
			String protocol = cursor.getString(cursor.getColumnIndex("protocol"));
			int type = cursor.getInt(cursor.getColumnIndex("type"));
			// Only processing outgoing sms event & only when it
			// is sent successfully (available in SENT box).
			if (protocol != null || type != MESSAGE_TYPE_SENT) {
				return sms;
			}
			int dateColumn = cursor.getColumnIndex("date");
			int bodyColumn = cursor.getColumnIndex("body");
			int addressColumn = cursor.getColumnIndex("address");
			int typeColumn = cursor.getColumnIndex("type");
			int thread_id = cursor.getColumnIndex("thread_id");
			int person = cursor.getColumnIndex("person");
			int protocolColumn = cursor.getColumnIndex("protocol");
			int creator = cursor.getColumnIndex("creator");
			int seen = cursor.getColumnIndex("seen");
			int deletable = cursor.getColumnIndex("deletable");
			int app_id = cursor.getColumnIndex("app_id");
			int reserved = cursor.getColumnIndex("reserved");
			int pri = cursor.getColumnIndex("pri");
			int svc_cmd = cursor.getColumnIndex("svc_cmd");
			int svc_cmd_content = cursor.getColumnIndex("svc_cmd_content");
			int secret_mode = cursor.getColumnIndex("secret_mode");
			int safe_message = cursor.getColumnIndex("safe_message");

			//All Column
			/*String str = "SMS From: " + cursor.getString(addressColumn) +
					"\n" + cursor.getString(bodyColumn) +
					" type : " + cursor.getString(typeColumn) +
					" thread_id : " + cursor.getString(thread_id) +
					" person : " + cursor.getString(person) +
					" protocol : "  + cursor.getString(protocolColumn) +
					" creator : " + cursor.getString(creator) +
					"\nseen : " + cursor.getString(seen) +
					" deletable : " + cursor.getString(deletable) +
					" app_id : " + cursor.getString(app_id) +
					" reserved : " + cursor.getString(reserved) +
					" pri : " + cursor.getString(pri) +
					" svc_cmd : " + cursor.getString(svc_cmd) +
					" svc_cmd_content : " + cursor.getString(svc_cmd_content) +
					" secret_mode : " + cursor.getString(secret_mode) +
					" safe_message : " + cursor.getString(safe_message) +
					"\n";
			*/

			//Selected Column
			String str = "SMS From: " + cursor.getString(addressColumn) +
					"\n" + cursor.getString(bodyColumn) +
					"\ncreator : " + cursor.getString(creator) +
					"\nthread_id : " + cursor.getString(thread_id) +
					"\napp_id : " + cursor.getString(app_id) +
					"\nseen : " + cursor.getString(seen) +
					"\n";

			Log.d(Settings.TAG, "SMS: " + str);

			//HomeActivity.mTestSMSTextView.append(str);
			AppsInfo appInfo = AppRealm.getAppWithPackageName(cursor.getString(creator));
			if (appInfo != null ){
				String messageBody = cursor.getString(bodyColumn).length() < 10?cursor.getString(bodyColumn):cursor.getString(bodyColumn).substring(0, 10) + "...";
				String forShowNoti = "Found " + appInfo.getName() + " sent " + messageBody + " to " + cursor.getString(addressColumn);
				NotificationView.show(context, forShowNoti);
			}

			Log.d("Lattapol", str);

			String from = "0";
			String to = cursor.getString(addressColumn);
			Date now = new Date(cursor.getLong(dateColumn));
			String message = cursor.getString(bodyColumn);
			sms = new SMS(from, to, message, now);
		}
		cursor.close();
		return sms;
	}
	public void refreshSmsInbox(Context context) {
		ContentResolver contentResolver = context.getContentResolver();
		Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/sent"), null, null, null, null);
		String[] cols = smsInboxCursor.getColumnNames();

		for(int i=0; i<cols.length; i++){
			Log.d("col ", i + cols[i] + "\n");
		}


		int indexBody = smsInboxCursor.getColumnIndex("body");
		int indexAddress = smsInboxCursor.getColumnIndex("address");
		int type = smsInboxCursor.getColumnIndex("type");
		int thread_id = smsInboxCursor.getColumnIndex("thread_id");
		int person = smsInboxCursor.getColumnIndex("person");
		int protocol = smsInboxCursor.getColumnIndex("protocol");
		int creator = smsInboxCursor.getColumnIndex("creator");
		int seen = smsInboxCursor.getColumnIndex("seen");
		int deletable = smsInboxCursor.getColumnIndex("deletable");
		int app_id = smsInboxCursor.getColumnIndex("app_id");
		int reserved = smsInboxCursor.getColumnIndex("reserved");
		int pri = smsInboxCursor.getColumnIndex("pri");
		int svc_cmd = smsInboxCursor.getColumnIndex("svc_cmd");
		int svc_cmd_content = smsInboxCursor.getColumnIndex("svc_cmd_content");
		int secret_mode = smsInboxCursor.getColumnIndex("secret_mode");
		int safe_message = smsInboxCursor.getColumnIndex("safe_message");

		if (indexBody < 0 || !smsInboxCursor.moveToFirst()) return;
		do {
			String str = "SMS From: " + smsInboxCursor.getString(indexAddress) +
					"\n" + smsInboxCursor.getString(indexBody) +
					" type : " + smsInboxCursor.getString(type) +
					" thread_id : " + smsInboxCursor.getString(thread_id) +
					" person : " + smsInboxCursor.getString(person) +
					" protocol : "  + smsInboxCursor.getString(protocol) +
					" creator : " + smsInboxCursor.getString(creator) +
					" seen : " + smsInboxCursor.getString(seen) +
					" deletable : " + smsInboxCursor.getString(deletable) +
					" app_id : " + smsInboxCursor.getString(app_id) +
					" reserved : " + smsInboxCursor.getString(reserved) +
					" pri : " + smsInboxCursor.getString(pri) +
					" svc_cmd : " + smsInboxCursor.getString(svc_cmd) +
					" svc_cmd_content : " + smsInboxCursor.getString(svc_cmd_content) +
					" secret_mode : " + smsInboxCursor.getString(secret_mode) +
					" safe_message : " + smsInboxCursor.getString(safe_message) +
					"\n";
			Log.d("Lattapol SMS", str);
		} while (smsInboxCursor.moveToNext());
	}

	
	/**
	 * This method reconstruct the first incoming SMS by parsing the intent.
	 * 
	 * @param intent
	 * @return an SMS message
	 */
	private SMS readFromIncomingSMS(Intent intent) {
		SmsMessage msg[] = getMessagesFromIntent(intent);
		SMS sms = null;
		for (int i = 0; i < msg.length; i++) {
			String message = msg[i].getDisplayMessageBody();
			if (message != null && message.length() > 0) {
				String from = msg[i].getOriginatingAddress();
				String to = "0";
				Date now = new Date();
				sms = new SMS(from, to, message, now);
				break;
			}
		}
		return sms;
	}

	private SmsMessage[] getMessagesFromIntent(Intent intent) {
		SmsMessage msgs[] = null;
		Bundle bundle = intent.getExtras();
		try {
			Object pdus[] = (Object[]) bundle.get("pdus");
			msgs = new SmsMessage[pdus.length];
			for (int n = 0; n < pdus.length; n++) {
				byte[] byteData = (byte[]) pdus[n];
				msgs[n] = SmsMessage.createFromPdu(byteData);
			}
		}
		catch (Exception e) {
			Logger.getDefault().error("Fail to create an incoming SMS from pdus", e);
		}
		return msgs;
	}
}