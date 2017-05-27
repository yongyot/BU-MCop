package th.ac.bu.mcop;

import android.app.Application;
import android.util.Log;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import th.ac.bu.mcop.utils.Constants;
import th.ac.bu.mcop.utils.Settings;
import th.ac.bu.mcop.utils.SharePrefs;

public class MCOPApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        Fabric.with(this, new Crashlytics());

        deleteDatabase();
    }

    /**
     * Delete realm file when change version code make sure use new database
     */
    private void deleteDatabase(){

        String cacheVersionCode = SharePrefs.getPreferenceString(this, Constants.KEY_VERSION_CODE, "");
        String currentVersionCode = BuildConfig.VERSION_CODE + "";

        if (!cacheVersionCode.equals(currentVersionCode)){
            // cache flag for open first app and initialization again
            SharePrefs.setPreference(this, Constants.KEY_ACCEPT_TERM, false);
            // cache current version code
            SharePrefs.setPreference(this, Constants.KEY_VERSION_CODE, BuildConfig.VERSION_CODE+"");

            Realm realm = null;
            try {
                realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.close();
                        Realm.deleteRealm(realm.getConfiguration());
                    }
                });
            } catch (Exception e){
                Log.d(Settings.TAG, "Exception Delete Realm " + e.getMessage());
                e.printStackTrace();
            } finally {
                if(realm != null) {
                    realm.close();
                }
            }
        }
    }
}