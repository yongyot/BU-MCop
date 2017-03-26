package th.ac.bu.mcop.activities;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
    private TextView mAppNameTextView, mPackageNameTextView, mVersionTextView, mStatusTextView, mStorageTextview, mPermissionTextView;
    private RelativeLayout mContainerButton;
    private ImageView mAppIconImageview;

    private String mPackageName;
    private int mAppStatus;

    @RequiresApi(api = Build.VERSION_CODES.M)
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
        mPermissionTextView = (TextView) findViewById(R.id.permission_textview);
        mAppIconImageview = (ImageView) findViewById(R.id.icon_imageview);
        mContainerButton = (RelativeLayout) findViewById(R.id.container_button);
        mIgnoreButton.setOnClickListener(this);
        mUninstallAppButton.setOnClickListener(this);

        setView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            getPackageManager().getPackageInfo(mPackageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            AppRealm.deleteAppWithPackageName(mPackageName);
            setResult(Constants.RESULT_DELETE);
            finish();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setView(){

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            mPackageName = bundle.getString("put_extra_package_name");
            mAppStatus = bundle.getInt("put_extra_app_status");
            mApplicationInfo = getApplicationInfoSelect(mPackageName);

            if (mApplicationInfo != null){
                mAppNameTextView.setText(mApplicationInfo.loadLabel(getBaseContext().getPackageManager()));
                mPackageNameTextView.setText(mApplicationInfo.packageName);
                mAppIconImageview.setImageDrawable(mApplicationInfo.loadIcon(getBaseContext().getPackageManager()));

                AppsInfo appsInfo = AppRealm.getAppWithPackageName(mPackageName);
                if (appsInfo != null){
                    if (appsInfo.getAppStatus() == Constants.APP_STATUS_SAFE){
                        mContainerButton.setVisibility(View.GONE);
                    }
                }

                try {
                    PackageInfo packageInfo = getPackageManager().getPackageInfo(mApplicationInfo.packageName, 0);
                    mVersionTextView.setText(getString(R.string.label_version) + " : " + packageInfo.versionName);

                    String appStatus = "";
                    if (mAppStatus == Constants.APP_STATUS_SAFE){
                        appStatus = "Safe";
                    } else if (mAppStatus == Constants.APP_STATUS_WARNING_YELLOW){
                        appStatus = "Low";
                    } else if (mAppStatus == Constants.APP_STATUS_WARNING_ORANGE){
                        appStatus = "Medium";
                    } else if (mAppStatus == Constants.APP_STATUS_WARNING_RED){
                        appStatus = "High";
                    }

                    mStatusTextView.setText(getString(R.string.label_risk_level) + " : " + appStatus);

                    PackageInfo packageInfoPermission = getPackageManager().getPackageInfo(mApplicationInfo.packageName, PackageManager.GET_PERMISSIONS);
                    String[] requestedPermissions = packageInfoPermission.requestedPermissions;

                    String permissionString = "";
                    for (int i = 0; i < requestedPermissions.length; i++){
                        permissionString += requestedPermissions[i] + "\n";
                    }
                    mPermissionTextView.setText(getString(R.string.label_permission) + "\n" + permissionString);
                    mPermissionTextView.setVisibility(View.INVISIBLE);
                    mStorageTextview.setVisibility(View.INVISIBLE);

                    setTextColor(mAppStatus);

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setTextColor(int appStatus){
        if (appStatus == Constants.APP_STATUS_SAFE){

//            mAppNameTextView.setTextColor(Color.GREEN);
//            mPackageNameTextView .setTextColor(Color.GREEN);
//            mVersionTextView .setTextColor(Color.GREEN);
            mStatusTextView.setTextColor(Color.GREEN);
            mStatusTextView.setTextColor(getColor(R.color.colorGreenSafe));
//            mStorageTextview .setTextColor(Color.GREEN);
//            mPermissionTextView.setTextColor(Color.GREEN);

        } else if (appStatus == Constants.APP_STATUS_WARNING_YELLOW){

//            mAppNameTextView.setTextColor(Color.YELLOW);
//            mPackageNameTextView .setTextColor(Color.YELLOW);
//            mVersionTextView .setTextColor(Color.YELLOW);
            mStatusTextView.setTextColor(Color.YELLOW);
//            mStorageTextview .setTextColor(Color.YELLOW);
//            mPermissionTextView.setTextColor(Color.YELLOW);

        } else if (appStatus == Constants.APP_STATUS_WARNING_ORANGE){

//            mAppNameTextView.setTextColor(Color.rgb(255,127,80));
//            mPackageNameTextView .setTextColor(Color.rgb(255,127,80));
//            mVersionTextView .setTextColor(Color.rgb(255,127,80));
            mStatusTextView.setTextColor(Color.rgb(255,127,80));
//            mStorageTextview .setTextColor(Color.rgb(255,127,80));
//            mPermissionTextView.setTextColor(Color.rgb(255,127,80));

        } else if (appStatus == Constants.APP_STATUS_WARNING_RED){

//            mAppNameTextView.setTextColor(Color.RED);
//            mPackageNameTextView .setTextColor(Color.RED);
//            mVersionTextView .setTextColor(Color.RED);
            mStatusTextView.setTextColor(Color.RED);
//            mStorageTextview .setTextColor(Color.RED);
//            mPermissionTextView.setTextColor(Color.RED);
        }
    }

    /***********************************************
     OnClickListener
     ************************************************/

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ignore_button){

            AppRealm.updateWithPackage(mPackageName, Constants.APP_STATUS_SAFE);
            setResult(Constants.RESULT_IGNORE);
            finish();

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
