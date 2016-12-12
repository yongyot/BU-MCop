package th.ac.bu.mcop.modules.api;

import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import th.ac.bu.mcop.modules.VirusTotalResponse;

/**
 * Created by jeeraphan on 12/12/16.
 */

public interface APIService {

    @GET("index.php/file/report")
    Call<ArrayList<VirusTotalResponse>> getReport(
            @Query("apikey") String apiKey,
            @Query("resource[]") ArrayList<String> resourceList
    );
}
