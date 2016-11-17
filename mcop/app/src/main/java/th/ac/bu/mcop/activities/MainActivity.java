package th.ac.bu.mcop.activities;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.net.NetworkInterface;

import th.ac.bu.mcop.R;
import th.ac.bu.mcop.modules.HashGen;
import th.ac.bu.mcop.modules.StatsFileManager;
import th.ac.bu.mcop.services.BackgroundService;
import th.ac.bu.mcop.utils.Constants;
import th.ac.bu.mcop.utils.Settings;
import th.ac.bu.mcop.utils.SharePrefs;
import th.ac.bu.mcop.widgets.NotificationView;

/**
 * Created by jeeraphan on 10/28/16.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mUsageAccessButton;
    private Button mStartStopServiceButton;
    private TextView mWarningTextView;
    private TextView mTextView;
    private TextView mCounterTextView;
    private TextView mFileSizeTextView;
    private TextView mLastTimeTextView;
    private TextView mMacAddressTextView;

    private boolean isAppPaused = false;
    private static Context mContext;
    private Runnable mRunnable;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsageAccessButton = (Button)findViewById(R.id.btnUsageAccess);
        mUsageAccessButton.setOnClickListener(this);

        mStartStopServiceButton = (Button)findViewById(R.id.btnStartExtracting);
        mStartStopServiceButton.setOnClickListener(this);

        mTextView = (TextView)findViewById(R.id.tvWarningText);
        mWarningTextView = (TextView)findViewById(R.id.tvWarning);
        mCounterTextView = (TextView) findViewById(R.id.tvCounter);
        mFileSizeTextView = (TextView) findViewById(R.id.tvFileSize);
        mLastTimeTextView = (TextView) findViewById(R.id.tvLastTime);
        mMacAddressTextView = (TextView) findViewById(R.id.tvMacAddress);

        mContext = getApplicationContext();
        mHandler = new Handler();
    }

    @Override
    protected void onStart() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageRecevier, new IntentFilter(Constants.INTENT_FILTER));
        isAppPaused = false;
        super.onStart();

        setView();
        boolean isFirstTime = SharePrefs.getPreferenceBoolean(mContext, "firstTime", false);
        if (!isFirstTime){
            initHasFile();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(mMessageRecevier);
        } catch (Exception ex) {
            Log.d(Settings.TAG, ex.getMessage());
        }

        isAppPaused = true;
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.btnUsageAccess){
            Intent intent = new Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);

        } else if (view.getId() == R.id.btnStartExtracting){

            if(!isServiceRunning(BackgroundService.class)) { //service is stopped. Start it.
                Intent intent = new Intent(this, BackgroundService.class);
                try {
                    stopService(intent);
                } catch (Exception ex) {
                    Log.d(Settings.TAG, ex.toString());
                }

                startService(intent);
                mStartStopServiceButton.setText("Stop Collecting Stats..");
                mWarningTextView.setTextColor(Color.GREEN);
                mWarningTextView.setText("Status");
                mTextView.setText("Collecting data....");

                setCounter();
            } else {
                stopService(new Intent(this,BackgroundService.class));
                BackgroundService.sStopRequest = true;

                mStartStopServiceButton.setText("Start Collecting Stats..");
                mWarningTextView.setTextColor(Color.parseColor("#FFA500"));
                mWarningTextView.setText("Status");
                mTextView.setText("Ready for Collection.");

                mHandler.removeCallbacks(mRunnable);
            }
        }
    }

    private BroadcastReceiver mMessageRecevier = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(!isAppPaused) {
                //update UI
                if(isServiceRunning(BackgroundService.class)) {
                    mStartStopServiceButton.setText("Stop Collecting Data");
                    mWarningTextView.setTextColor(Color.GREEN);

                    mWarningTextView.setText("Status:");
                    mTextView.setText("Collecting data....");
                    setCounter();
                } else {
                    mStartStopServiceButton.setText("Start Collecting Data");
                    mWarningTextView.setTextColor(Color.parseColor("#FFA500"));    //orange color
                    mWarningTextView.setText("Status:");
                    mTextView.setText("Ready for Collection.");
                }
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

    public void setCounter() {

        try {
            mCounterTextView.setText("Session: " + Integer.toString(BackgroundService.sCounter));
            mFileSizeTextView.setText("File Size: " + Long.toString(StatsFileManager.getFileSize()) + " KB");
            mLastTimeTextView.setText("เวลาที่ส่งไฟล์ล่าสุด: " + SharePrefs.getPreferenceString(mContext, "las_time_upload", ""));

            mHandler.postDelayed(mRunnable = new Runnable() {
                @Override
                public void run() {

                    mCounterTextView.setText("Session: " + Integer.toString(BackgroundService.sCounter));
                    mFileSizeTextView.setText("File Size: " + Long.toString(StatsFileManager.getFileSize()) + " KB");
                    mLastTimeTextView.setText("เวลาที่ส่งไฟล์ล่าสุด: " + SharePrefs.getPreferenceString(mContext, "las_time_upload", ""));
                    mHandler.postDelayed(this, Settings.sInterval * 1000);


                }
            }, Settings.sInterval * 1000);

        } catch(Exception ex) {
            NotificationView.show(mContext, "Error while setting counter text");
        }
    }

    private void setView(){

        mMacAddressTextView.setText("ID: " + Settings.sMacAddress);

        if(Settings.isUsageAccessGranted(mContext)){      //if user access is granted set the start button to visible and user access to invisible.

            mStartStopServiceButton.setVisibility(View.VISIBLE);
            mUsageAccessButton.setVisibility(View.INVISIBLE);

            if(isServiceRunning(BackgroundService.class)){ //access is already granted and service is running.

                mStartStopServiceButton.setText("Stop Collecting Data");
                mWarningTextView.setTextColor(Color.GREEN);

                mWarningTextView.setText("Status:");
                mTextView.setText("Collecting data....");
                setCounter();
            } else {
                mStartStopServiceButton.setText("Start Collecting Data");
                mWarningTextView.setTextColor(Color.parseColor("#FFA500"));    //orange color
                mWarningTextView.setText("Status:");
                mTextView.setText("Ready for Collection.");
            }
        } else {                                            //user access is not granted. Set the btnStart to invisble and usage access to visible.

            mStartStopServiceButton.setVisibility(View.INVISIBLE);
            mUsageAccessButton.setVisibility(View.VISIBLE);
            mWarningTextView.setText("Warning:");
            mWarningTextView.setTextColor(Color.RED);
            mTextView.setText("Please turn on usage access first.");
        }
    }

    private void initHasFile(){

        if(Settings.sMacAddress != null) {

            try {
                if (NetworkInterface.getByName("rmnet0") == null) {
                    NotificationView.show(mContext, "Data interface error.");
                }
            } catch(Exception ex) {
                Log.d(Settings.TAG, "Can not find data interface name. Details: " + ex.toString());
            }

            new Thread(new Runnable() {
                public void run() {
                    SharePrefs.setPreference(mContext, "firstTime", true);
                    HashGen hashGen = new HashGen();
                    hashGen.getAllAppInfo(mContext);

                }
            }).start();
        }
    }
}