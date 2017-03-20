package th.ac.bu.mcop.activities;

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
        ArrayList<AppRealm> warning = AppRealm.getWarningApps();

        //mAppsViewPagerAdapter = new AppsViewPagerAdapter(getSupportFragmentManager(), mApplicationInfosInstall);
        mAppsViewPagerAdapter = new AppsViewPagerAdapter(getSupportFragmentManager(), safeApps, warning);
        mViewPager.setAdapter(mAppsViewPagerAdapter);
        mPagerSlidingTabStrip.setViewPager(mViewPager);
    }
}
