package th.ac.bu.mcop.models;

/**
 * Created by jeeraphan on 11/22/16.
 */

public class NetData {

    // Define Constant value
    public static final String NETWORK_STATE_WIFI = "1";
    public static final String NETWORK_STATE_CELLULAR = "2";
    public static final String NETWORK_STATE_NO_NETWORK = "0";

    public static final String NETWORK_MODE_EVENTUAL = "0";
    public static final String NETWORK_MODE_CONTINUOUS = "1";

    public static final String APPLICATION_STATE_FOREGROUND = "0";
    public static final String APPLICATION_STATE_BACKGROUND = "1";

    public static final String BETWEEN_INNER = "0";
    public static final String BETWEEN_OUTER = "1";


    // Data model
    private String sentDataInBytePercentOfTotal;
    private String receivedDataInBytePercentOfTotal;
    private String netWorkState;
    private String netWorkMode;
    private String applicationState;

    private String avgOfSentDataInByte;
    private String sdOfSentDataInByte;
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
}
