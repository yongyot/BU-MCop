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

import th.ac.bu.mcop.utils.Constants;
import th.ac.bu.mcop.utils.Settings;

/**
 * Created by jeeraphan on 11/16/16.
 */

public class HashFileUploader extends AsyncTask <String, Void, String>{

    private Context mContext;
    private int mServerCode;
    public static boolean sIsUploading;

    public HashFileUploader(Context context){
        mContext = context;
    }

    @Override
    protected String doInBackground(String... strings) {

        if (Settings.sNetworkType != Constants.NETWORK_TYPE_NO_NETWORK){
            sIsUploading = true;

            String path = Settings.sHashFilePath;

            mServerCode = uploadFile(path);

            if (mServerCode == 200){
                File file = new File(path);
                file.delete();
            } else if (mServerCode == 404){
                Log.d(Settings.TAG, "Can not connect to server. Hash file can not be uploaded");
            } else if (mServerCode == -1){
                Log.d(Settings.TAG, "Hash file does not exist.");
            } else if (mServerCode == 504){
                Log.d(Settings.TAG, "Gateway Timeout. Couldn't upload hash file");
            } else if (mServerCode == 0){
                Log.d(Settings.TAG, "Can not reach to server with this network.");
            } else {
                Log.d(Settings.TAG, "Unhandled server code. Couldn't upload hash file" + mServerCode);
            }
        } else {
            Log.d(Settings.TAG, "No internet access. Couldn't upload hash file");
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(mServerCode == 200) {
            File file = new File(Settings.sHashFilePath);
            file.delete();
        }
    }

    synchronized public int uploadFile(String sourceFileUrl){
        String upLoadServerUri =  "http://mobile-monitoring.bu.ac.th//hash.aspx";

        int serverResponseCode = 0;
        String fileName = sourceFileUrl;

        HttpURLConnection conn;
        DataOutputStream dos;
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUrl);

        if (!sourceFile.isFile()) {
            Log.d("stats-results", "Error uploading file. Details:  Source File not exist :");
            return -1;
        }

        try{

            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            URL url = new URL(upLoadServerUri);

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

            Log.d(Settings.TAG, "HTTP Response for hash file is : "+ serverResponseMessage + ": " + serverResponseCode);

            fileInputStream.close();
            dos.flush();
            dos.close();

        } catch (MalformedURLException ex){
            ex.printStackTrace();
            Log.d(Settings.TAG, "Error uploading hash file. Details: " + ex.getMessage(), ex);
        } catch (Exception e){
            Log.d(Settings.TAG, "Error hash uploading file. Details:  "+ e.getMessage());
        }

        return serverResponseCode;
    }
}
