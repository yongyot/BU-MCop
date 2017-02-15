package th.ac.bu.mcop.models.response;

/**
 * Created by jeeraphan on 12/26/16.
 */

public class ResponseDataModel<T extends Object>{

    ReportModel data;

    public ReportModel getData() {
        return data;
    }

    public void setData(ReportModel data) {
        this.data = data;
    }
}
