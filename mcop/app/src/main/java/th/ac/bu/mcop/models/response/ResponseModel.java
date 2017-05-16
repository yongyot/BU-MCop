package th.ac.bu.mcop.models.response;

import com.google.gson.annotations.SerializedName;

public class ResponseModel<T extends Object> {

    @SerializedName("result")
    boolean result;
    @SerializedName("error")
    String error;
    @SerializedName("response")
    T response;

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

    public T getResponse() {
        return response;
    }

    public void setResponse(T response) {
        this.response = response;
    }
}
