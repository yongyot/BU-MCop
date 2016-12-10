package th.ac.bu.mcop.models;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;

import th.ac.bu.mcop.modules.NetStatsExtractor;
import th.ac.bu.mcop.utils.Constants;
import th.ac.bu.mcop.utils.Settings;
import th.ac.bu.mcop.widgets.NotificationView;

/**
 * Created by jeeraphan on 11/15/16.
 */

public class Stats {

    private boolean isInteracting;
    private int state;
    private String uid;
    private String packageName;
    private Net net;

    public boolean isInteracting() {
        return isInteracting;
    }

    public void setInteracting(boolean interacting) {
        isInteracting = interacting;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Net getNet() {
        return net;
    }

    public void setNet(Net net) {
        this.net = net;
    }

    public Stats(){
        isInteracting = false;
        uid = new String();
        packageName = new String();

        net = new Net();
    }

    public Stats(Stats stats){
        net = new Net();

        this.isInteracting = stats.isInteracting;
        this.uid = stats.uid;
        this.packageName = stats.packageName;

        this.net.copyNet(stats.net);
    }

    public static ArrayList<Stats> getStats(Context context){

        //this method generates list of running apps and also feeds the CPU stats for each app
        //List<Stats> appStats = CPUStatsExtractor.getRunningApps(context);

        //This method feeds the network info in the model
        //appStats = new NetStatsExtractor(context).collectNetStats(appStats);

        ArrayList<Stats> listStats = getRunningApps(context);
        listStats = new NetStatsExtractor(context).collectNetStats(listStats);


        return listStats;
    }

    public static ArrayList<Stats> netDifference(ArrayList<Stats> listOldStats, ArrayList<Stats> listNewStats) {

        ArrayList<Stats> listDiff = copyListStats(listNewStats);

        for (int i = 0;i < listNewStats.size(); i++) {
            if (!listNewStats.get(i).isMainProcess()) {
                continue;
            }

            int index = listNewStats.get(i).getIndexByUID(listOldStats, listNewStats.get(i).getUid());    //returns the index of uid of old list.
            if(index < 0) {
                //this app is not in old list. It means we just found it's data. So its net stats to 0.
                //set it to empty object.
                listDiff.get(i).setNet(new Net());

            } else {

                int oBG_UP_DATA, oBG_DOWN_DATA, oFG_UP_DATA, oFG_DOWN_DATA, oBG_UP_WiFi, oBG_DOWN_WiFi, oFG_UP_WiFi, oFG_DOWN_WiFi;
                int nBG_UP_DATA, nBG_DOWN_DATA, nFG_UP_DATA, nFG_DOWN_DATA, nBG_UP_WiFi, nBG_DOWN_WiFi, nFG_UP_WiFi, nFG_DOWN_WiFi;

                oBG_UP_DATA = Integer.parseInt(listOldStats.get(index).getNet().getBgUpData());
                oBG_DOWN_DATA = Integer.parseInt(listOldStats.get(index).getNet().getBgDownData());
                oFG_UP_DATA = Integer.parseInt(listOldStats.get(index).getNet().getFgUpData());
                oFG_DOWN_DATA = Integer.parseInt(listOldStats.get(index).getNet().getFgDownData());
                oBG_UP_WiFi = Integer.parseInt(listOldStats.get(index).getNet().getBgUpWifi());
                oBG_DOWN_WiFi = Integer.parseInt(listOldStats.get(index).getNet().getBgDownWifi());
                oFG_UP_WiFi = Integer.parseInt(listOldStats.get(index).getNet().getFgUpWifi());
                oFG_DOWN_WiFi = Integer.parseInt(listOldStats.get(index).getNet().getFgDownWifi());

                nBG_UP_DATA = Integer.parseInt(listNewStats.get(i).getNet().getBgUpData());
                nBG_DOWN_DATA = Integer.parseInt(listNewStats.get(i).getNet().getBgDownData());
                nFG_UP_DATA = Integer.parseInt(listNewStats.get(i).getNet().getFgUpData());
                nFG_DOWN_DATA = Integer.parseInt(listNewStats.get(i).getNet().getFgDownData());
                nBG_UP_WiFi = Integer.parseInt(listNewStats.get(i).getNet().getBgUpWifi());
                nBG_DOWN_WiFi = Integer.parseInt(listNewStats.get(i).getNet().getBgDownWifi());
                nFG_UP_WiFi = Integer.parseInt(listNewStats.get(i).getNet().getFgUpWifi());
                nFG_DOWN_WiFi = Integer.parseInt(listNewStats.get(i).getNet().getFgDownWifi());

                listDiff.get(i).getNet().setBgUpData(nBG_UP_DATA - oBG_UP_DATA + "");
                listDiff.get(i).getNet().setBgDownData(nBG_DOWN_DATA - oBG_DOWN_DATA + "");
                listDiff.get(i).getNet().setFgUpData(nFG_UP_DATA - oFG_UP_DATA + "");
                listDiff.get(i).getNet().setFgDownData(nFG_DOWN_DATA - oFG_DOWN_DATA + "");
                listDiff.get(i).getNet().setBgUpWifi(nBG_UP_WiFi - oBG_UP_WiFi + "");
                listDiff.get(i).getNet().setBgDownWifi(nBG_DOWN_WiFi - oBG_DOWN_WiFi + "");
                listDiff.get(i).getNet().setFgUpWifi(nFG_UP_WiFi - oFG_UP_WiFi + "");
                listDiff.get(i).getNet().setFgDownWifi(nFG_DOWN_WiFi - oFG_DOWN_WiFi + "");
            }
        }

        return listDiff;
    }

    //makes the copy of the stats list.
    private static ArrayList<Stats> copyListStats(ArrayList<Stats> appListSource){
        ArrayList<Stats> newAppList = new ArrayList<>();

        for (Stats stat : appListSource) {
            newAppList.add(new Stats(stat));
        }
        return newAppList;
    }

    private static int getIndexByUID(ArrayList<Stats> appStats, String uid) {
        for(int i = 0; i < appStats.size(); i++) {
            if(appStats.get(i).getUid().equalsIgnoreCase(uid)){
                return i;
            }
        }
        return -1;
    }

    public boolean isMainProcess() {
        return !packageName.contains(":");
    }

    private static ArrayList getRunningApps(Context context){

        ArrayList list = new ArrayList<>();
        /*final int INDEX_OF_PR = 1;
        final int INDEX_OF_CPU = 2;
        final int INDEX_OF_Status = 3;
        final int INDEX_OF_THR = 4;
        final int INDEX_OF_VSS = 5;
        final int INDEX_OF_RSS = 6;
        final int INDEX_OF_PCY = 7;
        final int INDEX_OF_UID = 8;
        final int INDEX_OF_NAME = 9;
        final int TOP_LENGTH= 10;*/

        final int INDEX_OF_PCY  = 7;
        final int INDEX_OF_NAME = 9;
        final int TOP_LENGTH    = 10;

        String stats = getTopCommandData(context);

        //("Top-Command",stats);
        String lines[] = stats.trim().split("\n");

        for (String line : lines){
            String datas[] = line.trim().split("\\s+");

            if (datas.length < TOP_LENGTH){
                continue;
            }

            int uid = isSystemPackage(datas[INDEX_OF_NAME], context);

            //It's a user-installed app.
            if(uid > 0) {

                Stats statsData=new Stats();

                statsData.setPackageName(datas[INDEX_OF_NAME]);
                statsData.isInteracting = isInteractive(datas[INDEX_OF_NAME], context);
                statsData.setUid(uid + "");


                if(datas[INDEX_OF_PCY].equalsIgnoreCase("fg")){
                    statsData.setState(Constants.STATE_FOREGROUND);
                } else if(datas[INDEX_OF_PCY].equalsIgnoreCase("bg")) {
                    statsData.setState(Constants.STATE_BACKGROUND);
                }

                list.add(statsData);
            }

        }

        Collections.sort(list, new PackageNameComparator());

        return list;
    }

    /* This method will read top command file and filter out all the applications from it and return to calling function as a string.
     */
    private static String getTopCommandData(Context context){

        StringBuffer topData = new StringBuffer();
        try {
            Process process = Runtime.getRuntime().exec("top -d 0 -n 1");
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            //read a line from top command's virtual file
            while ((line = in.readLine()) != null) {
                topData.append(line+"\n");
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            NotificationView.show(context,"Error: Top command couldn't be executed successfully");
        }

        String data = topData.toString();
        int index = data.indexOf("Name");
        return data.substring(index + 4);
    }

    private static int isSystemPackage(String packageName, Context context){
        //if package is user-installed then returns uid of given pacakage otherwise returns a negative number.
        //in top command packages can also have multiple processes. Name of a process is name of package. But when having multiple processes, a tag is also included in package name.
        //for example: com.facebook.katana is a process. And it there can also another process called com.facebook.katana:Dash
        //When there are multiple processes, process name contains package and a tag in the following syntax. <packageName:tag>
        //In this application we are not going to combine stats usage for multiple processes.
        //We will store this information in a file. Since these processes share same UID our server will be able to distinguish the apps with multiple processes.

        if(packageName.contains(":")){   //multiple process. Remove the tag including colon.

            int pos = packageName.indexOf(':');
            packageName = packageName.substring(0, pos);
        }

        final PackageManager pm = context.getPackageManager();
        PackageInfo pkgInfo;
        try {
            pkgInfo = pm.getPackageInfo(packageName, PackageManager.GET_META_DATA);
            if(( pkgInfo.applicationInfo.flags &  ApplicationInfo.FLAG_SYSTEM) == 0){
                return pkgInfo.applicationInfo.uid;
            }
        } catch (PackageManager.NameNotFoundException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            Log.d(Settings.TAG,"Unknown error occurred while checking package type in CPUStatsExtractor class.. Details: "+e.toString());
        }

        return -1;
    }

    private static boolean isInteractive(String packageName, Context context){     //returns whether the given package is top activity or not.

        String currentApp = "NULL";
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            //noinspection ResourceType
            UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  time - 1000*60*60*50, time);

            if (appList != null && appList.size() > 0) {

                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<>();

                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                } if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
        } else {
            ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
            currentApp = tasks.get(0).processName;
        }

        if(currentApp.equals(packageName)){
            return true;
        }

        return false;
    }

    public String getStringData() {
        Calendar calTime = Calendar.getInstance(Locale.ENGLISH);
        SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy HH:mm:ss:SSS", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        String date = sdf.format(calTime.getTime());

        //date
        String formatStr = "%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s";
        return  String.format(formatStr
                , date
                , uid
                , packageName
                , isMainProcess()
                , isInteracting
                , state
                , net.getBgUpData()
                , net.getBgDownData()
                , net.getFgUpData()
                , net.getFgDownData()
                , net.getBgUpWifi()
                , net.getBgDownWifi()
                , net.getFgUpWifi()
                , net.getFgDownWifi());
    }

    static class PackageNameComparator implements Comparator<Stats> {
        @Override
        public int compare(Stats s1,Stats s2) {
            return s1.getPackageName().compareTo(s2.getPackageName());
        }
    }
}
