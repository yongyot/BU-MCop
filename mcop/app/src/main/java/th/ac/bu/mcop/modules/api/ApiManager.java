package th.ac.bu.mcop.modules.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import th.ac.bu.mcop.models.response.ReportHeaderModel;
import th.ac.bu.mcop.models.response.ReportModel;
import th.ac.bu.mcop.models.response.ResponseModel;
import th.ac.bu.mcop.modules.VirusTotalResponse;

/**
 * Created by jeeraphan on 12/12/16.
 */

public class ApiManager {

    private final String API_KEY = "78660282aeaedccc679bb9b2e33095916ff8d356be6e77d05ef04a284c42deff";;
    private final String BASE_API = "http://mobile-monitoring.bu.ac.th/api/";
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

    public void getReport(Callback<ResponseModel<ReportHeaderModel<ReportModel>>> callback, ArrayList<String> resourceList){

        Call<ResponseModel<ReportHeaderModel<ReportModel>>> call = mApiService.getReport(API_KEY, resourceList);
        call.enqueue(callback);
    }

    public void uploadHashGen(Callback<String> callback){
        Call<String> call = mApiService.uploadHashGen("file_name", new File("path"));
        call.enqueue(callback);
    }
}