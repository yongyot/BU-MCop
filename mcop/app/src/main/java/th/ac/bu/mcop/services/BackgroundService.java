package th.ac.bu.mcop.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.util.Date;

import th.ac.bu.mcop.models.StatsFileManager;
import th.ac.bu.mcop.utils.Settings;

/**
 * Created by jeeraphan on 11/14/16.
 */

public class BackgroundService extends Service {

    public static boolean sIsServiceRunning;
    public static boolean sForceStop;
    public static volatile boolean sStopRequest;

    private Date mDate;
    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;
    private Context mContext;
    private Handler mHandler;
    private Runnable mRunnable;

    public static int counter = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initValue();
        initPowerManager();
        initSetting();
        initPathFile();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("emji", "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d("emji", "onTaskRemoved");
        super.onTaskRemoved(rootIntent);

        try {

            if (!sStopRequest){
                sendBroadcast(new Intent("YouWillNeverKillMe"));
            }

            if(mWakeLock.isHeld()){
                mWakeLock.release();
            }

            mHandler.removeCallbacks(mRunnable);
            stopForeground(true);
            sIsServiceRunning = false;

            stopSelf();
        } catch (Exception ex) {
            Log.d("emji", "error " + ex.toString());
        }
    }

    @Override
    public void onDestroy() {
        Log.d("emji", "onDestroy");
        super.onDestroy();

        try {

            if (!sStopRequest){
                sendBroadcast(new Intent("YouWillNeverKillMe"));
            }

           /*code for stpe down*/ //unregisterReceiver(mybroadcast);
            if(mWakeLock.isHeld()){
                mWakeLock.release();
            }

            mHandler.removeCallbacks(mRunnable);
            sIsServiceRunning = false;
            stopForeground(true);

            if(sForceStop) {
                Intent intent = new Intent(this, BackgroundService.class);
                stopService(intent);
            }

        } catch (Exception ex) {
            Log.d("emji","error " + ex.toString());
        }
    }

    private void initValue(){
        mContext = this;
        sIsServiceRunning = true;
        sStopRequest = false;
        mDate = new Date();
        mHandler = new Handler();
    }

    private void initPowerManager(){
        mPowerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        mWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
        mWakeLock.acquire();
    }

    private void initSetting(){
        Settings.loadSetting(mContext);
    }

    private void initPathFile(){

        String path = Settings.sApplicationPath + Settings.sApplicationPath;
        File file = new File(path);
        if (!file.exists()){
            new StatsFileManager(mContext).createNewFile();
        }
    }
}
