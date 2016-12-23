package th.ac.bu.mcop.models;

/**
 * Created by jeeraphan on 11/22/16.
 */

public class NetData {

    // Define Constant value

    public static final String NETWORK_MODE_EVENTUAL = "0";
    public static final String NETWORK_MODE_CONTINUOUS = "1";

    public static final String BETWEEN_INNER = "0";
    public static final String BETWEEN_OUTER = "1";


    // Data model

    private String packageName;
    private String uid;

    private String sentDataInBytePercentOfTotal;
    private String receivedDataInBytePercentOfTotal;
    private String netWorkState;
    private String netWorkMode;//
    private String applicationState;

    private String avgOfSentDataInByte;
    private String sdOfSentDataInByte;//
    private String minOfSentDataInByte;
    private String maxOfSentDataInByte;

    private String avgOfReceivedDataInByte;
    private String sdOfReceivedDataInByte;
    private String minOfReceivedDataInByte;
    private String maxOfReceivedDataInByte;

    private String avgOfSentDataInPercent;
    private String sdOfSentDataInPercent;
    private String minOfSentDataInPercent;
    private String maxOfSentDataInPercent;

    private String avgOfReceivedDataInPercent;
    private String sdOfReceivedDataInPercent;
    private String minOfReceivedDataInPercent;
    private String maxOfReceivedDataInPercent;

    private String sentBetween;
    private String receivedBetween;

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

    public String getSentDataInBytePercentOfTotal() {
        return sentDataInBytePercentOfTotal;
    }

    public void setSentDataInBytePercentOfTotal(String sentDataInBytePercentOfTotal) {
        this.sentDataInBytePercentOfTotal = sentDataInBytePercentOfTotal;
    }

    public String getReceivedDataInBytePercentOfTotal() {
        return receivedDataInBytePercentOfTotal;
    }

    public void setReceivedDataInBytePercentOfTotal(String receivedDataInBytePercentOfTotal) {
        this.receivedDataInBytePercentOfTotal = receivedDataInBytePercentOfTotal;
    }

    public String getNetWorkState() {
        return netWorkState;
    }

    public void setNetWorkState(String netWorkState) {
        this.netWorkState = netWorkState;
    }

    public String getNetWorkMode() {
        return netWorkMode;
    }

    public void setNetWorkMode(String netWorkMode) {
        this.netWorkMode = netWorkMode;
    }

    public String getApplicationState() {
        return applicationState;
    }

    public void setApplicationState(String applicationState) {
        this.applicationState = applicationState;
    }

    public String getAvgOfSentDataInByte() {
        return avgOfSentDataInByte;
    }

    public void setAvgOfSentDataInByte(String avgOfSentDataInByte) {
        this.avgOfSentDataInByte = avgOfSentDataInByte;
    }

    public String getSdOfSentDataInByte() {
        return sdOfSentDataInByte;
    }

    public void setSdOfSentDataInByte(String sdOfSentDataInByte) {
        this.sdOfSentDataInByte = sdOfSentDataInByte;
    }

    public String getMinOfSentDataInByte() {
        return minOfSentDataInByte;
    }

    public void setMinOfSentDataInByte(String minOfSentDataInByte) {
        this.minOfSentDataInByte = minOfSentDataInByte;
    }

    public String getMaxOfSentDataInByte() {
        return maxOfSentDataInByte;
    }

    public void setMaxOfSentDataInByte(String maxOfSentDataInByte) {
        this.maxOfSentDataInByte = maxOfSentDataInByte;
    }

    public String getAvgOfReceivedDataInByte() {
        return avgOfReceivedDataInByte;
    }

    public void setAvgOfReceivedDataInByte(String avgOfReceivedDataInByte) {
        this.avgOfReceivedDataInByte = avgOfReceivedDataInByte;
    }

    public String getSdOfReceivedDataInByte() {
        return sdOfReceivedDataInByte;
    }

    public void setSdOfReceivedDataInByte(String sdOfReceivedDataInByte) {
        this.sdOfReceivedDataInByte = sdOfReceivedDataInByte;
    }

    public String getMinOfReceivedDataInByte() {
        return minOfReceivedDataInByte;
    }

    public void setMinOfReceivedDataInByte(String minOfReceivedDataInByte) {
        this.minOfReceivedDataInByte = minOfReceivedDataInByte;
    }

    public String getMaxOfReceivedDataInByte() {
        return maxOfReceivedDataInByte;
    }

    public void setMaxOfReceivedDataInByte(String maxOfReceivedDataInByte) {
        this.maxOfReceivedDataInByte = maxOfReceivedDataInByte;
    }

    public String getAvgOfSentDataInPercent() {
        return avgOfSentDataInPercent;
    }

    public void setAvgOfSentDataInPercent(String avgOfSentDataInPercent) {
        this.avgOfSentDataInPercent = avgOfSentDataInPercent;
    }

    public String getSdOfSentDataInPercent() {
        return sdOfSentDataInPercent;
    }

    public void setSdOfSentDataInPercent(String sdOfSentDataInPercent) {
        this.sdOfSentDataInPercent = sdOfSentDataInPercent;
    }

    public String getMinOfSentDataInPercent() {
        return minOfSentDataInPercent;
    }

    public void setMinOfSentDataInPercent(String minOfSentDataInPercent) {
        this.minOfSentDataInPercent = minOfSentDataInPercent;
    }

    public String getMaxOfSentDataInPercent() {
        return maxOfSentDataInPercent;
    }

    public void setMaxOfSentDataInPercent(String maxOfSentDataInPercent) {
        this.maxOfSentDataInPercent = maxOfSentDataInPercent;
    }

    public String getAvgOfReceivedDataInPercent() {
        return avgOfReceivedDataInPercent;
    }

    public void setAvgOfReceivedDataInPercent(String avgOfReceivedDataInPercent) {
        this.avgOfReceivedDataInPercent = avgOfReceivedDataInPercent;
    }

    public String getSdOfReceivedDataInPercent() {
        return sdOfReceivedDataInPercent;
    }

    public void setSdOfReceivedDataInPercent(String sdOfReceivedDataInPercent) {
        this.sdOfReceivedDataInPercent = sdOfReceivedDataInPercent;
    }

    public String getMinOfReceivedDataInPercent() {
        return minOfReceivedDataInPercent;
    }

    public void setMinOfReceivedDataInPercent(String minOfReceivedDataInPercent) {
        this.minOfReceivedDataInPercent = minOfReceivedDataInPercent;
    }

    public String getMaxOfReceivedDataInPercent() {
        return maxOfReceivedDataInPercent;
    }

    public void setMaxOfReceivedDataInPercent(String maxOfReceivedDataInPercent) {
        this.maxOfReceivedDataInPercent = maxOfReceivedDataInPercent;
    }

    public String getSentBetween() {
        return sentBetween;
    }

    public void setSentBetween(String sentBetween) {
        this.sentBetween = sentBetween;
    }

    public String getReceivedBetween() {
        return receivedBetween;
    }

    public void setReceivedBetween(String receivedBetween) {
        this.receivedBetween = receivedBetween;
    }
}
