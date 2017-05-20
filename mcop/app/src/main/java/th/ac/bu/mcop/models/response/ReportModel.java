package th.ac.bu.mcop.models.response;

import com.google.gson.annotations.SerializedName;

public class ReportModel {

    @SerializedName("resource")
    String resource;

    @SerializedName("detection_ratio")
    String detectionRatio;

    @SerializedName("detection_percentage")
    float detectionPercentage;

//    @SerializedName("scan")
//    String scan;

    @SerializedName("response_code")
    int responseCode;

    @SerializedName("verbose_msg")
    String verboseMsg;

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getDetectionRatio() {
        return detectionRatio;
    }

    public void setDetectionRatio(String detectionRatio) {
        this.detectionRatio = detectionRatio;
    }

    public float getDetectionPercentage() {
        return detectionPercentage;
    }

    public void setDetectionPercentage(int detectionPercentage) {
        this.detectionPercentage = detectionPercentage;
    }

//    public String getScan() {
//        return scan;
//    }
//
//    public void setScan(String scan) {
//        this.scan = scan;
//    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getVerboseMsg() {
        return verboseMsg;
    }

    public void setVerboseMsg(String verboseMsg) {
        this.verboseMsg = verboseMsg;
    }
}
