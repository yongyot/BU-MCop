package th.ac.bu.mcop.modules.api;

import java.io.File;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import th.ac.bu.mcop.models.response.ReportHeaderModel;
import th.ac.bu.mcop.models.response.ReportModel;
import th.ac.bu.mcop.models.response.ResponseModel;


/**
 * Created by jeeraphan on 12/12/16.
 */

public interface APIService {

    @GET("index.php/file/report")
    Call<ResponseModel<ReportHeaderModel<ReportModel>>> getReport(
            @Query("apikey") String apiKey,
            @Query("resource[]") ArrayList<String> resourceList
    );

    @POST("http://mobile-monitoring.bu.ac.th//hash.aspx")
    Call<String> uploadHashGen(
            @Query("file_name") String uploadFile,
            @Query("File") File file
    );
}
