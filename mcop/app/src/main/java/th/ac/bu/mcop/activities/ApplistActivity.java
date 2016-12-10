package th.ac.bu.mcop.activities;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;

import th.ac.bu.mcop.R;
import th.ac.bu.mcop.utils.Settings;

/**
 * Created by jeeraphan on 12/10/16.
 */

public class ApplistActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applist);

        getTotalApplicationUsingInternet();
    }

    private void getTotalApplicationUsingInternet(){

        PackageManager packageManager = getPackageManager();
        ArrayList<ApplicationInfo> applicationInfos = (ArrayList<ApplicationInfo>) packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        ArrayList<ApplicationInfo> applicationInfosInstall = new ArrayList<>();

        for (ApplicationInfo info : applicationInfos) {

            if (packageManager.getLaunchIntentForPackage(info.packageName) != null) {
                applicationInfosInstall.add(info);

                Log.d(Settings.TAG, "info: " + info.packageName);
            }
        }
    }
}
