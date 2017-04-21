package th.ac.bu.mcop.modules.api;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.ArrayList;

import th.ac.bu.mcop.utils.Settings;

public class ApplicationInfoManager {

    public static ArrayList<ApplicationInfo> getTotalApplication(Activity activity){

        ArrayList<ApplicationInfo> applicationInfosInstall = new ArrayList<>();

        PackageManager packageManager = activity.getPackageManager();
        ArrayList<ApplicationInfo> applicationInfos = (ArrayList<ApplicationInfo>) packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        if (applicationInfos == null){
            return new ArrayList<>();
        }

        for (ApplicationInfo info : applicationInfos) {
            String[] nativeLibraryDirs = info.nativeLibraryDir.split("/");
            if (nativeLibraryDirs.length > 0){
                if (!nativeLibraryDirs[0].equals("system")){
                    applicationInfosInstall.add(info);
                }
            }
        }

        return applicationInfosInstall;
    }

    public static ArrayList<ApplicationInfo> getTotalApplicationUsingInternet(Context context){

        ArrayList<ApplicationInfo> applicationInfosInstall = new ArrayList<>();

        PackageManager packageManager = context.getPackageManager();
        ArrayList<ApplicationInfo> applicationInfos = (ArrayList<ApplicationInfo>) packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        if (applicationInfos == null){
            return new ArrayList<>();
        }

        for (ApplicationInfo info : applicationInfos) {

            if (packageManager.getLaunchIntentForPackage(info.packageName) != null) {
                applicationInfosInstall.add(info);
            }
        }

        return applicationInfosInstall;
    }
}
