package th.ac.bu.mcop.activities;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.astuetz.PagerSlidingTabStrip;

import java.util.ArrayList;

import th.ac.bu.mcop.R;
import th.ac.bu.mcop.adapters.AppsViewPagerAdapter;
import th.ac.bu.mcop.modules.ApplicationInfoManager;

/**
 * Created by jeeraphan on 12/10/16.
 */

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

        mAppsViewPagerAdapter = new AppsViewPagerAdapter(getSupportFragmentManager(), mApplicationInfosInstall);
        mViewPager.setAdapter(mAppsViewPagerAdapter);
        mPagerSlidingTabStrip.setViewPager(mViewPager);
    }
}
