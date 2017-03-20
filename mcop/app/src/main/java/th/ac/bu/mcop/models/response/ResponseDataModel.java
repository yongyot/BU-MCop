package th.ac.bu.mcop.models.response;

public class ResponseDataModel<T extends Object>{

    boolean result;
    String error;
    ReportModel data;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public ReportModel getData() {
        return data;
    }

    public void setData(ReportModel data) {
        this.data = data;
    }
}
