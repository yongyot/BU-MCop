package th.ac.bu.mcop.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ReportHeaderModel<T extends Object>{

    @SerializedName("result")
    boolean result;
    @SerializedName("error")
    String error;
    @SerializedName("data")
    T data;

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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
