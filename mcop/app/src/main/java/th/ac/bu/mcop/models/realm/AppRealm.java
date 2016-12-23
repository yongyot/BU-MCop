package th.ac.bu.mcop.models.realm;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import th.ac.bu.mcop.models.AppsInfo;

/**
 * Created by jeeraphan on 12/23/16.
 */

public class AppRealm extends RealmObject{

    private String hash;
    private String packageName;
    private String versionName;
    private String versionCode;
    private long lastUpdate;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public static void save(ArrayList<AppsInfo> appsInfos){

        RealmList<AppRealm> appRealms = new RealmList<>();
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        for (AppsInfo appsInfo : appsInfos){

            AppRealm appRealm = realm.createObject(AppRealm.class);
            appRealm.setHash(appsInfo.getHash());
            appRealm.setLastUpdate(appsInfo.getLastUpdate());
            appRealm.setPackageName(appsInfo.getPackageName());
            appRealm.setVersionName(appsInfo.getVersionName());
            appRealm.setVersionCode(appsInfo.getVersionCode());

            appRealms.add(appRealm);
        }

        realm.copyFromRealm(appRealms);
        realm.commitTransaction();
    }

    public static ArrayList<AppRealm> getAll() {

        ArrayList<AppRealm> appRealms = new ArrayList<>();

        Realm realm = Realm.getDefaultInstance();

        RealmResults<AppRealm> result = realm.where(AppRealm.class).findAll();

        if (result != null && result.size() > 0) {
            appRealms.addAll(result);
        }

        return appRealms;
    }

    public static void deleteAll(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<AppRealm> results = realm.where(AppRealm.class).findAll();
        results.deleteAllFromRealm();
        realm.commitTransaction();
    }
}
