package th.ac.bu.mcop.models.response;

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
