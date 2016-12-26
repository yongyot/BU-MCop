package th.ac.bu.mcop.modules;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import th.ac.bu.mcop.utils.Settings;
import th.ac.bu.mcop.widgets.NotificationView;

/**
 * Created by jeeraphan on 11/14/16.
 */

public class StatsFileManager {

    private Context mContext;

    public StatsFileManager(Context context){
        mContext = context;
    }

    public synchronized void writeToFile(String data, boolean append) {

        File statsDir;
        statsDir = new File(Settings.sApplicationPath);

        if (!statsDir.exists())
            statsDir.mkdirs();

        try {
            File file = new File(statsDir, Settings.sOutputFileName);
            FileOutputStream fos = new FileOutputStream(file, append);
            fos.write(data.getBytes());
            fos.close();
        } catch (Exception e) {
        }
    }

    public static boolean compressFile(Context context) {
        boolean result = false;
        byte[] buffer = new byte[1024];
        String outputFile = Settings.sOutputFileName;
        try {

            File statsDir;
            statsDir = new File(Settings.sApplicationPath);

            FileOutputStream fos = new FileOutputStream(statsDir + "/" + outputFile + ".zip");

            ZipOutputStream zos = new ZipOutputStream(fos);
            ZipEntry ze = new ZipEntry(outputFile);
            zos.putNextEntry(ze);
            FileInputStream in = new FileInputStream(statsDir + "/" + outputFile);

            int len;
            while ((len = in.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }

            in.close();
            zos.closeEntry();
            zos.close();
            fos.close();

        } catch (IOException ex) {
            Log.d(Settings.TAG, "Error occurred while compressing file. Details: " + ex.toString());
            ex.printStackTrace();
            NotificationView.show(context, "Data compression error.");
        }
        return result;
    }

    public static long getFileSize() {
        long size;
        try {
            File statsDir;
            statsDir = new File(Settings.sApplicationPath);

            if (!statsDir.exists())
                statsDir.mkdirs();

            File file = new File(statsDir, Settings.sOutputFileName);

            size = file.length() / 1024;

            return size;
        }
        catch (Exception ex) {
            return -1;
        }
    }

    public void createNewFile() {
        Log.d(Settings.TAG, "createNewFile");
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy HH:mm:ss:SSS", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        String createtime = sdf.format(cal.getTime());

        String metaInfo = "File Name: " + Settings.sOutputFileName + "\r\n" +
                "Extraction Started: " + createtime + "\r\n";
        String formatStr = "%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s";
        String title = String.format(formatStr
                , "LOG_TIME"
                , "PACKAGE_NAME"
                , "UID"
                , "SENT%"
                , "RECEIVED%"
                , "NETWORK_STATE"
                , "MODE"
                , "APP_STATES"
                , "AVG_SENT"
                , "SD_SENT"
                , "MIN_SENT"
                , "MAX_SENT"
                , "AVG_RECEIVED"
                , "SD_RECEIVED"
                , "MIN_RECEIVED"
                , "MAX_RECEIVED"
                , "AVG_SENT%"
                , "SD_SENT%"
                , "MIN_SENT%"
                , "MAX_SENT%"
                , "AVG_RECEIVED%"
                , "SD_RECEIVED%"
                , "MIN_RECEIVED%"
                , "MAX_RECEIVED%"
                , "SENT_BETWEEN"
                , "RECEIVED_BETWEEN"
                );

        String line = "";
        for (int i = 0; i <= title.length() + 48; i++)
            line = line + "-";

        String head = metaInfo + line + "\r\n" + title + "\r\n" + line + "\r\n";
        Log.d(Settings.TAG, head);
        writeToFile(head, false);
    }
}
