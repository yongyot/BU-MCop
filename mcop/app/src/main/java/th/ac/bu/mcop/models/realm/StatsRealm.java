package th.ac.bu.mcop.models.realm;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import th.ac.bu.mcop.models.Net;
import th.ac.bu.mcop.models.Stats;
import th.ac.bu.mcop.utils.Settings;

/**
 * Created by jeeraphan on 12/23/16.
 */

public class StatsRealm extends RealmObject{

    private String uid;
    private String packageName;
    private String netWorkState;
    private String applicationState;
    private float sentDataInByte;
    private float receivedDataInByte;
    private float sentDataInBytePercentOfTotal;
    private float receivedDataInBytePercentOfTotal;
    private Net net;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getNetWorkState() {
        return netWorkState;
    }

    public void setNetWorkState(String netWorkState) {
        this.netWorkState = netWorkState;
    }

    public String getApplicationState() {
        return applicationState;
    }

    public void setApplicationState(String applicationState) {
        this.applicationState = applicationState;
    }

    public float getSentDataInByte() {
        return sentDataInByte;
    }

    public void setSentDataInByte(float sentDataInByte) {
        this.sentDataInByte = sentDataInByte;
    }

    public float getReceivedDataInByte() {
        return receivedDataInByte;
    }

    public void setReceivedDataInByte(float receivedDataInByte) {
        this.receivedDataInByte = receivedDataInByte;
    }

    public float getSentDataInBytePercentOfTotal() {
        return sentDataInBytePercentOfTotal;
    }

    public void setSentDataInBytePercentOfTotal(float sentDataInBytePercentOfTotal) {
        this.sentDataInBytePercentOfTotal = sentDataInBytePercentOfTotal;
    }

    public float getReceivedDataInBytePercentOfTotal() {
        return receivedDataInBytePercentOfTotal;
    }

    public void setReceivedDataInBytePercentOfTotal(float receivedDataInBytePercentOfTotal) {
        this.receivedDataInBytePercentOfTotal = receivedDataInBytePercentOfTotal;
    }

    public Net getNet() {
        return net;
    }

    public void setNet(Net net) {
        this.net = net;
    }

    public static void save(ArrayList<Stats> listAppRunning){

        int totalSentInByte = 0;
        int totalReceivedInByte = 0;

        for (Stats stats : listAppRunning){

            totalSentInByte += stats.getNet().getUpDataInByte();
            totalReceivedInByte += stats.getNet().getDownDataInByte();
        }

        // Save to Realm
        RealmList<StatsRealm> statsRealms = new RealmList<>();
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        for (Stats stats : listAppRunning){

            float sentDataInBytePercentOfToal = (stats.getNet().getUpDataInByte() / totalSentInByte) * 100;
            float receivedDataInBytePercentOfTotal = (stats.getNet().getDownDataInByte() / totalReceivedInByte)* 100;

            StatsRealm statsRealm = realm.createObject(StatsRealm.class);
            statsRealm.setPackageName(stats.getPackageName());
            statsRealm.setUid(stats.getUid());
            statsRealm.setNetWorkState(Settings.sNetworkType + "");
            statsRealm.setApplicationState(stats.getState() + "");
            statsRealm.setSentDataInByte(stats.getNet().getUpDataInByte());
            statsRealm.setReceivedDataInByte(stats.getNet().getDownDataInByte());
            statsRealm.setSentDataInBytePercentOfTotal(sentDataInBytePercentOfToal);
            statsRealm.setReceivedDataInBytePercentOfTotal(receivedDataInBytePercentOfTotal);

            statsRealms.add(statsRealm);
        }

        realm.copyFromRealm(statsRealms);
        realm.commitTransaction();

    }

    public static ArrayList<StatsRealm> getAll() {

        ArrayList<StatsRealm> statsRealms = new ArrayList<>();

        Realm realm = Realm.getDefaultInstance();

        RealmResults<StatsRealm> result = realm.where(StatsRealm.class).findAll();

        if (result != null && result.size() > 0) {
            statsRealms.addAll(result);
        }

        return statsRealms;

    }

    public static ArrayList<StatsRealm> getStatsWithPackageName(String packageName){

        ArrayList<StatsRealm> statsRealms = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();

        RealmResults<StatsRealm> result = realm.where(StatsRealm.class)
                                            .equalTo("packageName", packageName)
                                            .findAll();

        if (result != null && result.size() > 0) {
            statsRealms.addAll(result);
        }

        return statsRealms;
    }

    public static void deleteAll(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<StatsRealm> results = realm.where(StatsRealm.class).findAll();
        results.deleteAllFromRealm();
        realm.commitTransaction();
    }
}
