package th.ac.bu.mcop.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jeeraphan on 12/26/16.
 */

public class ResponseModel<T extends Object> {

    @SerializedName("result")
    int result;

    @SerializedName("error")
    int error;

    @SerializedName("response")
    T data;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
