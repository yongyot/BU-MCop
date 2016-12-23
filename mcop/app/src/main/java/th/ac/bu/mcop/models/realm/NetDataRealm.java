package th.ac.bu.mcop.models.realm;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by jeeraphan on 12/22/16.
 */

public class NetDataRealm extends RealmObject{
    private String packageName;
    private String uid;
    private String netWorkState;
    private String applicationState;
    private float sentDataInByte;
    private float receivedDataInByte;
    private float sentDataInBytePercentOfTotal;
    private float receivedDataInBytePercentOfTotal;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public static ArrayList<NetDataRealm> getNetDatas() {

        ArrayList<NetDataRealm> netDataRealms = new ArrayList<>();

        Realm realm = Realm.getDefaultInstance();

        RealmResults<NetDataRealm> result = realm.where(NetDataRealm.class).findAll();

        if (result != null && result.size() > 0) {
            netDataRealms.addAll(result);
        }

        return netDataRealms;

    }

    public static void delete(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<NetDataRealm> results = realm.where(NetDataRealm.class).findAll();
        results.deleteAllFromRealm();
        realm.commitTransaction();
    }
}
