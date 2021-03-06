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
import th.ac.bu.mcop.models.NetData;
import th.ac.bu.mcop.models.Stats;
import th.ac.bu.mcop.models.realm.AppRealm;
import th.ac.bu.mcop.models.realm.StatsRealm;
import th.ac.bu.mcop.utils.Constants;
import th.ac.bu.mcop.utils.Settings;

public class StatsExtractor {

    public static final String TOP_COMMAND = "top -d 0 -n 1";
    public static final String DATA_INTERFACE = "rmnet0";
    public static final String PROC_FILE_NAME = "/proc/net/xt_qtaguid/stats";
    public static final String FRONT_GROUND = "fg";
    public static final String BACK_GROUND = "bg";
    private static Context mContext;

    public static void saveStats(Context context){

        mContext = context;
        StatsRealm.save(getRunningApps());
    }

    private static String readProceFile(){

        String procFileName = PROC_FILE_NAME;
        StringBuffer fileData = new StringBuffer();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(procFileName));
            String line;                //ignore header
            while ((line = bufferedReader.readLine()) != null) {
                fileData.append(line + "\n\r");
            }

            bufferedReader.close();
        } catch (Exception e) {
            Log.d(Settings.TAG, "Error reading proc file in NetStatsCollector. Details: " + e.toString());
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
                if(datas[INDEX_OF_PCY].equalsIgnoreCase(FRONT_GROUND)){
                    stats.setState(Constants.STATE_FOREGROUND);
                } else if(datas[INDEX_OF_PCY].equalsIgnoreCase(BACK_GROUND)) {
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
            Process process = Runtime.getRuntime().exec(TOP_COMMAND);
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
        if (data.length() > i+4){
            return data.substring(i+4);
        }
        return data;
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
                if (dataBG.length > APP_UID && dataBG[APP_UID].equalsIgnoreCase(UID) && dataBG[TAG].equalsIgnoreCase("0x0")){

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

    public static void saveNetData(){

        ArrayList<AppRealm> appRealms = AppRealm.getAll();

        for (AppRealm app : appRealms){

            ArrayList<StatsRealm> statses = StatsRealm.getStatsWithPackageName(app.getPackageName());
            if (statses.size() > 0){

                NetData netData = new NetData();
                netData.setPackageName(statses.get(0).getPackageName());
                netData.setUid(statses.get(0).getUid());
                netData.setNetWorkState(getNetWorkState(statses)+"");
                netData.setNetWorkMode(getNetWorkMode(statses));
                netData.setApplicationState(getApplicationState(statses)+"");

                netData.setAvgOfSentDataInByte(getAvgOfSentDataInByte(statses)+"");
                netData.setSdOfSentDataInByte(getSDOfSentDataInByte(statses)+"");
                netData.setMinOfSentDataInByte(getMinOfSentDataInByte(statses)+"");
                netData.setMaxOfSentDataInByte(getMaxOfSentDataInByte(statses)+"");

                netData.setAvgOfReceivedDataInByte(getAvgOfReceivedDataInByte(statses)+"");
                netData.setSdOfReceivedDataInByte(getSDOfReceivedDataInByte(statses)+"");
                netData.setMinOfReceivedDataInByte(getMinOfReceivedDataInByte(statses)+"");
                netData.setMaxOfReceivedDataInByte(getMaxOfReceivedDataInByte(statses)+"");

                netData.setAvgOfSentDataInPercent(getAvgOfSentDataInPercent(statses)+"");
                netData.setSdOfSentDataInPercent(getSDOfSentDataInPercent(statses)+"");
                netData.setMinOfSentDataInPercent(getMinOfSentDataInPercent(statses)+"");
                netData.setMaxOfSentDataInPercent(getMaxOfSentDataInPercent(statses)+"");

                netData.setAvgOfReceivedDataInPercent(getAvgOfReceivedDataInPercent(statses)+"");
                netData.setSdOfReceivedDataInPercent(getSDOfReceivedDataInPercent(statses)+"");
                netData.setMinOfReceivedDataInPercent(getMinOfReceivedDataInPercent(statses)+"");
                netData.setMaxOfReceivedDataInPercent(getMaxOfReceivedDataInPercent(statses)+"");

                netData.setSentBetween(getSentBetween(statses));
                netData.setReceivedBetween(getReceivedBetween(statses));

                netData.setSentDataInBytePercentOfTotal(getSentDataInPercentOfTotal(appRealms, statses.get(0).getPackageName())+"");
                netData.setReceivedDataInBytePercentOfTotal(getReceivedDataInPercentOfTotal(appRealms, statses.get(0).getPackageName())+"");


                StatsFileManager.writeToFile(netData.getStringNetData(), true);
            }
        }

        StatsRealm.deleteAll();
    }

    private static String getNetWorkMode(ArrayList<StatsRealm> statses){

        if (statses.size() < 2){
            return NetData.NETWORK_MODE_CONTINUOUS;
        }

        String lastTimeString = statses.get(statses.size() - 1).getCreateDate();
        String beforeLastTime = statses.get(statses.size() - 2).getCreateDate();

        // format HH:mm
        String[] lastTimeArray = lastTimeString.split(":");
        String[] beforeLastTimeArray = beforeLastTime.split(":");

        int hourLastTimeInt = Integer.parseInt(lastTimeArray[0]);
        int hourBeforeLastTimeInt = Integer.parseInt(lastTimeArray[0]);

        int minuteLastTimeInt = Integer.parseInt(lastTimeArray[1]);
        int minuteBeforeLastTimeInt = Integer.parseInt(beforeLastTimeArray[1]);

        if (hourLastTimeInt == hourBeforeLastTimeInt){
            if ((minuteLastTimeInt - minuteBeforeLastTimeInt) <= 30){
                return NetData.NETWORK_MODE_EVENTUAL;
            }
        } else {
            if (((60 - minuteLastTimeInt) - minuteBeforeLastTimeInt) < 30){
                return NetData.NETWORK_MODE_EVENTUAL;
            }
        }

        return NetData.NETWORK_MODE_CONTINUOUS;
    }

    private static double getReceivedDataInPercentOfTotal(ArrayList<AppRealm> appRealms, String packageName){

        int totalReceivedDataAllApp = 0;
        int totalReceivedDataThisApp = 0;

        for (AppRealm app : appRealms){
            ArrayList<StatsRealm> statses = StatsRealm.getStatsWithPackageName(app.getPackageName());

            if (statses.size() > 0){

                for (StatsRealm stats : statses){
                    totalReceivedDataAllApp += stats.getSentDataInByte();

                    if (packageName.equals(stats.getPackageName())){
                        totalReceivedDataThisApp += stats.getSentDataInByte();
                    }
                }
            }
        }

        if (totalReceivedDataAllApp > 0){
            return (totalReceivedDataThisApp / totalReceivedDataAllApp) * 100;
        }

        return 0;
    }

    private static double getSentDataInPercentOfTotal(ArrayList<AppRealm> appRealms, String packageName){

        int totalSentDataAllApp = 0;
        int totalSentDataThisApp = 0;

        for (AppRealm app : appRealms){
            ArrayList<StatsRealm> statses = StatsRealm.getStatsWithPackageName(app.getPackageName());

            if (statses.size() > 0){

                for (StatsRealm stats : statses){
                    totalSentDataAllApp += stats.getSentDataInByte();

                    if (packageName.equals(stats.getPackageName())){
                        totalSentDataThisApp += stats.getSentDataInByte();
                    }
                }
            }
        }

        if (totalSentDataAllApp > 0){
            return (totalSentDataThisApp / totalSentDataAllApp) * 100;
        }

        return 0;
    }

    private static String getReceivedBetween(ArrayList<StatsRealm> statses){

        int countReceivedData = 0;

        for (StatsRealm stats : statses){
            if (stats.getSentDataInByte() > 0){
                countReceivedData++;
            }
        }

        if (countReceivedData > 1){ // assume per 60 s
            return NetData.BETWEEN_INNER;
        }

        return NetData.BETWEEN_OUTER;
    }

    private static String getSentBetween(ArrayList<StatsRealm> statses){

        int countSentData = 0;

        for (StatsRealm stats : statses){
            if (stats.getSentDataInByte() > 0){
                countSentData++;
            }
        }

        if (countSentData > 1){ // assume per 60 s
            return NetData.BETWEEN_INNER;
        }

        return NetData.BETWEEN_OUTER;
    }

    private static double getSDOfReceivedDataInPercent(ArrayList<StatsRealm> statses){

        double mean = getAvgOfReceivedDataInPercent(statses);

        ArrayList<Double> datas = new ArrayList<>();
        for (StatsRealm stats : statses){
            datas.add(stats.getReceivedDataInBytePercentOfTotal());
        }

        double temp = 0;
        for(double a : datas){
            temp += (a-mean)*(a-mean);
        }

        double variance = temp / datas.size();

        return Math.sqrt(variance);
    }

    private static double getSDOfSentDataInPercent(ArrayList<StatsRealm> statses){

        double mean = getAvgOfSentDataInPercent(statses);

        ArrayList<Double> datas = new ArrayList<>();
        for (StatsRealm stats : statses){
            datas.add(stats.getSentDataInBytePercentOfTotal());
        }

        double temp = 0;
        for(double a : datas){
            temp += (a-mean)*(a-mean);
        }

        double variance = temp / datas.size();

        return Math.sqrt(variance);
    }

    private static double getSDOfReceivedDataInByte(ArrayList<StatsRealm> statses){

        double mean = getAvgOfReceivedDataInByte(statses);

        ArrayList<Double> datas = new ArrayList<>();
        for (StatsRealm stats : statses){
            datas.add(stats.getReceivedDataInByte());
        }

        double temp = 0;
        for(double a : datas){
            temp += (a-mean)*(a-mean);
        }

        double variance = temp / datas.size();

        return Math.sqrt(variance);
    }

    private static double getSDOfSentDataInByte(ArrayList<StatsRealm> statses){

        double mean = getAvgOfSentDataInByte(statses);

        ArrayList<Double> datas = new ArrayList<>();
        for (StatsRealm stats : statses){
            datas.add(stats.getSentDataInByte());
        }

        double temp = 0;
        for(double a : datas){
            temp += (a-mean)*(a-mean);
        }

        double variance = temp / datas.size();

        return Math.sqrt(variance);
    }

    private static double getMaxOfReceivedDataInPercent(ArrayList<StatsRealm> statses){
        double max = statses.get(0).getReceivedDataInBytePercentOfTotal();
        for (StatsRealm stats : statses){

            if (max < stats.getReceivedDataInBytePercentOfTotal()){
                max = stats.getReceivedDataInBytePercentOfTotal();
            }
        }
        return max;
    }

    private static double getMinOfReceivedDataInPercent(ArrayList<StatsRealm> statses){
        double min = statses.get(0).getReceivedDataInBytePercentOfTotal();
        for (StatsRealm stats : statses){

            if (min > stats.getReceivedDataInBytePercentOfTotal()){
                min = stats.getReceivedDataInBytePercentOfTotal();
            }
        }
        return min;
    }

    private static double getAvgOfReceivedDataInPercent(ArrayList<StatsRealm> statses){
        double totalReceivedDataInPercent = 0;
        for (StatsRealm stats : statses){
            totalReceivedDataInPercent += stats.getReceivedDataInBytePercentOfTotal();
        }

        return totalReceivedDataInPercent / statses.size();
    }

    private static double getMaxOfSentDataInPercent(ArrayList<StatsRealm> statses){
        double max = statses.get(0).getSentDataInBytePercentOfTotal();
        for (StatsRealm stats : statses){

            if (max < stats.getSentDataInBytePercentOfTotal()){
                max = stats.getSentDataInBytePercentOfTotal();
            }
        }
        return max;
    }

    private static double getMinOfSentDataInPercent(ArrayList<StatsRealm> statses){
        double min = statses.get(0).getSentDataInBytePercentOfTotal();
        for (StatsRealm stats : statses){

            if (min > stats.getSentDataInBytePercentOfTotal()){
                min = stats.getSentDataInBytePercentOfTotal();
            }
        }
        return min;
    }

    private static double getAvgOfSentDataInPercent(ArrayList<StatsRealm> statses){
        double totalSentDataInPercent = 0;
        for (StatsRealm stats : statses){
            totalSentDataInPercent += stats.getSentDataInBytePercentOfTotal();
        }

        return totalSentDataInPercent / statses.size();
    }

    private static double getMaxOfReceivedDataInByte(ArrayList<StatsRealm> statses){
        double max = statses.get(0).getReceivedDataInByte();
        for (StatsRealm stats : statses){

            if (max < stats.getReceivedDataInByte()){
                max = stats.getReceivedDataInByte();
            }
        }
        return max;
    }

    private static double getMinOfReceivedDataInByte(ArrayList<StatsRealm> statses){
        double min = statses.get(0).getReceivedDataInByte();
        for (StatsRealm stats : statses){

            if (min > stats.getReceivedDataInByte()){
                min = stats.getReceivedDataInByte();
            }
        }
        return min;
    }

    private static double getAvgOfReceivedDataInByte(ArrayList<StatsRealm> statses){

        double totalReceivedDataInByte = 0;
        for (StatsRealm stats : statses){
            totalReceivedDataInByte += stats.getReceivedDataInByte();
        }

        return totalReceivedDataInByte / statses.size();
    }

    private static double getMaxOfSentDataInByte(ArrayList<StatsRealm> statses){
        double max = statses.get(0).getSentDataInByte();
        for (StatsRealm stats : statses){

            if (max < stats.getSentDataInByte()){
                max = stats.getSentDataInByte();
            }
        }
        return max;
    }

    private static double getMinOfSentDataInByte(ArrayList<StatsRealm> statses){
        double min = statses.get(0).getSentDataInByte();
        for (StatsRealm stats : statses){

            if (min > stats.getSentDataInByte()){
                min = stats.getSentDataInByte();
            }
        }
        return min;
    }

    private static double getAvgOfSentDataInByte(ArrayList<StatsRealm> statses){

        double totalSentDataInByte = 0;
        for (StatsRealm stats : statses){
            totalSentDataInByte += stats.getSentDataInByte();
        }

        return totalSentDataInByte / statses.size();
    }

    private static double getApplicationState(ArrayList<StatsRealm> statses){

        int countFg = 0;
        int countBg = 0;

        for (StatsRealm stats : statses){
            if (stats.getApplicationState().equals(Constants.STATE_FOREGROUND)){
                countFg++;
            } else if (stats.getApplicationState().equals(Constants.STATE_BACKGROUND)){
                countBg++;
            }
        }

        if (countFg > countBg){
            return Constants.STATE_FOREGROUND;
        }

        return Constants.STATE_BACKGROUND;
    }

    private static int getNetWorkState(ArrayList<StatsRealm> statses) {

        int countMobile = 0;
        int countWifi = 0;
        int countNone = 0;

        for (StatsRealm stats : statses){

            if (stats.getNetWorkState().equals(Constants.NETWORK_TYPE_MOBILE)){
                countMobile++;
            } else if (stats.getNetWorkState().equals(Constants.NETWORK_TYPE_WIFI)){
                countWifi++;
            } else if (stats.getNetWorkState().equals(Constants.NETWORK_TYPE_NO_NETWORK)){
                countNone++;
            }
        }

        if (countNone > 0){

            if (countMobile > countNone){
                return Constants.NETWORK_TYPE_MOBILE;
            } else if (countWifi > countNone){
                return Constants.NETWORK_TYPE_WIFI;
            }

            return Constants.NETWORK_TYPE_NO_NETWORK;

        } else {

            if (countMobile > countWifi){
                return Constants.NETWORK_TYPE_MOBILE;
            }
            return Constants.NETWORK_TYPE_WIFI;
        }
    }
}
