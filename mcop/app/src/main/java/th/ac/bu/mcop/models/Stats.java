package th.ac.bu.mcop.models;

import android.content.Context;

import java.util.ArrayList;

import th.ac.bu.mcop.modules.NetStatsExtractor;

/**
 * Created by jeeraphan on 11/15/16.
 */

public class Stats {

    private boolean isInteracting;
    private int state;
    private String uid;
    private String packageName;
    private Net net;

    public Stats(){
        isInteracting = false;
        uid = new String();
        packageName = new String();

        net = new Net();
    }

    public Stats(Stats stats){
        net = new Net();

        this.isInteracting = stats.isInteracting;
        this.uid = stats.uid;
        this.packageName = stats.packageName;

        this.net.copyNet(stats.net);
    }

    public boolean isInteracting() {
        return isInteracting;
    }

    public void setInteracting(boolean interacting) {
        isInteracting = interacting;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

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

    public Net getNet() {
        return net;
    }

    public void setNet(Net net) {
        this.net = net;
    }

    public static ArrayList<Stats> getStats(Context context){

        ArrayList<Stats> listStats = new ArrayList<>();

        return listStats;
    }

    public boolean isMainProcess() {
        return !packageName.contains(":");
    }
}
