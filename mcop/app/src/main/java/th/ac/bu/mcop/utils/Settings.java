package th.ac.bu.mcop.utils;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import th.ac.bu.mcop.widgets.NotificationView;

/**
 * Created by jeeraphan on 11/14/16.
 */

public class Settings {

    public static int sNetworkType;
    public static int sInterval;
    public static String sApplicationPath;
    public static String sWifiInterfaceName;
    public static String sMacAddress;
    public static String sOutputFileName;
    public static String sHashFilePath;
    public static boolean sIsUsageAccessGranted;

    public static void loadSetting(Context context){

        sInterval = 5;
        sIsUsageAccessGranted = isUsageAccessGranted(context);
        sWifiInterfaceName = getWifiInterfaceName();
        sMacAddress = getMacAddress(context);
        sApplicationPath = context.getCacheDir().toString() + "/BU-Stat-Collector/";
        sHashFilePath = sApplicationPath + "hashData";
        sOutputFileName = sMacAddress.replace(":", "-") + ".stats";
    }

    public static String getWifiInterfaceName(){

        try {
            if (NetworkInterface.getByName("wlan0") != null){
                return "wlan0";
            } else if (NetworkInterface.getByName("eth0") != null){
                return "eth0";
            } else if (NetworkInterface.getByName("eth1") != null){
                return "eth1";
            }
        } catch (Exception e){
            Log.d("emji","Can not get wifi network interface name.");
        }

        return "";
    }

    public static String getMacAddress(Context context){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){ // M is API 23

            try {
                List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
                for (NetworkInterface nif : all) {
                    if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                    byte[] macBytes = nif.getHardwareAddress();
                    if (macBytes == null) {
                        return "";
                    }
                    StringBuilder res1 = new StringBuilder();
                    for (byte b : macBytes) {
                        res1.append(Integer.toHexString(b & 0xFF) + ":");
                    }

                    if (res1.length() > 0) {
                        res1.deleteCharAt(res1.length() - 1);
                    }
                    return res1.toString();
                }
            } catch (Exception ex) {
                Log.d("emji","Can not retrieve mac address on Marshmallow");
                NotificationView.show(context, "Error getting mac address.");
            }

            return null;

        } else {
            WifiManager wifiManager = (WifiManager)context.getSystemService(context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            return wifiInfo.getMacAddress();
        }
    }

    public static boolean isUsageAccessGranted(Context context) {
        boolean result = false;
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){     //on lollipop or above try to get top most
            //noinspection ResourceType
            UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  time - 1000*60*60*50, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);

                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    result = true;
                }
            }
        } else {    //below 5.0 return true since no settings are rquired.
            result = true;
        }
        return result;
    }
}
