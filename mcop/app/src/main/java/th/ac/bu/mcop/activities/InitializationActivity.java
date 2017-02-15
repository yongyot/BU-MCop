package th.ac.bu.mcop.activities;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import org.json.JSONObject;

import java.io.File;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import th.ac.bu.mcop.R;
import th.ac.bu.mcop.models.AppsInfo;
import th.ac.bu.mcop.models.response.ReportHeaderModel;
import th.ac.bu.mcop.models.response.ReportModel;
import th.ac.bu.mcop.models.response.ResponseDataModel;
import th.ac.bu.mcop.models.response.ResponseModel;
import th.ac.bu.mcop.models.response.ResponseUpload;
import th.ac.bu.mcop.modules.HashGenManager;
import th.ac.bu.mcop.modules.VirusTotalResponse;
import th.ac.bu.mcop.modules.api.ApiManager;
import th.ac.bu.mcop.utils.Constants;
import th.ac.bu.mcop.utils.Settings;
import th.ac.bu.mcop.utils.SharePrefs;
import th.ac.bu.mcop.widgets.NotificationView;

/**
 * Created by jeeraphan on 12/10/16.
 */

public class InitializationActivity extends AppCompatActivity implements HashGenManager.OnHashGenListener{

    private ImageView mCircleImageview;
    private Handler mHandler = new Handler();
    private ArrayList<AppsInfo> mAppInfos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initialization);

        mCircleImageview = (ImageView) findViewById(R.id.circle_imageview);
        mAppInfos = new ArrayList<>();
        animateScal();

        Settings.loadSetting(this);
        initHasFile();
    }

    private void animateScal(){

        AnimatorSet animator = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.scal);
        animator.setTarget(mCircleImageview);
        animator.start();
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {}

            @Override
            public void onAnimationEnd(Animator animator) {
                animateScal();
            }

            @Override
            public void onAnimationCancel(Animator animator) {}

            @Override
            public void onAnimationRepeat(Animator animator) {}
        });
    }

    private void initHasFile(){

        Log.d(Settings.TAG, "initHasFile: " + Settings.sMacAddress);

        if(Settings.sMacAddress != null) {

            try {
                if (NetworkInterface.getByName("rmnet0") == null) {
                    NotificationView.show(this, "Data interface error.");
                }
            } catch(Exception ex) {
                Log.d(Settings.TAG, "Can not find data interface name. Details: " + ex.toString());
            }

            new Thread(new Runnable() {
                public void run() {
                    SharePrefs.setPreference(getBaseContext(), Constants.KEY_FIRST_TIME, true);
                    HashGenManager hashGen = new HashGenManager();
                    hashGen.setOnHashGenListener(InitializationActivity.this);
                    hashGen.getAllAppInfo(getBaseContext());
                }
            }).start();
        }
    }

    /***********************************************
     OnHashGenListener
     ************************************************/

    @Override
    public void onHashGenFinished() {
        Log.d(Settings.TAG, "onHashGenFinished");
        getReport();
    }

    private void getReport(){

        ApiManager.getInstance().getReport(new Callback<ResponseModel<ReportHeaderModel<ReportModel>>>() {
            @Override
            public void onResponse(Call<ResponseModel<ReportHeaderModel<ReportModel>>> call, Response<ResponseModel<ReportHeaderModel<ReportModel>>> response) {
                if (response != null){
                    ResponseModel<ReportHeaderModel<ReportModel>> reportModel = response.body();

                    ArrayList<ReportModel> sendApkApps = new ArrayList<>();
                    ArrayList<ReportModel> safeApps = new ArrayList<>();
                    ArrayList<ReportModel> warningApps = new ArrayList<>();

                    for (ReportModel model : reportModel.getResponse().getData()){
                        if (model.getResponseCode() == 0){
                            sendApkApps.add(model);
                        } else if (model.getDetectionPercentage() > 50){
                            warningApps.add(model);
                        } else {
                            safeApps.add(model);
                        }
                    }

                    Log.d(Settings.TAG, "sendApkApps size: " + sendApkApps.size());
                    Log.d(Settings.TAG, "safeApps size: " + safeApps.size());
                    Log.d(Settings.TAG, "warningApps size: " + warningApps.size());

                    // upload apk for check again
                    //if (sendApkApps.size() > 0){
                        sendApk(sendApkApps);
                    //}
                }

                //startHomeActivity();
            }

            @Override
            public void onFailure(Call<ResponseModel<ReportHeaderModel<ReportModel>>> call, Throwable t) {
                startHomeActivity();
            }
        }, getAllHashReport());
    }

    private void sendApk(ArrayList<ReportModel> sendApkApps){

        Log.d(Settings.TAG, "sendApk");

        String packageName = "com.skype.raider";//getPankageNameWithHash(sendApkApps.get(0).getResource());
        Log.d(Settings.TAG, "packageName: " + packageName);
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            String pathString = packageInfo.applicationInfo.sourceDir;
            Log.d(Settings.TAG, "pathString: " + pathString);
            File file = new File(pathString);

            RequestBody apkbody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            RequestBody apikey = RequestBody.create(MediaType.parse("text/plain"), ApiManager.API_KEY);

            ApiManager.getInstance().uploadAPK(new Callback<ResponseModel>() {
                @Override
                public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                    Log.d(Settings.TAG, "onResponse");
                    if (response.body() != null){
                        Log.d(Settings.TAG, "isResult: " + response.body().isResult());
                        Log.d(Settings.TAG, "getError: " + response.body().getError());
                        //Log.d(Settings.TAG, "getResource: " + response.body().getResponse().getData().getResource());

                    }
                }

                @Override
                public void onFailure(Call<ResponseModel> call, Throwable t) {
                    Log.d(Settings.TAG, "onFailure: " + t.getMessage());
                }
            }, apikey, apkbody);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private String getPankageNameWithHash(String hash){

        for (AppsInfo appInfo : mAppInfos){
            if (appInfo.getHash().equals(hash)){
                return appInfo.getPackageName();
            }
        }

        return "";
    }

    public String getAllHashReport() {

        String selfApk = "th.ac.bu.mcop";
        PackageManager packageManager = getPackageManager();
        final List<ApplicationInfo> apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        String hashs = "";

        for (ApplicationInfo appInfo : apps) {

            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 && !appInfo.packageName.equals(selfApk)) {

                HashGenManager hashGen = new HashGenManager();
                AppsInfo app = hashGen.getPackageInfo(appInfo.packageName, getBaseContext());
                hashs += app.getHash() + ",";

                mAppInfos.add(app);
            }
        }
        Log.d(Settings.TAG, "hash: " + hashs);
        return hashs;
    }

    private void startHomeActivity(){
        mHandler.postDelayed(new Runnable() {
            public void run() {
                Intent intent = new  Intent(InitializationActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}
