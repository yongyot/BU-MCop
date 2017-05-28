package th.ac.bu.mcop.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import th.ac.bu.mcop.R;
import th.ac.bu.mcop.models.AppsInfo;
import th.ac.bu.mcop.models.realm.AppRealm;
import th.ac.bu.mcop.modules.api.ApplicationInfoManager;
import th.ac.bu.mcop.utils.Constants;


public class AppInfoActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mUninstallAppButton, mIgnoreButton, mBackButton, mLeveButton;
    private ApplicationInfo mApplicationInfo;
    private TextView mAppNameTextView, mPackageNameTextView, mVersionTextView, mUpdatedTextView;
    private Button mSafeButton, mLowButton, mMediumButton, mHightButton;
    private RelativeLayout mContainerButton;
    private ImageView mAppIconImageview;
    private TextView mSan1TextView, mSan2TextView, mSan3TextView, mSan4TextView, mSan5TextView;

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
        mAppIconImageview = (ImageView) findViewById(R.id.icon_imageview);
        mVersionTextView = (TextView) findViewById(R.id.version_value_textview);
        mUpdatedTextView = (TextView) findViewById(R.id.update_value_textview);
        mContainerButton = (RelativeLayout) findViewById(R.id.container_button);
        mBackButton = (Button) findViewById(R.id.back_button);

        mSafeButton = (Button) findViewById(R.id.safe_button);
        mLowButton = (Button) findViewById(R.id.low_button);
        mMediumButton = (Button) findViewById(R.id.medium_button);
        mHightButton = (Button) findViewById(R.id.high_button);
        mLeveButton = (Button) findViewById(R.id.level_button);

        mSan1TextView = (TextView) findViewById(R.id.scan1_textview);
        mSan2TextView = (TextView) findViewById(R.id.scan2_textview);
        mSan3TextView = (TextView) findViewById(R.id.scan3_textview);
        mSan4TextView = (TextView) findViewById(R.id.scan4_textview);
        mSan5TextView = (TextView) findViewById(R.id.scan5_textview);

        mIgnoreButton.setOnClickListener(this);
        mUninstallAppButton.setOnClickListener(this);
        mBackButton.setOnClickListener(this);

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

        SimpleDateFormat format = new SimpleDateFormat("MMM dd ,yyyy");
        String dateString = format.format(new Date());

        mUpdatedTextView.setText(dateString);

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
                    mVersionTextView.setText(packageInfo.versionName);

                    if (mAppStatus == Constants.APP_STATUS_SAFE){

                        mLowButton.setBackgroundResource(R.drawable.empty);
                        mMediumButton.setBackgroundResource(R.drawable.empty);
                        mHightButton.setBackgroundResource(R.drawable.empty);

                        mLowButton.setTextColor(Color.GRAY);
                        mMediumButton.setTextColor(Color.GRAY);
                        mHightButton.setTextColor(Color.GRAY);

                        mLeveButton.setBackgroundResource(R.drawable.risk_safe);
                        mLeveButton.setText(R.string.label_safe);

                    } else if (mAppStatus == Constants.APP_STATUS_WARNING_YELLOW){

                        mSafeButton.setBackgroundResource(R.drawable.empty);
                        mMediumButton.setBackgroundResource(R.drawable.empty);
                        mHightButton.setBackgroundResource(R.drawable.empty);

                        mSafeButton.setTextColor(Color.GRAY);
                        mMediumButton.setTextColor(Color.GRAY);
                        mHightButton.setTextColor(Color.GRAY);

                        mLeveButton.setBackgroundResource(R.drawable.risk_low);
                        mLeveButton.setText(R.string.label_Low);

                    } else if (mAppStatus == Constants.APP_STATUS_WARNING_ORANGE){

                        mSafeButton.setBackgroundResource(R.drawable.empty);
                        mLowButton.setBackgroundResource(R.drawable.empty);
                        mHightButton.setBackgroundResource(R.drawable.empty);

                        mSafeButton.setTextColor(Color.GRAY);
                        mLowButton.setTextColor(Color.GRAY);
                        mHightButton.setTextColor(Color.GRAY);

                        mLeveButton.setBackgroundResource(R.drawable.risk_medium);
                        mLeveButton.setText(R.string.label_medium);

                    } else if (mAppStatus == Constants.APP_STATUS_WARNING_RED){

                        mSafeButton.setBackgroundResource(R.drawable.empty);
                        mLowButton.setBackgroundResource(R.drawable.empty);
                        mMediumButton.setBackgroundResource(R.drawable.empty);

                        mSafeButton.setTextColor(Color.GRAY);
                        mLowButton.setTextColor(Color.GRAY);
                        mMediumButton.setTextColor(Color.GRAY);

                        mLeveButton.setBackgroundResource(R.drawable.risk_high);
                        mLeveButton.setText(R.string.label_high);
                    }

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }

            AppsInfo appInfo = AppRealm.getAppWithPackageName(mPackageName);

            if (appInfo == null) {
                return;
            }

            if (appInfo.getScan() == null) {
                return;
            }

            String[] apps = appInfo.getScan().split(",");
            if (apps.length > 4) {
                mSan1TextView.setText(apps[0]);
                mSan2TextView.setText(apps[1]);
                mSan3TextView.setText(apps[2]);
                mSan4TextView.setText(apps[3]);
                mSan5TextView.setText(apps[4]);
            } else if (apps.length > 3) {
                mSan1TextView.setText(apps[0]);
                mSan2TextView.setText(apps[1]);
                mSan3TextView.setText(apps[2]);
                mSan4TextView.setText(apps[3]);
            } else if (apps.length > 2) {
                mSan1TextView.setText(apps[0]);
                mSan2TextView.setText(apps[1]);
                mSan3TextView.setText(apps[2]);
            } else if (apps.length > 1) {
                mSan1TextView.setText(apps[0]);
                mSan2TextView.setText(apps[1]);
            } else if (appInfo.getScan().length() > 0) {
                mSan1TextView.setText(appInfo.getScan().replace(",", ""));
            }
        }
    }

    /***********************************************
     OnClickListener
     ************************************************/

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ignore_button){

            new AlertDialog
                    .Builder(this)
                    .setTitle(getString(R.string.app_name))
                    .setMessage("Do you want to ignore this Application?")
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.label_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            AppRealm.updateWithPackage(mPackageName, Constants.APP_STATUS_SAFE);
                            setResult(Constants.RESULT_IGNORE);
                            finish();
                        }
                    })
                    .setNegativeButton(getString(R.string.label_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).show();

        } else if (view.getId() == R.id.uninstall_app_button){

            final Intent intent = new Intent();
            intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.parse("package:" + mPackageName));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            startActivity(intent);
        } else if (view.getId() == R.id.back_button){
            finish();
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
