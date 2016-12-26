package th.ac.bu.mcop.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jeeraphan on 12/26/16.
 */

public class ReportModel{

    @SerializedName("resource")
    String resource;

    @SerializedName("detection_ratio")
    String detectionRatio;

    @SerializedName("detection_percentage")
    int detectionPercentage;

    @SerializedName("scan")
    String scan;

    @SerializedName("response_code")
    int responseCode;

    @SerializedName("verbose_msg")
    String verboseMsg;
}
