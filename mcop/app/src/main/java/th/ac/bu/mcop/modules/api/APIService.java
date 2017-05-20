package th.ac.bu.mcop.modules.api;

import java.util.ArrayList;

import okhttp3.RequestBody;
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

public interface APIService {

    @GET("api/virustotal.php/hash")
    Call<ResponseModel<ReportHeaderModel<ArrayList<ReportModel>>>> getReport(
            @Query("apikey") String apiKey,
            @Query("md5") String resources
    );

    @Multipart
    @POST("api/virustotal.php/file")
    Call<ResponseModel<ResponseDataModel<ReportModel>>> uploadAPK(
            @Part("file\"; filename=\"file.apk\" ") RequestBody file
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

