package th.ac.bu.mcop.modules.api;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import th.ac.bu.mcop.models.response.ReportHeaderModel;
import th.ac.bu.mcop.models.response.ReportModel;
import th.ac.bu.mcop.models.response.ResponseDataModel;
import th.ac.bu.mcop.models.response.ResponseModel;
import th.ac.bu.mcop.models.response.ResponseUpload;


/**
 * Created by jeeraphan on 12/12/16.
 */

public interface APIService {

    @GET("api/virustotal.php/hash")
    Call<ResponseModel<ReportHeaderModel<ReportModel>>> getReport(
            @Query("apikey") String apiKey,
            @Query("resource[]") String resources
    );

    @Multipart
    @POST("/api/virustotal.php/file")
    Call<ResponseModel> uploadAPK(
            @Part("apikey") RequestBody apikey,
            @Part("userfile\"; filename=\"pp.png\" ") RequestBody userfile
            );

    @POST("Mcop/api_uploadfile/api/uploadfile")
    Call<ResponseUpload> uploadNetDataByte(
            @Body RequestBody requestBody
    );

    @POST("Mcop/api_uploadfile/api/hashcodefile")
    Call<ResponseUpload> uploadHashCodeByte(
            @Body RequestBody requestBody
    );
}

