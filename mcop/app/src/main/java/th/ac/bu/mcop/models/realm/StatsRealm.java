package th.ac.bu.mcop.models.realm;

import android.util.Log;

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
    private double sentDataInByte;
    private double receivedDataInByte;
    private double sentDataInBytePercentOfTotal;
    private double receivedDataInBytePercentOfTotal;
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

    public double getSentDataInByte() {
        return sentDataInByte;
    }

    public void setSentDataInByte(int sentDataInByte) {
        this.sentDataInByte = sentDataInByte;
    }

    public double getReceivedDataInByte() {
        return receivedDataInByte;
    }

    public void setReceivedDataInByte(int receivedDataInByte) {
        this.receivedDataInByte = receivedDataInByte;
    }

    public double getSentDataInBytePercentOfTotal() {
        return sentDataInBytePercentOfTotal;
    }

    public void setSentDataInBytePercentOfTotal(int sentDataInBytePercentOfTotal) {
        this.sentDataInBytePercentOfTotal = sentDataInBytePercentOfTotal;
    }

    public double getReceivedDataInBytePercentOfTotal() {
        return receivedDataInBytePercentOfTotal;
    }

    public void setReceivedDataInBytePercentOfTotal(int receivedDataInBytePercentOfTotal) {
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

            int sentDataInBytePercentOfToal = (stats.getNet().getUpDataInByte() / totalSentInByte) * 100;
            int receivedDataInBytePercentOfTotal = (stats.getNet().getDownDataInByte() / totalReceivedInByte)* 100;

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

            if (stats.getPackageName().equals("com.nianticlabs.pokemongo")){
                Log.d(Settings.TAG, "getPackageName                     : " + stats.getPackageName());
                Log.d(Settings.TAG, "getUid                             : " + stats.getUid());
                Log.d(Settings.TAG, "NetworkType                        : " + Settings.sNetworkType + "");
                Log.d(Settings.TAG, "ApplicationState                   : " + stats.getState() + "");
                Log.d(Settings.TAG, "getUpDataInByte                    : " + stats.getNet().getUpDataInByte());
                Log.d(Settings.TAG, "getDownDataInByte                  : " + stats.getNet().getDownDataInByte());
                Log.d(Settings.TAG, "sentDataInBytePercentOfToal        : " + sentDataInBytePercentOfToal);
                Log.d(Settings.TAG, "receivedDataInBytePercentOfTotal   : " + receivedDataInBytePercentOfTotal);
                Log.d(Settings.TAG, "********************");
            }
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
