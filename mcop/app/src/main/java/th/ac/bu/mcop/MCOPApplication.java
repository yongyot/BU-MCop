package th.ac.bu.mcop;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by jeeraphan on 12/22/16.
 */

public class MCOPApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}
