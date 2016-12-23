package th.ac.bu.mcop.modules;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import th.ac.bu.mcop.models.AppsInfo;
import th.ac.bu.mcop.utils.Settings;

/**
 * Created by jeeraphan on 11/15/16.
 */

public class HashGenManager {

    public interface OnHashGenListener {
        void onHashGenFinished();
    }

    public volatile static boolean sIsGenerating;
    private OnHashGenListener mListener;

    public void setOnHashGenListener(OnHashGenListener listener){
        mListener = listener;
    }

    public void getAllAppInfo(Context context) {
        sIsGenerating = true;
        String data = "";

        SimpleDateFormat formatter = new SimpleDateFormat("d MMM yyyy HH:mm:ss:SSS", Locale.ENGLISH);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));

        new ArrayList<>();{
            final List<ApplicationInfo> packs = context.getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);

            for (ApplicationInfo appInfo : packs) {
                if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    AppsInfo app = getPackageInfo(appInfo.packageName, context);

                    Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
                    calendar.setTimeInMillis(app.getLastUpdate());
                    String date=  formatter.format(calendar.getTime());
                    data = data + app.getPackageName() + "*" + date + "*" + app.getHash() + "*"+ app.getVersionName() + "*" + app.getVersionCode() + "\n";
                }
            }
        }
        writeToFile(data,false);
        sIsGenerating = false;
    }


    public AppsInfo getPackageInfo(String packageName,Context context) {
        AppsInfo appInfo = new AppsInfo();

        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
            long millis = packageInfo.lastUpdateTime;
            appInfo.setHash(getHash(packageInfo.applicationInfo.sourceDir));
            appInfo.setLastUpdate(millis);
            appInfo.setPackageName(packageName);
            appInfo.setVersionName(packageInfo.versionName);
            appInfo.setVersionCode(packageInfo.versionCode + "");
            return appInfo;

        } catch (Exception ex) {
            Log.d(Settings.TAG,"Error occurred in getPackageInfo method. Details: "+ ex.toString());
        }
        return null;
    }


    private String getHash(String file) {
        try {
            FileInputStream inputStream = new FileInputStream(file);
            MessageDigest digest = MessageDigest.getInstance("MD5");

            byte[] bytesBuffer = new byte[1024];
            int bytesRead = -1;

            while ((bytesRead = inputStream.read(bytesBuffer)) != -1) {
                digest.update(bytesBuffer, 0, bytesRead);
            }

            byte[] hashedBytes = digest.digest();

            return convertByteArrayToHexString(hashedBytes);
        } catch (Exception ex) {
            Log.d(Settings.TAG,"Error while generating hashcode. Details: " + ex.toString());
        }
        return null;
    }

    private  String convertByteArrayToHexString(byte[] arrayBytes) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < arrayBytes.length; i++) {
            stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16)
                    .substring(1));
        }
        return stringBuffer.toString();
    }

    private  void writeToFile(String data, boolean append) {

        data = Settings.sMacAddress + "\n" + data;
        File statsDir;
        statsDir = new File(Settings.sApplicationPath);

        if(!statsDir.exists()){
            statsDir.mkdirs();
        }

        try {
            File file = new File(Settings.sHashFilePath);
            FileOutputStream fos = new FileOutputStream(file, append);
            fos.write(data.getBytes());
            fos.close();
        }  catch (Exception e) {
            Log.d(Settings.TAG, "Unable to write hash info in file. Details:\n" + e.toString());
        } finally {
            if (mListener != null){
                mListener.onHashGenFinished();
            }
        }
    }
}
