package th.ac.bu.mcop;

import android.app.Application;
import io.realm.Realm;

public class MCOPApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}