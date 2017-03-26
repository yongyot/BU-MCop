package th.ac.bu.mcop.activities;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.astuetz.PagerSlidingTabStrip;

import java.util.ArrayList;

import th.ac.bu.mcop.R;
import th.ac.bu.mcop.adapters.AppsViewPagerAdapter;
import th.ac.bu.mcop.models.realm.AppRealm;
import th.ac.bu.mcop.modules.api.ApplicationInfoManager;
import th.ac.bu.mcop.utils.Constants;
import th.ac.bu.mcop.utils.Settings;

public class ApplistActivity extends AppCompatActivity {

    private AppsViewPagerAdapter mAppsViewPagerAdapter;
    private ViewPager mViewPager;
    private PagerSlidingTabStrip mPagerSlidingTabStrip;

    ArrayList<ApplicationInfo> mApplicationInfosInstall;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applist);

        mApplicationInfosInstall = ApplicationInfoManager.getTotalApplicationUsingInternet(this);

        mViewPager = (ViewPager) findViewById(R.id.app_viewpager);
        mPagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);

        ArrayList<AppRealm> safeApps = AppRealm.getSafeApps();

        ArrayList<AppRealm> yellowApps = AppRealm.getWarningYellowApps();
        ArrayList<AppRealm> orangeApps = AppRealm.getWarningOrangeApps();
        ArrayList<AppRealm> redApps = AppRealm.getWarningRedApps();

        ArrayList<AppRealm> warning = new ArrayList<>();
        warning.addAll(redApps);
        warning.addAll(orangeApps);
        warning.addAll(yellowApps);

        mAppsViewPagerAdapter = new AppsViewPagerAdapter(getSupportFragmentManager(), safeApps, warning);
        mViewPager.setAdapter(mAppsViewPagerAdapter);
        mPagerSlidingTabStrip.setViewPager(mViewPager);
    }

    private void setAdapter(){
        ArrayList<AppRealm> safeApps = AppRealm.getSafeApps();

        ArrayList<AppRealm> yellowApps = AppRealm.getWarningYellowApps();
        ArrayList<AppRealm> orangeApps = AppRealm.getWarningOrangeApps();
        ArrayList<AppRealm> redApps = AppRealm.getWarningRedApps();

        ArrayList<AppRealm> warning = new ArrayList<>();
        warning.addAll(redApps);
        warning.addAll(orangeApps);
        warning.addAll(yellowApps);

        mAppsViewPagerAdapter.setAdapter(safeApps, warning);
        mAppsViewPagerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(Settings.TAG, "onActivityResult ApplistActivity : " + requestCode + ":" + resultCode);
        if (requestCode == Constants.REQUEST_CODE_APP_INFO){
            setAdapter();
        }
    }
}
