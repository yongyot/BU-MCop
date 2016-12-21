package th.ac.bu.mcop.services;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
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
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import th.ac.bu.mcop.R;
import th.ac.bu.mcop.activities.MainActivity;
import th.ac.bu.mcop.modules.HashFileUploader;
import th.ac.bu.mcop.modules.HashGen;
import th.ac.bu.mcop.modules.NetDataExtractor;
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
    private Date mCurrentDate;

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
        Log.d(Settings.TAG, "BackgroundService onStartCommand");

        startAsForeground();

        boolean retry = true;
        int networkErrorCount = 0;
        final int intenalCounter = 0;
        mCurrentDate = new Date();

        mHandler.postDelayed(mRunnable = new Runnable() {
            @Override
            public void run() {
                sendBroadcast();
                checkUpdateHashGen();

                NetDataExtractor netDataExtractor = new NetDataExtractor(mContext);
                netDataExtractor.getNetData();
//                ArrayList<Stats> listDiffStats;
//                ArrayList<Stats> listOldStats;
//                ArrayList<Stats> listNewStats;
//                if (mIsForeground){
//                    //below lollipop we need to remove the notification by stop foreground and restart the notification
//                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
//                        stopForeground(true);
//                        updateNotification();
//                    } else {
//                        stopForeground(false);
//                    }
//
//                    mIsForeground = false;
//
//                    //this code generates the hash code every midnight.s
//                    if (compare(mCurrentDate, new Date()) != 0){
//
//                        //creates hashcode file for every app
//                        HashGen hashGen = new HashGen();
//                        hashGen.getAllAppInfo(mContext);
//                    }
//
//                    //scheduler for uploading hashcode
//                    File file = new File(Settings.sHashFilePath);
//                    boolean isExist = file.exists();
//
//                    //if hash file exist upload it to server..
//                    if (isExist && !HashGen.sIsGenerating && !HashFileUploader.sIsUploading){
//                        HashFileUploader hashFileUploader = new HashFileUploader(mContext);
//                        hashFileUploader.execute();
//                    }
//
//                    //make sure file does not exist.
//                    isExist = file.exists();
//
//                    if (isExist && StatsFileManager.getFileSize() >= Settings.sUploadSize && HashGen.sIsGenerating){
//                        FileUploader fileUploader = new FileUploader(mContext);
//                        fileUploader.execute();
//                    }
//
//                    if (!FileUploader.sIsUploading){
//
//                        String data = "";
//
//                        // get the stats for the first time.
//                        if (intenalCounter == 0){
//                            listOldStats = Stats.getStats(mContext);
//
//                        } else if (intenalCounter > Settings.sNetInterval){
//
//                            listNewStats = Stats.getStats(mContext);
//                            listDiffStats = Stats.netDifference(listOldStats, listNewStats);
//
//                            listOldStats = listNewStats;
//
//                            startAsForeground();
//
//                            boolean error = false;
//
//                            String networkType = Settings.sNetworkType + "";
//                            for (Stats stats : listDiffStats) {
//                                if (stats.getNet().sError) {
//                                    error = true;
//                                }
//
//                                data = data + stats.getStringData() + "|" + networkType +"\n";
//                            }
//                        }
//                    }
//
//                    startAsForeground();
//                    sCounter++;
//                    mHandler.postDelayed(this, Settings.sInterval * 1000);
//                }

                startAsForeground();
                sCounter++;
                mHandler.postDelayed(this, Settings.sInterval * 1000);
            }
        }, Settings.sInterval * 1000);

        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(Settings.TAG, "BackgroundService onTaskRemoved");
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
            Log.d(Settings.TAG, "error " + ex.toString());
        }
    }

    @Override
    public void onDestroy() {
        Log.d(Settings.TAG, "BackgroundService onDestroy");
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
            Log.d(Settings.TAG,"error " + ex.toString());
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

        mBuilder.setContentTitle("mCOP Stats Collector")
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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void updateNotification() {
        NotificationManager notifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
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
        notifyManager.notify(Constants.ONGOING_NOTIFICATION_ID, mBuilder.build());
    }

    private void sendBroadcast(){
        Intent intentUpdateUI = new Intent(Constants.INTENT_FILTER_UPDATE_UI);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentUpdateUI);

        Intent intentUpdateInternet = new Intent(Constants.INTENT_FILTER_INTERNET);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentUpdateInternet);
    }

    private void checkUpdateNetData(){

    }

    private void checkUpdateHashGen(){

        if (compare(mCurrentDate, new Date()) != 0){

            HashGen hashGen = new HashGen();
            hashGen.getAllAppInfo(mContext);

            File file = new File(Settings.sHashFilePath);
            boolean isExist = file.exists();
            if (isExist && !HashGen.sIsGenerating && !HashFileUploader.sIsUploading){
                HashFileUploader hashFileUploader = new HashFileUploader(mContext);
                hashFileUploader.execute();
            }
        }
    }

    public int compare(Date d1, Date d2) {

        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(d1);

        Calendar calendar2 = Calendar.getInstance();
        calendar1.setTime(d2);

        if (calendar1.YEAR != calendar2.YEAR)
            return calendar1.YEAR - calendar2.YEAR;

        if (calendar1.MONTH != calendar2.MONTH)
            return calendar1.MONTH - calendar2.MONTH;

        return calendar1.DATE - calendar2.DATE;

    }
}
