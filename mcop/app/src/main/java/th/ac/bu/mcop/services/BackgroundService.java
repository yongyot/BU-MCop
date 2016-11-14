package th.ac.bu.mcop.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;

import java.util.Date;

import th.ac.bu.mcop.utils.Settings;

/**
 * Created by jeeraphan on 11/14/16.
 */

public class BackgroundService extends Service {

    private static boolean sIsServiceRunning;
    private static volatile boolean sStopRequest;

    private Date mDate;
    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;
    private Context mContext;

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
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initValue(){
        mContext = this;
        sIsServiceRunning = true;
        sStopRequest = false;
        mDate = new Date();
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

    }
}
