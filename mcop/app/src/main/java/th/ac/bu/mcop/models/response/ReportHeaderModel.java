package th.ac.bu.mcop.models.response;

import java.util.ArrayList;

public class ReportHeaderModel<T extends Object>{

    boolean result;
    String error;
    ArrayList<ReportModel> data;

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

    public ArrayList<ReportModel> getData() {
        return data;
    }

    public void setData(ArrayList<ReportModel> data) {
        this.data = data;
    }
}
