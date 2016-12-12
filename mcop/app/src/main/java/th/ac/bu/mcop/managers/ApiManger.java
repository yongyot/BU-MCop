package th.ac.bu.mcop.managers;

/**
 * Created by jeeraphan on 12/12/16.
 */

public class ApiManger {

    private static ApiManger sApiManger;

    public ApiManger getInstance(){
        if (sApiManger == null){
            sApiManger = new ApiManger();
        }
        return sApiManger;
    }

    public ApiManger(){

    }
}
