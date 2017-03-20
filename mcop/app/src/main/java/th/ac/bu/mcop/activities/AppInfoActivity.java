package th.ac.bu.mcop.activities;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import th.ac.bu.mcop.R;
import th.ac.bu.mcop.models.AppsInfo;
import th.ac.bu.mcop.models.realm.AppRealm;
import th.ac.bu.mcop.modules.api.ApplicationInfoManager;
import th.ac.bu.mcop.utils.Constants;
import th.ac.bu.mcop.utils.Settings;


public class AppInfoActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mUninstallAppButton, mIgnoreButton;
    private ApplicationInfo mApplicationInfo;
    private TextView mAppNameTextView, mPackageNameTextView, mVersionTextView, mStatusTextView, mStorageTextview;
    private ImageView mAppIconImageview;

    private String mPackageName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);

        mIgnoreButton = (Button) findViewById(R.id.ignore_button);
        mUninstallAppButton = (Button) findViewById(R.id.uninstall_app_button);
        mAppNameTextView = (TextView) findViewById(R.id.app_name_textview);
        mPackageNameTextView = (TextView) findViewById(R.id.package_name_textview);
        mVersionTextView = (TextView) findViewById(R.id.version_textview);
        mStatusTextView = (TextView) findViewById(R.id.status_textview);
        mStorageTextview = (TextView) findViewById(R.id.storage_textview);
        mAppIconImageview = (ImageView) findViewById(R.id.icon_imageview);
        mIgnoreButton.setOnClickListener(this);
        mUninstallAppButton.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            mPackageName = bundle.getString("put_extra_package_name");
            mApplicationInfo = getApplicationInfoSelect(mPackageName);

            if (mApplicationInfo != null){
                mAppNameTextView.setText(mApplicationInfo.loadLabel(getBaseContext().getPackageManager()));
                mPackageNameTextView.setText(mApplicationInfo.packageName);
                mAppIconImageview.setImageDrawable(mApplicationInfo.loadIcon(getBaseContext().getPackageManager()));

                AppsInfo appsInfo = AppRealm.getAppWithPackageName(mPackageName);
                if (appsInfo != null){
                    if (appsInfo.getAppStatus() == Constants.APP_STATUS_SAFE){
                        mIgnoreButton.setVisibility(View.INVISIBLE);
                    }
                }

                try {
                    PackageInfo packageInfo = getPackageManager().getPackageInfo(mApplicationInfo.packageName, 0);
                    mVersionTextView.setText(getString(R.string.label_version) + " " + packageInfo.versionName);

                    PackageInfo packageInfoPermission = getPackageManager().getPackageInfo(mApplicationInfo.packageName, PackageManager.GET_PERMISSIONS);
                    String[] requestedPermissions = packageInfoPermission.requestedPermissions;

                    /*String permissionString = "";
                    for (int i = 0; i < requestedPermissions.length; i++){
                        permissionString += requestedPermissions[i] + "\n";
                    }*/

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /***********************************************
     OnClickListener
     ************************************************/

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ignore_button){

        } else if (view.getId() == R.id.uninstall_app_button){

            final Intent intent = new Intent();
            intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.parse("package:" + mPackageName));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            startActivity(intent);
        }
    }

    private ApplicationInfo getApplicationInfoSelect(String packageName){

        ArrayList<ApplicationInfo> applicationInfos = ApplicationInfoManager.getTotalApplicationUsingInternet(this);
        for (ApplicationInfo apps : applicationInfos){
            if (apps.packageName.equals(packageName)){
                return apps;
            }
        }

        return null;
    }
}
