package th.ac.bu.mcop.modules;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import th.ac.bu.mcop.models.Net;
import th.ac.bu.mcop.models.Stats;
import th.ac.bu.mcop.utils.Settings;
import th.ac.bu.mcop.widgets.NotificationView;

/**
 * Created by jeeraphan on 11/16/16.
 */

public class NetStatsExtractor {

    private Context mContext;
    private String mDataInterface;

    public NetStatsExtractor(Context context){
        mContext = context;
        mDataInterface = "rmnet0";
    }

    public ArrayList<Stats> collectNetStats(ArrayList<Stats> listStats) {

        //In this method we will go through each application we found in Top command and we will set the network data in appList.net for each application
        //Some applications can have multiple processes. So when there is a multiple process we will read the data for it just once.

        String unFilteredStats = readProcFile();

        if(unFilteredStats.trim().isEmpty()){
            return null;
        }

        for(int i = 0; i < listStats.size();i++) {
            if(listStats.get(i).isMainProcess()) {

                Net net = getNetStats(listStats.get(i).getUid(), unFilteredStats);
                listStats.get(i).getNet().copyNet(net);
            } else {
                listStats.get(i).getNet().setEmtpy();
            }
        }
        return listStats;
    }

    public ArrayList<Stats> collectEmptyNetStats(ArrayList<Stats> appsList) {
        //In this method we will go through each application we found in Top command and we will set the network data in appList.net for each application
        //Some applications can have multiple processes. So when there is a multiple process we will read the data for it just once.

        for(int i = 0; i < appsList.size(); i++) {
            appsList.get(i).getNet().setNegative();
        }
        return appsList;
    }

    public Net getNetStats(String uid, String unFilteredStats){

        final int TAG = 2;
        final int RX_BYTES = 5;
        final int TX_BYTES = 7;
        final int INTERFACE = 1;
        final int APP_UID = 3;

        boolean wifiFound = false;
        boolean dataFound = false;

        Net net = new Net();
        String lines[] = unFilteredStats.split("\n");

        try{

            for (int i = 0; i < lines.length; i++){

                String dataBg[] = lines[i].trim().split("\\s+");

                //this is the uid we are looking for. There will be two consecutive lines. First for background data and second for foreground data for same interface.
                if (dataBg[APP_UID].equalsIgnoreCase(uid) && dataBg[TAG].equalsIgnoreCase("0x0")){

                    // We already read the first now read second one.
                    String dataFg[] = lines[i + 1].trim().split("\\s+");
                    i++;

                    if (dataBg[INTERFACE].equalsIgnoreCase(Settings.sWifiInterfaceName)){
                        //wifi background data for up and down is here

                        net.setBgUpWifi(dataBg[TX_BYTES]);
                        net.setBgDownWifi(dataBg[RX_BYTES]);
                        net.setFgUpWifi(dataFg[TX_BYTES]);
                        net.setFgDownWifi(dataFg[RX_BYTES]);

                        wifiFound = true;

                    } else if (dataBg[INTERFACE].equalsIgnoreCase(mDataInterface)){
                        //read for mobile background and foreground here

                        net.setBgUpData(dataBg[TX_BYTES]);
                        net.setBgDownData(dataBg[RX_BYTES]);
                        net.setFgUpData(dataFg[TX_BYTES]);
                        net.setFgDownData(dataFg[RX_BYTES]);

                        dataFound = true;
                    }
                }

                if (wifiFound && dataFound){
                    break;
                }
            }

        }catch (Exception e){
            net.sError = true;
            Log.d(Settings.TAG, "Might be malformed proc file from net stats extractor. Details: " + e.toString());
        }

        return net;
    }

    private String readProcFile(){


        String procFileName = "/proc/net/xt_qtaguid/stats";
        StringBuffer fileData = new StringBuffer();
        BufferedReader bufferedReader = null;

        try {

            bufferedReader = new BufferedReader(new FileReader(procFileName));
            String line;

            while ((line = bufferedReader.readLine()) != null){
                fileData.append(line + "\n");
            }
            bufferedReader.close();

        } catch (Exception e){

            NotificationView.show(mContext, "Couldn't read network file");

        } finally {
            try {
                if (bufferedReader != null){
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return fileData.toString();
    }
}
