package th.ac.bu.mcop.services;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import th.ac.bu.mcop.R;
import th.ac.bu.mcop.activities.MainActivity;
import th.ac.bu.mcop.modules.StatsFileManager;
import th.ac.bu.mcop.utils.Constants;
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
    private final Handler mHandler = new Handler();
    private Runnable mRunnable;
    private boolean mIsForeground;
    private DateFormat mDateFormat;

    public static int sCounter = 0;

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

        boolean retry = true;
        int networkErrorCount = 0;
        int intenalCounter = 0;
        Date currentDate = new Date();

        mHandler.postDelayed(mRunnable = new Runnable() {
            @Override
            public void run() {

            

            }
        }, Settings.sInterval * 1000);


        startAsForeground();

        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d("emji", "onTaskRemoved");
        super.onTaskRemoved(rootIntent);

        try {

            if (!sStopRequest){
                sendBroadcast(new Intent("YouWillNeverKillMe"));
            }
            /*code for stpe down*/ //unregisterReceiver(mybroadcast);
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
        sCounter = 0;
        sIsServiceRunning = true;
        sStopRequest = false;
        mIsForeground = false;
        mDate = new Date();
        mDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void startAsForeground() {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        //TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        int icon = R.mipmap.ic_launcher;

        mBuilder.setContentTitle("Stats Collector")
                .setContentText("Started: " + mDateFormat.format(mDate))
                .setSubText("Session: " + sCounter)
                .setContentInfo("Interval: " + Settings.sInterval)
                .setSmallIcon(icon)
                .setColor(Color.parseColor("#78909C"))
                .setCategory(Notification.CATEGORY_SERVICE)
                .setPriority(Notification.PRIORITY_LOW)

                .setContentIntent(resultPendingIntent);

        startForeground(Constants.ONGOING_NOTIFICATION_ID, mBuilder.build());

        mIsForeground = true;
    }
}
