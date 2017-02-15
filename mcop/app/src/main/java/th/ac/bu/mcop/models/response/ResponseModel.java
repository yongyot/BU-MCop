package th.ac.bu.mcop.models.response;

/**
 * Created by jeeraphan on 12/26/16.
 */

public class ResponseModel<T extends Object> {

    boolean result;
    String error;
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
