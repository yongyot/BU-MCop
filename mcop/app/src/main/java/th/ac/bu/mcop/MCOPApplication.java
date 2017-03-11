package th.ac.bu.mcop;

import android.content.IntentFilter;

import io.realm.Realm;
import th.ac.bu.mcop.android.monitor.AndroidMonitorApplication;
import th.ac.bu.mcop.mobile.monitor.core.Watchdog;

/**
 * Created by jeeraphan on 12/22/16.
 */

public class MCOPApplication extends AndroidMonitorApplication {

    @Override
    protected void initialize(Watchdog watchdog, IntentFilter filter) {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}
