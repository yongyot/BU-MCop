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

    public  String getBgUpData() {
        return bgUpData;
    }

    public void   setBgUpData(String bgUpData) {
        this.bgUpData = bgUpData;
    }

    public String getFgUpData() {
        return fgUpData;
    }

    public void   setFgUpData(String fgUpData) {
        this.fgUpData = fgUpData;
    }

    public String getBgUpWifi() {
        return bgUpWifi;
    }

    public void   setBgUpWifi(String bgUpWifi) {
        this.bgUpWifi = bgUpWifi;
    }

    public String getFgUpWifi() {
        return fgUpWifi;
    }

    public void   setFgUpWifi(String fgUpWifi) {
        this.fgUpWifi = fgUpWifi;
    }

    public String getBgDownData() {
        return bgDownData;
    }

    public void   setBgDownData(String bgDownData) {
        this.bgDownData = bgDownData;
    }

    public String getFgDownData() {
        return fgDownData;
    }

    public void   setFgDownData(String fgDownData) {
        this.fgDownData = fgDownData;
    }

    public String getBgDownWifi() {
        return bgDownWifi;
    }

    public void   setBgDownWifi(String bgDownWifi) {
        this.bgDownWifi = bgDownWifi;
    }

    public String getFgDownWifi() {
        return fgDownWifi;
    }

    public void   setFgDownWifi(String fgDownWifi) {
        this.fgDownWifi = fgDownWifi;
    }
}