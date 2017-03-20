package th.ac.bu.mcop.models;

public class Stats {

    private int state;
    private String uid;
    private String packageName;
    private Net net;

    public Stats(){
        uid = new String();
        packageName = new String();
        net = new Net();
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

    public boolean isMainProcess() {
        return !packageName.contains(":");
    }
}
