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
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import th.ac.bu.mcop.R;
import th.ac.bu.mcop.activities.HomeActivity;
import th.ac.bu.mcop.models.response.ReportHeaderModel;
import th.ac.bu.mcop.models.response.ReportModel;
import th.ac.bu.mcop.models.response.ResponseModel;
import th.ac.bu.mcop.models.response.ResponseUpload;
import th.ac.bu.mcop.modules.HashFileUploader;
import th.ac.bu.mcop.modules.HashGenManager;
import th.ac.bu.mcop.modules.StatsExtractor;
import th.ac.bu.mcop.modules.StatsFileManager;
import th.ac.bu.mcop.modules.api.APIService;
import th.ac.bu.mcop.modules.api.ApiManager;
import th.ac.bu.mcop.utils.Constants;
import th.ac.bu.mcop.utils.Settings;
import th.ac.bu.mcop.utils.SharePrefs;

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
    private DateFormat mDateFormat;

    public static int sCounter = 5;

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

        mHandler.postDelayed(mRunnable = new Runnable() {
            @Override
            public void run() {

                sCounter += 5;

                if (sCounter > 60){
                    sCounter = 5;
                    StatsExtractor.saveNetData();
                }

                sendBroadcast();
                checkUpdateHashGen();
                startAsForeground();
                StatsExtractor.saveStats(mContext);
                Log.d(Settings.TAG, "File size: " + StatsFileManager.getFileSize());
                if (StatsFileManager.getFileSize() > Settings.sUploadSize){

                    //uploadNetDataFile();
                    uploadNetDataByte();
                }

                mHandler.postDelayed(this, Settings.sInterval * 1000);
            }
        }, Settings.sInterval * 1000);

        return START_STICKY;
    }

    private void uploadNetDataByte() {

        String path = Settings.sApplicationPath + Settings.sOutputFileName;
        String osVersion = "5.0";//Build.VERSION.BASE_OS + "";
        String deviceModel = Build.MODEL;
        final File file = new File(path);

        try {
            byte[] arrayByte = fullyReadFileToBytes(file);

            JSONObject object = new JSONObject();
            object.put("device_mac", Settings.getMacAddress(mContext));
            object.put("os_version", osVersion);
            object.put("device_model", deviceModel);
            object.put("file", arrayByte);

            JSONObject jsonObject = new JSONObject(new Gson().toJson(object));
            String bodyPostJson = jsonObject.getString("nameValuePairs");
            Log.d(Settings.TAG, "bodyPost: " + bodyPostJson);
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),bodyPostJson);
            Log.d(Settings.TAG, "requestBody: " + requestBody);
            ApiManager.getInstance().uploadNetDataByte(new Callback<ResponseUpload>() {
                @Override
                public void onResponse(Call<ResponseUpload> call, Response<ResponseUpload> response) {
                    Log.d(Settings.TAG, "upload net date byte success");
                    Log.d(Settings.TAG, response + "");
                    if (response != null){
                        Log.d(Settings.TAG, "body: " + response.body());
                        Log.d(Settings.TAG, "isCompleted: " + response.body().getIsCompleted());
                        Log.d(Settings.TAG, "Message: " + response.body().getMessage());
                    }

                    file.delete();
                    initPathFile();
                }

                @Override
                public void onFailure(Call<ResponseUpload> call, Throwable t) {
                    Log.d(Settings.TAG, "upload byte fail");
                    Log.d(Settings.TAG, t.getMessage());
                }
            }, requestBody);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException ex){
            ex.printStackTrace();
        }
    }

    private byte[] fullyReadFileToBytes(File f) throws IOException {
        int size = (int) f.length();
        byte bytes[] = new byte[size];
        byte tmpBuff[] = new byte[size];

        FileInputStream fis= new FileInputStream(f);;
        try {

            int read = fis.read(bytes, 0, size);
            if (read < size) {
                int remain = size - read;
                while (remain > 0) {
                    read = fis.read(tmpBuff, 0, remain);
                    System.arraycopy(tmpBuff, 0, bytes, size - remain, read);
                    remain -= read;
                }
            }
        }  catch (IOException e){
            throw e;
        } finally {
            fis.close();
        }

        return bytes;
    }

    private void uploadHashCodeByte() {

        String osVersion = "5.0";//Build.VERSION.BASE_OS + "";
        String deviceModel = Build.MODEL;
        final File file = new File(Settings.sHashFilePath);

        try {
            byte[] arrayByte = fullyReadFileToBytes(file);

            JSONObject object = new JSONObject();
            object.put("device_mac", Settings.getMacAddress(mContext));
            object.put("os_version", osVersion);
            object.put("device_model", deviceModel);
            object.put("file", arrayByte);

            JSONObject jsonObject = new JSONObject(new Gson().toJson(object));
            String bodyPostJson = jsonObject.getString("nameValuePairs");
            Log.d(Settings.TAG, "bodyPost: " + bodyPostJson);
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), bodyPostJson);
            Log.d(Settings.TAG, "requestBody: " + requestBody);
            ApiManager.getInstance().uploadHashCodeByte(new Callback<ResponseUpload>() {
                @Override
                public void onResponse(Call<ResponseUpload> call, Response<ResponseUpload> response) {
                    Log.d(Settings.TAG, "upload hash code byte success");
                    Log.d(Settings.TAG, response + "");
                    if (response != null) {
                        Log.d(Settings.TAG, "body: " + response.body());
                        Log.d(Settings.TAG, "isCompleted: " + response.body().getIsCompleted());
                        Log.d(Settings.TAG, "Message: " + response.body().getMessage());
                    }

                    file.delete();
                    initPathFile();
                }

                @Override
                public void onFailure(Call<ResponseUpload> call, Throwable t) {
                    Log.d(Settings.TAG, "upload byte fail");
                    Log.d(Settings.TAG, t.getMessage());
                }
            }, requestBody);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(Settings.TAG, "BackgroundService onTaskRemoved");
        super.onTaskRemoved(rootIntent);

        try {

            if (!sStopRequest){
                sendBroadcast(new Intent(getString(R.string.key_nerver_kill)));
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
                sendBroadcast(new Intent(getString(R.string.key_nerver_kill)));
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

        Intent resultIntent = new Intent(this, HomeActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        //TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(HomeActivity.class);

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
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void updateNotification() {
        NotificationManager notifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        Intent resultIntent = new Intent(this, HomeActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(HomeActivity.class);

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

    private void checkUpdateHashGen(){

        boolean isCurrentDate = false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String cacheDateString = SharePrefs.getPreferenceString(mContext, Constants.KEY_CURRENT_DATE, "");
        String currentDateString = sdf.format(date);

        // check current date
        if (cacheDateString.equals(currentDateString)){
            isCurrentDate = true;
        } else {
            SharePrefs.setPreference(mContext, Constants.KEY_CURRENT_DATE, currentDateString);
        }

        if (!isCurrentDate){

            HashGenManager hashGen = new HashGenManager();
            hashGen.getAllAppInfo(mContext);

            uploadHashCodeByte();

//            File file = new File(Settings.sHashFilePath);
//            boolean isExist = file.exists();
//            if (isExist && !HashGenManager.sIsGenerating && !HashFileUploader.sIsUploading){
//                HashFileUploader hashFileUploader = new HashFileUploader(mContext);
//                hashFileUploader.execute();
//            }
        }
    }
}
