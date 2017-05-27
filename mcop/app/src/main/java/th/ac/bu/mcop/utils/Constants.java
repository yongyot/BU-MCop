package th.ac.bu.mcop.utils;

public class Constants {

    public static final int NETWORK_TYPE_NO_NETWORK = 0;
    public static final int NETWORK_TYPE_MOBILE = 1;
    public static final int NETWORK_TYPE_WIFI = 2;

    public static final int STATE_BACKGROUND = 0;
    public static final int STATE_FOREGROUND = 1;

    public static final int ONGOING_NOTIFICATION_ID = 2147483;

    public static final String INTENT_FILTER_UPDATE_UI = "intent_filter_update_ui";
    public static final String INTENT_FILTER_INTERNET = "intent_filter_internet";

    public static final String KEY_CURRENT_DATE = "key_current_date";
    public static final String KEY_FIRST_TIME = "key_first_time";
    public static final String KEY_ACCEPT_TERM = "key_accept_term";
    public static final String KEY_VERSION_CODE = "key_version_code";
    public static final String KEY_TOTAL_APP = "key_total_app";

    public static final int REQUEST_CODE_APP_INFO = 1001;
    public static final int RESULT_DELETE = 2001;
    public static final int RESULT_IGNORE = 2002;

    public static final int REQUEST_READ_PHONE_STATE = 5001;
    public static final int REQUEST_READ_SMS = 5002;
    public static final int REQUEST_READ_CALL_LOG = 5004;

    public final static int APP_STATUS_SAFE = 0;
    public final static int APP_STATUS_WARNING_YELLOW = 1;
    public final static int APP_STATUS_WARNING_ORANGE = 2;
    public final static int APP_STATUS_WARNING_RED = 3;

    public final static int APP_STATUS_WAIT_FOR_SEND_HASH = 4;
    public final static int APP_STATUS_WAIT_FOR_SEND_APK = 5;
}
