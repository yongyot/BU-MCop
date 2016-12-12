package th.ac.bu.mcop.modules;

/**
 * Created by jeeraphan on 12/12/16.
 */

public class VirusTotalResponse {
    int response_code;
    String resource;
    String verbose_msg;

    public int getResponse_code() {
        return response_code;
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getVerbose_msg() {
        return verbose_msg;
    }

    public void setVerbose_msg(String verbose_msg) {
        this.verbose_msg = verbose_msg;
    }
}
