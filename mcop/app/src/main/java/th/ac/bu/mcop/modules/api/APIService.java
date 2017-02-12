package th.ac.bu.mcop.modules.api;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import th.ac.bu.mcop.models.request.UploadRequest;
import th.ac.bu.mcop.models.response.ReportHeaderModel;
import th.ac.bu.mcop.models.response.ReportModel;
import th.ac.bu.mcop.models.response.ResponseModel;
import th.ac.bu.mcop.models.response.ResponseUpload;


/**
 * Created by jeeraphan on 12/12/16.
 */

public interface APIService {

    @GET("index.php/file/report")
    Call<ResponseModel<ReportHeaderModel<ReportModel>>> getReport(
            @Query("apikey") String apiKey,
            @Query("resource[]") ArrayList<String> resourceList
    );

    @POST("http://mobile-monitoring.bu.ac.th/Mcop/api_uploadfile/api/uploadfile")
    Call<ResponseUpload> uploadNetDataByte(
            @Body RequestBody requestBody
    );

    @POST("http://mobile-monitoring.bu.ac.th/Mcop/api_uploadfile/api/hashcodefile")
    Call<ResponseUpload> uploadHashCodeByte(
            @Body RequestBody requestBody
    );
}

