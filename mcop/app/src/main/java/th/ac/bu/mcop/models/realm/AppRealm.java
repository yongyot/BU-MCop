package th.ac.bu.mcop.models.realm;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import th.ac.bu.mcop.models.AppsInfo;
import th.ac.bu.mcop.utils.Constants;

public class AppRealm extends RealmObject{

    private String hash;
    private String name;
    private String packageName;
    private String versionName;
    private String versionCode;
    private String scan;
    private long lastUpdate;
    private int appStatus;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAppStatus() {
        return appStatus;
    }

    public void setAppStatus(int appStatus) {
        this.appStatus = appStatus;
    }

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


    public String getScan() {
        return scan;
    }

    public void setScan(String scan) {
        this.scan = scan;
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
            appRealm.setAppStatus(appsInfo.getAppStatus());
            appRealm.setName(appsInfo.getName());
            appRealm.setScan(appsInfo.getScan());

            appRealms.add(appRealm);
        }

        realm.copyFromRealm(appRealms);
        realm.commitTransaction();
    }

    public static void save(AppsInfo appsInfo){

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        AppRealm appRealm = realm.createObject(AppRealm.class);
        appRealm.setHash(appsInfo.getHash());
        appRealm.setLastUpdate(appsInfo.getLastUpdate());
        appRealm.setPackageName(appsInfo.getPackageName());
        appRealm.setVersionName(appsInfo.getVersionName());
        appRealm.setVersionCode(appsInfo.getVersionCode());
        appRealm.setAppStatus(appsInfo.getAppStatus());
        appRealm.setName(appsInfo.getName());
        appRealm.setScan(appsInfo.getScan());

        realm.copyFromRealm(appRealm);
        realm.commitTransaction();
    }

    public static void update(AppsInfo appsInfo) {
        Realm realm = Realm.getDefaultInstance();

        AppRealm app = realm.where(AppRealm.class)
                .equalTo("packageName", appsInfo.getPackageName())
                .findFirst();

        realm.beginTransaction();
        if (app == null) {

            AppRealm appRealm = realm.createObject(AppRealm.class);
            appRealm.setHash(appsInfo.getHash());
            appRealm.setLastUpdate(appsInfo.getLastUpdate());
            appRealm.setVersionName(appsInfo.getVersionName());
            appRealm.setVersionCode(appsInfo.getVersionCode());
            appRealm.setAppStatus(appsInfo.getAppStatus());
            appRealm.setName(appsInfo.getName());
            appRealm.setScan(appsInfo.getScan());
        } else {
            app.setAppStatus(appsInfo.getAppStatus());
            app.setScan(appsInfo.getScan());
        }

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

    public static AppsInfo getAppWithHash(final String hash){
        Realm realm = Realm.getDefaultInstance();
        AppRealm appRealm = realm.where(AppRealm.class).equalTo("hash", hash).findFirst();
        if (appRealm != null){
            AppsInfo appsInfo = new AppsInfo();
            appsInfo.setAppStatus(appRealm.getAppStatus());
            appsInfo.setHash(appRealm.getHash());
            appsInfo.setVersionCode(appRealm.getVersionCode());
            appsInfo.setVersionName(appRealm.getVersionName());
            appsInfo.setLastUpdate(appRealm.getLastUpdate());
            appsInfo.setPackageName(appRealm.getPackageName());
            appsInfo.setAppStatus(appRealm.getAppStatus());
            appsInfo.setName(appRealm.getName());
            appsInfo.setScan(appRealm.getScan());

            return appsInfo;
        }

        return null;
    }

    public static AppsInfo getAppWithPackageName(final String packageName){
        Realm realm = Realm.getDefaultInstance();
        AppRealm appRealm = realm.where(AppRealm.class).equalTo("packageName", packageName).findFirst();

        AppsInfo appsInfo = new AppsInfo();
        appsInfo.setAppStatus(appRealm.getAppStatus());
        appsInfo.setHash(appRealm.getHash());
        appsInfo.setVersionCode(appRealm.getVersionCode());
        appsInfo.setVersionName(appRealm.getVersionName());
        appsInfo.setLastUpdate(appRealm.getLastUpdate());
        appsInfo.setPackageName(appRealm.getPackageName());
        appsInfo.setAppStatus(appRealm.getAppStatus());
        appsInfo.setName(appRealm.getName());
        appsInfo.setScan(appRealm.getScan());
        return appsInfo;
    }

    public static void deleteAppWithPackageName(final String packageName){

        Realm realm = Realm.getDefaultInstance();

        RealmQuery<AppRealm> query = realm.where(AppRealm.class);
        query.equalTo("packageName", packageName);

        final RealmResults<AppRealm> result = query.findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (result.size() > 0){
                    result.get(0).deleteFromRealm();
                }

            }
        });
    }

    public static ArrayList<AppRealm> getSafeApps(){

        ArrayList<AppRealm> appRealms = new ArrayList<>();

        Realm realm = Realm.getDefaultInstance();

        RealmQuery<AppRealm> query = realm.where(AppRealm.class);
        query.equalTo("appStatus", Constants.APP_STATUS_SAFE);

        RealmResults<AppRealm> result = query.findAll();
        appRealms.addAll(result);

        return appRealms;
    }

    public static ArrayList<AppRealm> getWarningYellowApps(){

        ArrayList<AppRealm> appRealms = new ArrayList<>();

        Realm realm = Realm.getDefaultInstance();

        RealmQuery<AppRealm> query = realm.where(AppRealm.class);
        query.equalTo("appStatus", Constants.APP_STATUS_WARNING_YELLOW);

        RealmResults<AppRealm> result = query.findAll();
        appRealms.addAll(result);

        return appRealms;
    }

    public static ArrayList<AppRealm> getWarningOrangeApps(){

        ArrayList<AppRealm> appRealms = new ArrayList<>();

        Realm realm = Realm.getDefaultInstance();

        RealmQuery<AppRealm> query = realm.where(AppRealm.class);
        query.equalTo("appStatus", Constants.APP_STATUS_WARNING_ORANGE);

        RealmResults<AppRealm> result = query.findAll();
        appRealms.addAll(result);

        return appRealms;
    }

    public static ArrayList<AppRealm> getWarningRedApps(){

        ArrayList<AppRealm> appRealms = new ArrayList<>();

        Realm realm = Realm.getDefaultInstance();

        RealmQuery<AppRealm> query = realm.where(AppRealm.class);
        query.equalTo("appStatus", Constants.APP_STATUS_WARNING_RED);

        RealmResults<AppRealm> result = query.findAll();
        appRealms.addAll(result);

        return appRealms;
    }

    public static ArrayList<AppRealm> getSendHashApps(){

        ArrayList<AppRealm> appRealms = new ArrayList<>();

        Realm realm = Realm.getDefaultInstance();

        RealmQuery<AppRealm> query = realm.where(AppRealm.class);
        query.equalTo("appStatus", Constants.APP_STATUS_WAIT_FOR_SEND_HASH);

        RealmResults<AppRealm> result = query.findAll();
        appRealms.addAll(result);

        return appRealms;
    }

    public static ArrayList<AppRealm> getSendApkApps(){

        ArrayList<AppRealm> appRealms = new ArrayList<>();

        Realm realm = Realm.getDefaultInstance();

        RealmQuery<AppRealm> query = realm.where(AppRealm.class);
        query.equalTo("appStatus", Constants.APP_STATUS_WAIT_FOR_SEND_APK);

        RealmResults<AppRealm> result = query.findAll();
        appRealms.addAll(result);

        return appRealms;
    }

    public static void updateWithPackage(final String packageName, final int appStatus){

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                AppRealm appRealm = bgRealm.where(AppRealm.class).equalTo("packageName", packageName).findFirst();
                appRealm.setAppStatus(appStatus);
            }
        });
    }

    public static boolean isExising(String packageName) {

        Realm realm = Realm.getDefaultInstance();

        RealmQuery<AppRealm> query = realm.where(AppRealm.class);
        query.equalTo("packageName", packageName);

        if (query.findAll().size() > 0) {
            return true;
        }

        return false;
    }

    public static void deleteAll(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<AppRealm> results = realm.where(AppRealm.class).findAll();
        results.deleteAllFromRealm();
        realm.commitTransaction();
    }
}
