package th.ac.bu.mcop.modules;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import th.ac.bu.mcop.utils.Constants;
import th.ac.bu.mcop.utils.Settings;
import th.ac.bu.mcop.utils.SharePrefs;

/**
 * Created by jeeraphan on 11/16/16.
 */

public class FileUploader extends AsyncTask<String, Void, String> {

    private Context mContext;
    public static volatile boolean sIsUploading;

    public FileUploader(Context context){
        mContext = context;
    }

    @Override
    protected String doInBackground(String... strings) {

        if (Settings.sNetworkType != Constants.NETWORK_TYPE_NO_NETWORK){

            sIsUploading = true;

            StatsFileManager.compressFile(mContext);
            String path = Settings.sApplicationPath + Settings.sOutputFileName + ".zip";
            int serverCode = uploadFile(path);

            if (serverCode == 200){
                new StatsFileManager(mContext).createNewFile();
            } else if (serverCode == 404){
                Log.d(Settings.TAG, "Can not connect to server.");
            } else if (serverCode == -1){
                Log.d(Settings.TAG, "Source file does not exist.11");
            } else if (serverCode == 504){
                Log.d(Settings.TAG, "Gateway Timeout.");
            } else {
                Log.d(Settings.TAG, "Unhandled server code. " + serverCode);
            }

            File file = new File(path);
            file.delete();
        }

        sIsUploading = false;

        return null;
    }

    synchronized public int uploadFile(String sourceFileUrl){

        int serverResponseCode = 0;

        String upLoadServerUrl = "http://mobile-monitoring.bu.ac.th//default.aspx?id=" + Settings.sOutputFileName;
        String fileName = sourceFileUrl;

        HttpURLConnection conn;
        DataOutputStream dos;
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUrl);

        //file not found
        if (!sourceFile.isFile()){
            return  -1;
        }

        try {

            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            URL url = new URL(upLoadServerUrl);

            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs
            conn.setUseCaches(false); // Don't use a Cached Copy
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("uploaded_file", fileName);

            dos = new DataOutputStream(conn.getOutputStream());

            bytesAvailable = fileInputStream.available();

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {

                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            serverResponseCode = conn.getResponseCode();
            String serverResponseMessage = conn.getResponseMessage();
            Log.d(Settings.TAG, "HTTP Response is : "+ serverResponseMessage + ": " + serverResponseCode);

            //close the streams //
            fileInputStream.close();
            dos.flush();
            dos.close();

            // cache last time
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String currentDateandTime = sdf.format(new Date());
            SharePrefs.setPreference(mContext, "las_time_upload", currentDateandTime);

        } catch (MalformedURLException ex){
            ex.printStackTrace();
            Log.d(Settings.TAG, "Error uploading file. Details: " + ex.getMessage(), ex);
        } catch (Exception e){
            Log.d(Settings.TAG, "Error uploading file. Details:  "+ e.getMessage());
        }

        return serverResponseCode;
    }
}