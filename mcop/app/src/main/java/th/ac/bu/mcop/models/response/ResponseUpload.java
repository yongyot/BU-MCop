package th.ac.bu.mcop.models.response;

/**
 * Created by jeeraphan on 2/12/17.
 */

public class ResponseUpload {
    private int IsCompleted;
    private String Message;

    public int getIsCompleted() {
        return IsCompleted;
    }

    public void setIsCompleted(int isCompleted) {
        IsCompleted = isCompleted;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }
}
