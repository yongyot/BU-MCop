package th.ac.bu.mcop.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import th.ac.bu.mcop.utils.Constants;
import th.ac.bu.mcop.utils.Settings;

public class IntenetReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean isWifiConn = networkInfo.isConnected();
        networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean isMobileConn = networkInfo.isConnected();

        if (isWifiConn){
            Settings.sNetworkType = Constants.NETWORK_TYPE_WIFI;
        } else if (isMobileConn){
            Settings.sNetworkType = Constants.NETWORK_TYPE_MOBILE;
        } else {
            Settings.sNetworkType = Constants.NETWORK_TYPE_NO_NETWORK;
        }
    }
}
