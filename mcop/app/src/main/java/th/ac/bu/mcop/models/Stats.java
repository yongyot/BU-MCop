package th.ac.bu.mcop.models;

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
}
