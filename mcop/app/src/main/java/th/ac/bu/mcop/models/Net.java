package th.ac.bu.mcop.models;

/**
 * Created by jeeraphan on 11/14/16.
 */

public class Net {

    //up
    private String bgUpData;
    private String fgUpData;
    private String bgUpWifi;
    private String fgUpWifi;

    //down
    private String bgDownData;
    private String fgDownData;
    private String bgDownWifi;
    private String fgDownWifi;

    public Net(){
        bgUpData    = "0";
        fgUpData    = "0";
        bgUpWifi    = "0";
        fgUpWifi    = "0";
        bgDownData  = "0";
        fgDownData  = "0";
        bgDownWifi  = "0";
        fgDownWifi  = "0";
    }

    public void copyNet(Net net){
        bgUpData    = net.bgUpData;
        fgUpData    = net.fgUpData;
        bgUpWifi    = net.bgUpWifi;
        fgUpWifi    = net.fgUpWifi;
        bgDownData  = net.bgDownData;
        fgDownData  = net.fgDownData;
        bgDownWifi  = net.bgDownWifi;
        fgDownWifi  = net.fgDownWifi;
    }

    public void setEmtpy() {

        bgUpData    = "-";
        fgUpData    = "-";
        bgUpWifi    = "-";
        fgUpWifi    = "-";
        bgDownData  = "-";
        fgDownData  = "-";
        bgDownWifi  = "-";
        fgDownWifi  = "-";
    }

    public void setNegative() {
        bgUpData    = "-1";
        fgUpData    = "-1";
        bgUpWifi    = "-1";
        fgUpWifi    = "-1";
        bgDownData  = "-1";
        fgDownData  = "-1";
        bgDownWifi  = "-1";
        fgDownWifi  = "-1";
    }
}