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

import java.io.File;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import th.ac.bu.mcop.R;
import th.ac.bu.mcop.models.AppsInfo;
import th.ac.bu.mcop.models.realm.AppRealm;
import th.ac.bu.mcop.models.response.ReportHeaderModel;
import th.ac.bu.mcop.models.response.ReportModel;
import th.ac.bu.mcop.models.response.ResponseDataModel;
import th.ac.bu.mcop.models.response.ResponseModel;
import th.ac.bu.mcop.modules.HashGenManager;
import th.ac.bu.mcop.modules.api.ApiManager;
import th.ac.bu.mcop.modules.api.ApplicationInfoManager;
import th.ac.bu.mcop.utils.Constants;
import th.ac.bu.mcop.utils.Settings;
import th.ac.bu.mcop.utils.SharePrefs;
import th.ac.bu.mcop.widgets.NotificationView;

public class InitializationActivity extends AppCompatActivity implements HashGenManager.OnHashGenListener{

    private ImageView mCircleImageview;
    private Handler mHandler = new Handler();
    private ArrayList<AppsInfo> mAppInfos;

    private int mAPKCounter = 0;
    private int mAPKSize = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initialization);

        mCircleImageview = (ImageView) findViewById(R.id.circle_imageview);
        mAppInfos = new ArrayList<>();
        animateScal();

        Settings.loadSetting(this);
        insertAppsToRealm();
    }

    private void animateScal(){

        AnimatorSet animator = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.scal3);
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

    private void insertAppsToRealm(){

        Log.d(Settings.TAG, "insertAppsToRealm");

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<AppRealm> appRealms = AppRealm.getAll();
                if (appRealms.size() <= 0){

                    ArrayList<ApplicationInfo> applicationInfos = ApplicationInfoManager.getTotalApplicationUsingInternet(InitializationActivity.this);
                    ArrayList<AppsInfo> appsInfos = new ArrayList<>();
                    int count = 0;
                    for (ApplicationInfo applicationInfo : applicationInfos){

                        AppsInfo appsInfo = new HashGenManager().getPackageInfo(applicationInfo.packageName, getBaseContext());
                        appsInfo.setName(applicationInfo.loadLabel(getPackageManager()).toString());

                        //appsInfo.setAppStatus(Constants.APP_STATUS_SAFE); // set default

                        if (count % 4 == 0){
                            appsInfo.setAppStatus(Constants.APP_STATUS_SAFE);
                        } else if (count % 4 == 1){
                            appsInfo.setAppStatus(Constants.APP_STATUS_WARNING_YELLOW);
                        } else if (count % 4 == 2){
                            appsInfo.setAppStatus(Constants.APP_STATUS_WARNING_ORANGE);
                        } else if (count % 4 == 3){
                            appsInfo.setAppStatus(Constants.APP_STATUS_WARNING_RED);
                        }

                        appsInfos.add(appsInfo);

                        count++;

                    }

                    AppRealm.save(appsInfos);
                }

                initHasFile();
            }
        }).start();
    }

    private void initHasFile(){
        Log.d(Settings.TAG, "initHasFile : " + Settings.sMacAddress);
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
        } else {
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
        Log.d(Settings.TAG, "getReport");
        ApiManager.getInstance().getReport(new Callback<ResponseModel<ReportHeaderModel<ReportModel>>>() {
            @Override
            public void onResponse(Call<ResponseModel<ReportHeaderModel<ReportModel>>> call, Response<ResponseModel<ReportHeaderModel<ReportModel>>> response) {
                if (response != null){
                    ResponseModel<ReportHeaderModel<ReportModel>> reportModel = response.body();

                    ArrayList<ReportModel> sendApkApps = new ArrayList<>();
                    ArrayList<ReportModel> safeApps = new ArrayList<>();
                    ArrayList<ReportModel> warningApps = new ArrayList<>();

                    Log.d(Settings.TAG, "getReport reportModel isResult: " + reportModel.isResult());
                    Log.d(Settings.TAG, "getReport reportModel getError: " + reportModel.getError());

                    if (reportModel.getResponse() != null && (reportModel.getResponse().getData() != null || reportModel.getResponse().getData().size() > 0)){

                        Log.d(Settings.TAG, "getReport reportModel getData : " + reportModel.getResponse().getData());

                        // size = 0 because safe all
                        if (reportModel.getResponse().getData().size() > 0){
                            for (ReportModel model : reportModel.getResponse().getData()){

                                int percent = model.getDetectionPercentage();

                                if (percent == 0){
                                    sendApkApps.add(model);

                                    //update status app status send apk
                                    AppsInfo appInfo = AppRealm.getAppWithHash(model.getResource());
                                    Log.d(Settings.TAG, "appInfo1");
                                    appInfo.setAppStatus(Constants.APP_STATUS_SEND_APK);
                                    AppRealm.update(appInfo);

                                } else {
                                    warningApps.add(model);

                                    // update status app warning
                                    AppsInfo appInfo = AppRealm.getAppWithHash(model.getResource());
                                    Log.d(Settings.TAG, "appInfo2");
                                    if (percent > 25){
                                        appInfo.setAppStatus(Constants.APP_STATUS_WARNING_YELLOW);
                                    } else if (percent > 50){
                                        appInfo.setAppStatus(Constants.APP_STATUS_WARNING_ORANGE);
                                    } else if (percent > 75){
                                        appInfo.setAppStatus(Constants.APP_STATUS_WARNING_RED);
                                    }

                                    AppRealm.update(appInfo);

                                }
                            }
                        }
                    }

                    mAPKSize = sendApkApps.size();

                    Log.d(Settings.TAG, "sendApkApps size : " + sendApkApps.size());
                    Log.d(Settings.TAG, "safeApps size    : " + safeApps.size());
                    Log.d(Settings.TAG, "warningApps size : " + warningApps.size());

                    // upload apk for check again
                    if (sendApkApps.size() > 0){
                        for (ReportModel apk : safeApps){
                            sendApk(apk);
                        }
                    } else {
                        startHomeActivity();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseModel<ReportHeaderModel<ReportModel>>> call, Throwable t) {
                startHomeActivity();
            }
        }, getAllHashReport());
    }

    private void sendApk(ReportModel sendApkApp){
        Log.d(Settings.TAG, "sendApk");

        mAPKCounter++;

        String packageName = getPankageNameWithHash(sendApkApp.getResource());
        Log.d(Settings.TAG, "packageName: " + packageName);
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            String pathString = packageInfo.applicationInfo.sourceDir;
            File file = new File(pathString);

            Log.d(Settings.TAG, "file exists: " + file.exists());

            // create RequestBody instance from file
            Uri uri = Uri.fromFile(file);
            Log.d(Settings.TAG, "uri: " + uri);

            final RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);

            ApiManager.getInstance().uploadAPK(new Callback<ResponseModel<ResponseDataModel<ReportModel>>>() {
                @Override
                public void onResponse(Call<ResponseModel<ResponseDataModel<ReportModel>>> call, Response<ResponseModel<ResponseDataModel<ReportModel>>> response) {
                    Log.d(Settings.TAG, "sendApk onResponse");

                    ResponseModel<ResponseDataModel<ReportModel>> reportModel = response.body();

                    if (reportModel != null){

                        Log.d(Settings.TAG, "sendApk isResult: " + reportModel.isResult());
                        Log.d(Settings.TAG, "sendApk getError: " + reportModel.getError());

                        Log.d(Settings.TAG, "sendApk data: " + reportModel.getResponse().getData());

                        if (reportModel.getResponse().getData() != null){

                            if (reportModel.getResponse().getData().getResponseCode() == 0){
                                // update status app warning
                                AppsInfo appInfo = AppRealm.getAppWithHash(reportModel.getResponse().getData().getResource());
                                appInfo.setAppStatus(Constants.APP_STATUS_SEND_HASH);
                                AppRealm.update(appInfo);
                            }
                        }
                    }

                    if (mAPKCounter > mAPKSize){
                        startHomeActivity();
                    }
                }

                @Override
                public void onFailure(Call<ResponseModel<ResponseDataModel<ReportModel>>> call, Throwable t) {
                    Log.d(Settings.TAG, "sendApk onFailure: " + t.getMessage());

                    if (mAPKCounter > mAPKSize){
                        startHomeActivity();
                    }
                }
            }, requestBody);

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

                //Log.d(Settings.TAG, app.getPackageName() +  " hash: " + app.getHash());
            }
        }
        //Log.d(Settings.TAG, "hash: " + hashs);
        return hashs;
    }

    private void startHomeActivity(){

        SharePrefs.setPreference(this, Constants.KEY_ACCEPT_TERM, true);

        mHandler.postDelayed(new Runnable() {
            public void run() {
                Intent intent = new  Intent(InitializationActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}
