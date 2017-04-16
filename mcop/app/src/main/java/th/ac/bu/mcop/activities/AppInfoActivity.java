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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import th.ac.bu.mcop.R;
import th.ac.bu.mcop.models.AppsInfo;
import th.ac.bu.mcop.models.realm.AppRealm;
import th.ac.bu.mcop.modules.api.ApplicationInfoManager;
import th.ac.bu.mcop.utils.Constants;
import th.ac.bu.mcop.utils.Settings;


public class AppInfoActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mUninstallAppButton, mIgnoreButton;
    private ApplicationInfo mApplicationInfo;
    private TextView mAppNameTextView, mPackageNameTextView, mVersionTextView, mUpdatedTextView;
    private Button mSafeButton, mLowButton, mMediumButton, mHightButton;
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
        mAppIconImageview = (ImageView) findViewById(R.id.icon_imageview);
        mVersionTextView = (TextView) findViewById(R.id.version_value_textview);
        mUpdatedTextView = (TextView) findViewById(R.id.update_value_textview);
        mContainerButton = (RelativeLayout) findViewById(R.id.container_button);

        mSafeButton = (Button) findViewById(R.id.safe_button);
        mLowButton = (Button) findViewById(R.id.low_button);
        mMediumButton = (Button) findViewById(R.id.medium_button);
        mHightButton = (Button) findViewById(R.id.high_button);

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

                    } else if (mAppStatus == Constants.APP_STATUS_WARNING_YELLOW){

                        mSafeButton.setBackgroundResource(R.drawable.empty);
                        mMediumButton.setBackgroundResource(R.drawable.empty);
                        mHightButton.setBackgroundResource(R.drawable.empty);

                        mSafeButton.setTextColor(Color.GRAY);
                        mMediumButton.setTextColor(Color.GRAY);
                        mHightButton.setTextColor(Color.GRAY);

                    } else if (mAppStatus == Constants.APP_STATUS_WARNING_ORANGE){

                        mSafeButton.setBackgroundResource(R.drawable.empty);
                        mLowButton.setBackgroundResource(R.drawable.empty);
                        mHightButton.setBackgroundResource(R.drawable.empty);

                        mSafeButton.setTextColor(Color.GRAY);
                        mLowButton.setTextColor(Color.GRAY);
                        mHightButton.setTextColor(Color.GRAY);

                    } else if (mAppStatus == Constants.APP_STATUS_WARNING_RED){

                        mSafeButton.setBackgroundResource(R.drawable.empty);
                        mLowButton.setBackgroundResource(R.drawable.empty);
                        mMediumButton.setBackgroundResource(R.drawable.empty);

                        mSafeButton.setTextColor(Color.GRAY);
                        mLowButton.setTextColor(Color.GRAY);
                        mMediumButton.setTextColor(Color.GRAY);
                    }

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
