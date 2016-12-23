package th.ac.bu.mcop.modules;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import th.ac.bu.mcop.models.Net;
import th.ac.bu.mcop.models.Stats;
import th.ac.bu.mcop.models.realm.AppRealm;
import th.ac.bu.mcop.models.realm.StatsRealm;
import th.ac.bu.mcop.utils.Constants;
import th.ac.bu.mcop.utils.Settings;

/**
 * Created by jeeraphan on 11/22/16.
 */

public class StatsExtractor {

    public static final String DATA_INTERFACE = "rmnet0";
    private static Context mContext;

    public static void saveNetData(){

        ArrayList<AppRealm> appRealms = AppRealm.getAll();
        for (AppRealm app : appRealms){

            ArrayList<StatsRealm> statses = StatsRealm.getStatsWithPackageName(app.getPackageName());
            for (StatsRealm stats : statses){
                Log.d(Settings.TAG, stats.getPackageName());
            }
            Log.d(Settings.TAG, "====================================");
        }

        StatsRealm.deleteAll();
    }

    public static void saveStats(Context context){

        mContext = context;
        StatsRealm.save(getRunningApps());
    }

    private static String readProceFile(){

        String procFileName="/proc/net/xt_qtaguid/stats";
        StringBuffer fileData = new StringBuffer();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(procFileName));
            String line;                //ignore header
            while ((line = bufferedReader.readLine()) != null) {
                fileData.append(line + "\n");
            }

            bufferedReader.close();
        } catch (Exception e) {
            Log.d(Settings.TAG, "Error reading proc file in NetStatsCollector. Details: " + e.toString());
            //Notify.showNotification(context, "Couldn't read network file");
            try {
                if(bufferedReader!=null)
                    bufferedReader.close();

            } catch (Exception ex) {
                Log.e(Settings.TAG,"Can not close buffer reader for proc file.");
            }
        }

        return fileData.toString();
    }

    private static ArrayList<Stats> getRunningApps(){

        final int INDEX_OF_PCY      = 7;
        final int INDEX_OF_NAME     = 9;
        final int TOP_LENGTH        = 10;

        ArrayList<Stats> listAppRunning = new ArrayList<>();

        String statsString = getTopCommandData();

        String lines[] = statsString.trim().split("\n");

        for (String line : lines) {

            String datas[] = line.trim().split("\\s+");

            if(datas.length < TOP_LENGTH) {
                continue;
            }

            int uid = isSystemPackage(datas[INDEX_OF_NAME]);
            if(uid > 0) {

                Stats stats = new Stats();
                stats.setPackageName(datas[INDEX_OF_NAME]);
                stats.setUid(uid + "");
                if(datas[INDEX_OF_PCY].equalsIgnoreCase("fg")){
                    stats.setState(Constants.STATE_FOREGROUND);
                } else if(datas[INDEX_OF_PCY].equalsIgnoreCase("bg")) {
                    stats.setState(Constants.STATE_BACKGROUND);
                }

                Net net = getNetWith(stats, readProceFile());
                stats.setNet(net);

                listAppRunning.add(stats);
            }
        }

        return listAppRunning;
    }

    private static Net getNetWith(Stats stats, String unFilteredStats){

        if (stats.isMainProcess()){
            return getNetStats(stats.getUid(), unFilteredStats);
        }

        return new Net();
    }

    public static String getTopCommandData(){

        StringBuffer topData = new StringBuffer();
        try {
            Process process = Runtime.getRuntime().exec("top -d 0 -n 1");
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = in.readLine()) != null) {
                topData.append(line+"\n");
            }
            in.close();

        } catch (Exception e) {
            Log.d(Settings.TAG, "Top command error. Top command couldn't execute successfuly. Details:\n"+e.toString());
            e.printStackTrace();
        }

        String data = topData.toString();
        int i = data.indexOf("Name");
        return data.substring(i+4);
    }

    private static int isSystemPackage(String packageName){

        if(packageName.contains(":")){

            int pos = packageName.indexOf(':');
            packageName = packageName.substring(0, pos);
        }

        final PackageManager pm = mContext.getPackageManager();
        PackageInfo pkgInfo;

        try {
            pkgInfo = pm.getPackageInfo(packageName, PackageManager.GET_META_DATA);
            if(( pkgInfo.applicationInfo.flags &  ApplicationInfo.FLAG_SYSTEM) == 0){
                return pkgInfo.applicationInfo.uid;
            }
        } catch (PackageManager.NameNotFoundException ex) {
        } catch (Exception e) {
            Log.d(Settings.TAG,"Unknown error occurred while checking package type in CPUStatsExtractor class.. Details: "+e.toString());
        }

        return -1;
    }

    private static Net getNetStats(String UID,String unFilteredStats) {

        final int TAG       = 2;
        final int RX_BYTES  = 5;
        final int TX_BYTES  = 7;
        final int INTERFACE = 1;
        final int APP_UID   = 3;

        boolean wifiFound = false;
        boolean dataFound = false;

        Net net = new Net();

        String lines[] = unFilteredStats.split("\n");

        try {
            for(int i = 0; i < lines.length; i++) {

                String dataBG[] = lines[i].trim().split("\\s+");

                if (dataBG[APP_UID].equalsIgnoreCase(UID) && dataBG[TAG].equalsIgnoreCase("0x0")){

                    String dataFG[] = lines[i + 1].trim().split("\\s+");
                    i++;
                    if (dataBG[INTERFACE].equalsIgnoreCase(Settings.sWifiInterfaceName)){

                        net.setBgUpWifi(dataBG[TX_BYTES]);
                        net.setBgDownWifi(dataBG[RX_BYTES]);
                        net.setFgUpWifi(dataFG[TX_BYTES]);
                        net.setFgDownWifi(dataFG[RX_BYTES]);
                        wifiFound = true;

                    } else if (dataBG[INTERFACE].equalsIgnoreCase(DATA_INTERFACE)){

                        net.setBgUpData(dataBG[TX_BYTES]);
                        net.setBgDownData(dataBG[RX_BYTES]);
                        net.setFgUpData(dataFG[TX_BYTES]);
                        net.setFgDownData(dataFG[RX_BYTES]);
                        dataFound = true;
                    }
                }
                if (wifiFound && dataFound) {
                    break;
                }
            }
        }
        catch (Exception e) {
            net.sError = true;
            Log.d(Settings.TAG, "Might be malformed proc file from net stats extractor. Details: " + e.toString());
        }

        return net;
    }
}
