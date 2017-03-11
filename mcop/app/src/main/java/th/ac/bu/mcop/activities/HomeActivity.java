package th.ac.bu.mcop.activities;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import th.ac.bu.mcop.R;
import th.ac.bu.mcop.broadcastreceiver.IntenetReceiver;
import th.ac.bu.mcop.models.realm.AppRealm;
import th.ac.bu.mcop.modules.api.ApplicationInfoManager;
import th.ac.bu.mcop.services.BackgroundService;
import th.ac.bu.mcop.utils.Constants;
import th.ac.bu.mcop.utils.Settings;

/**
 * Created by jeeraphan on 12/10/16.
 */

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
        mTestSMSTextView.setVisibility(View.GONE);

        mManageAppButton.setOnClickListener(this);
        mAboutButton.setOnClickListener(this);
        mStartLogButton.setOnClickListener(this);

        ArrayList<ApplicationInfo> applicationInstalled = ApplicationInfoManager.getTotalApplication(this);
        ArrayList<ApplicationInfo> applicationUsingInternet = ApplicationInfoManager.getTotalApplicationUsingInternet(this);

        mTotalInstalledAppTextView.setText(applicationInstalled.size() + "");
        mAppUsingInternetTextView.setText(applicationUsingInternet.size() + "");

        mHandler = new Handler();
        mIntenetReceiver = new IntenetReceiver();
    }

    @Override
    protected void onStart() {
        super.onStart();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageRecevier, new IntentFilter(Constants.INTENT_FILTER_UPDATE_UI));
        LocalBroadcastManager.getInstance(this).registerReceiver(mIntenetReceiver, new IntentFilter(Constants.INTENT_FILTER_INTERNET));

        isAppPaused = false;
        super.onStart();
        Settings.loadSetting(this);
        setView();
        setAppSafeOrNotView();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(mMessageRecevier);
            unregisterReceiver(mIntenetReceiver);
        } catch (Exception ex) {
            Log.d(Settings.TAG, ex.getMessage());
        }

        isAppPaused = true;
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

        } else if (view.getId() == R.id.start_log_button){

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

            setView();
        }
    }

    private void setView(){

        if(Settings.isUsageAccessGranted(this)){

            if(isServiceRunning(BackgroundService.class)){

                mStartLogButton.setText("Stop Collecting Data");
                mMessageTextView.setText("Collecting data....");
                mMessageTextView.setTextColor(Color.GREEN);

            } else {

                mStartLogButton.setText("Start Collecting Data");
                mMessageTextView.setText("Ready for Collection.");
                mMessageTextView.setTextColor(Color.parseColor("#FFA500")); //orange color
            }
        } else {

            mStartLogButton.setText("Turn on");
            mMessageTextView.setText("Please turn on usage access first.");
            mMessageTextView.setTextColor(Color.RED);

            new AlertDialog
                    .Builder(this)
            .setTitle(getString(R.string.app_name))
            .setMessage("Please turn on usage access first.")
            .setPositiveButton(getString(R.string.label_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS);
                    startActivity(intent);
                }
            })
            .setNegativeButton(getString(R.string.label_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            }).show();
        }

    }

    private void setAppSafeOrNotView(){

        ArrayList<AppRealm> warningApps = AppRealm.getWarningApps();

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
                setView();
            }
        }
    };

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
