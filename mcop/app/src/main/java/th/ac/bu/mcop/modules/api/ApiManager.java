package th.ac.bu.mcop.modules.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import th.ac.bu.mcop.models.request.UploadRequest;
import th.ac.bu.mcop.models.response.ReportHeaderModel;
import th.ac.bu.mcop.models.response.ReportModel;
import th.ac.bu.mcop.models.response.ResponseDataModel;
import th.ac.bu.mcop.models.response.ResponseModel;
import th.ac.bu.mcop.models.response.ResponseUpload;

/**
 * Created by jeeraphan on 12/12/16.
 */

public class ApiManager {

    public static final String API_KEY = "78660282aeaedccc679bb9b2e33095916ff8d356be6e77d05ef04a284c42deff";;
    private final String BASE_API = "http://mobile-monitoring.bu.ac.th/";
    private static ApiManager sApiManger;
    private Gson mGson;
    private Retrofit mRetrofit;
    private APIService mApiService;

    public static ApiManager getInstance(){
        if (sApiManger == null){
            sApiManger = new ApiManager();
        }
        return sApiManger;
    }

    public ApiManager(){
        mGson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_API)
                .addConverterFactory(GsonConverterFactory.create(mGson))
                .build();
        mApiService = mRetrofit.create(APIService.class);
    }

    public void getReport(Callback<ResponseModel<ReportHeaderModel<ReportModel>>> callback, String resources){

        Call<ResponseModel<ReportHeaderModel<ReportModel>>> call = mApiService.getReport(API_KEY, resources);
        call.enqueue(callback);
    }

    public void uploadAPK(Callback<ResponseModel> callback, RequestBody apkBody, RequestBody apiBody){
        Call<ResponseModel> call = mApiService.uploadAPK(apiBody, apkBody);
        call.enqueue(callback);
    }

    public void uploadHashCodeByte(Callback<ResponseUpload> callback, RequestBody requestBody){
        Call<ResponseUpload> call = mApiService.uploadHashCodeByte(requestBody);
        call.enqueue(callback);
    }

    public void uploadNetDataByte(Callback<ResponseUpload> callback, RequestBody requestBody){
        Call<ResponseUpload> call = mApiService.uploadNetDataByte(requestBody);
        call.enqueue(callback);
    }
}