package th.ac.bu.mcop.activities;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import th.ac.bu.mcop.R;
import th.ac.bu.mcop.models.AppsInfo;
import th.ac.bu.mcop.models.realm.AppRealm;
import th.ac.bu.mcop.models.response.ReportHeaderModel;
import th.ac.bu.mcop.models.response.ReportModel;
import th.ac.bu.mcop.models.response.ResponseModel;
import th.ac.bu.mcop.models.response.Scan;
import th.ac.bu.mcop.modules.HashGenManager;
import th.ac.bu.mcop.modules.api.ApiManager;
import th.ac.bu.mcop.modules.api.ApplicationInfoManager;
import th.ac.bu.mcop.utils.Constants;
import th.ac.bu.mcop.utils.Settings;
import th.ac.bu.mcop.utils.SharePrefs;
import th.ac.bu.mcop.widgets.NotificationView;

public class InitializationActivity extends AppCompatActivity implements HashGenManager.OnHashGenListener {

    private ImageView mCircleImageview, mRadarCircleImage1, mRadarCircleImage2, mRadarCircleImage3;
    private Handler mHandler = new Handler();
    private ArrayList<AppsInfo> mAppInfos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initialization);

        mCircleImageview = (ImageView) findViewById(R.id.circle_imageview);
        mRadarCircleImage1 = (ImageView) findViewById(R.id.radar_circle_imageview1);
        mRadarCircleImage2 = (ImageView) findViewById(R.id.radar_circle_imageview2);
        mRadarCircleImage3 = (ImageView) findViewById(R.id.radar_circle_imageview3);
        mAppInfos = new ArrayList<>();
        animateScal();
        animateRatate();

        Settings.loadSetting(this);
        insertAppsToRealm();
    }

    private void animateScal(){

        AnimatorSet animator1 = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.scal3);
        animator1.setTarget(mRadarCircleImage1);
        animator1.start();

        AnimatorSet animator2 = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.scal3);
        animator2.setTarget(mRadarCircleImage2);
        animator2.setStartDelay(300);
        animator2.start();

        AnimatorSet animator3 = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.scal3);
        animator3.setTarget(mRadarCircleImage3);
        animator3.setStartDelay(600);
        animator3.start();
    }

    private void animateRatate(){

        AnimatorSet animator = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.rotate);
        animator.setTarget(mCircleImageview);
        animator.start();
    }

    private void insertAppsToRealm(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<AppRealm> appRealms = AppRealm.getAll();
                if (appRealms.size() <= 0){

                    ArrayList<ApplicationInfo> applicationInfos = ApplicationInfoManager.getTotalApplicationUsingInternet(InitializationActivity.this);
                    ArrayList<AppsInfo> appsInfos = new ArrayList<>();
                    for (ApplicationInfo applicationInfo : applicationInfos){

                        if (applicationInfo.packageName != null && applicationInfo.packageName.length() > 0){

                            AppsInfo appsInfo = new HashGenManager().getPackageInfo(applicationInfo, getBaseContext());
                            appsInfo.setName(applicationInfo.loadLabel(getPackageManager()).toString());
                            appsInfo.setAppStatus(Constants.APP_STATUS_SAFE); // set default
                            appsInfos.add(appsInfo);
                        }
                    }

                    AppRealm.save(appsInfos);
                    SharePrefs.setPreference(InitializationActivity.this, Constants.KEY_TOTAL_APP, appsInfos.size());
                }

                initHasFile();
            }
        }).start();
    }

    private void initHasFile(){
        Log.d(Settings.TAG, "initHasFile");
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
        sendHash(getAllHashReport());
    }

    private void sendHash(String resource){

        ApiManager.getInstance().getReport(new Callback<ResponseModel<ReportHeaderModel<ArrayList<ReportModel>>>>() {
            @Override
            public void onResponse(Call<ResponseModel<ReportHeaderModel<ArrayList<ReportModel>>>> call, Response<ResponseModel<ReportHeaderModel<ArrayList<ReportModel>>>> response) {

                Log.d(Settings.TAG, "sendHash: " + response);

                if (response == null){
                    startHomeActivity();
                    return;
                }

                if (response.body() == null){
                    startHomeActivity();
                    return;
                }

                if (response.body().getResponse() == null){
                    startHomeActivity();
                    return;
                }

                if (response.body().getResponse().getData() == null){
                    startHomeActivity();
                    return;
                }

                if (response.body().getResponse().getData().size() <= 0){ // size = 0 because safe all
                    startHomeActivity();
                    return;
                }

                ArrayList<ReportModel> reportModels = response.body().getResponse().getData();

                int countSendApkApp = 0;
                int countSafeApps = 0;
                int countWarning = 0;

                for (ReportModel model : reportModels){

                    AppsInfo appInfo = AppRealm.getAppWithHash(model.getResource());

                    // response code 0 is need send apk again
                    // response code 1 is scan success
                    if (model.getResponseCode() == 0){
                        if (appInfo != null) {
                            appInfo.setAppStatus(Constants.APP_STATUS_WAIT_FOR_SEND_APK);
                            countSendApkApp++;
                        }
                    } else {

                        if (appInfo != null) {

                            float percent = model.getDetectionPercentage();
                            if (percent > 75){
                                appInfo.setAppStatus(Constants.APP_STATUS_WARNING_RED);
                                countWarning++;
                            } else if (percent > 50){
                                appInfo.setAppStatus(Constants.APP_STATUS_WARNING_ORANGE);
                                countWarning++;
                            } else if (percent > 25){
                                appInfo.setAppStatus(Constants.APP_STATUS_WARNING_YELLOW);
                                countWarning++;
                            } else {
                                appInfo.setAppStatus(Constants.APP_STATUS_SAFE);
                                countSafeApps++;
                            }

                            String scanString = "";
                            for (Scan scan : model.getScan()) {
                                scanString += scan.getEngien() + " : " + scan.getFound() + ",";
                            }
                            Log.d(Settings.TAG, "scanString: " + scanString);
                            appInfo.setScan(scanString);
                            AppRealm.update(appInfo);
                        }
                    }
                }

                Log.d(Settings.TAG, "sendApkApps : " + countSendApkApp);
                Log.d(Settings.TAG, "safeApps    : " + countSafeApps);
                Log.d(Settings.TAG, "warningApps : " + countWarning);

                startHomeActivity();
            }

            @Override
            public void onFailure(Call<ResponseModel<ReportHeaderModel<ArrayList<ReportModel>>>> call, Throwable t) {
                Log.d(Settings.TAG, "onFailure: " + t.getLocalizedMessage());
                startHomeActivity();
            }
        }, resource);
    }

    public String getAllHashReport() {

        String selfApk = "th.ac.bu.mcop";
        PackageManager packageManager = getPackageManager();
        final List<ApplicationInfo> apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        String hashs = "";

        for (ApplicationInfo appInfo : apps) {

            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 && !appInfo.packageName.equals(selfApk)) {

                HashGenManager hashGen = new HashGenManager();
                AppsInfo app = hashGen.getPackageInfo(appInfo, getBaseContext());
                hashs += app.getHash() + ",";

                mAppInfos.add(app);

                Log.d(Settings.TAG, app.getPackageName() +  " : " + app.getHash());
            }
        }

        Log.d(Settings.TAG, hashs);

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
