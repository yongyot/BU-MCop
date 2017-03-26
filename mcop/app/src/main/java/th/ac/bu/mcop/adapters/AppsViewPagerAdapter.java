package th.ac.bu.mcop.adapters;

import android.content.pm.ApplicationInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.ArrayList;

import th.ac.bu.mcop.fragments.SalfAppsFragment;
import th.ac.bu.mcop.fragments.WarningAppsFragment;
import th.ac.bu.mcop.models.realm.AppRealm;
import th.ac.bu.mcop.utils.Settings;

public class AppsViewPagerAdapter extends FragmentPagerAdapter {

    private final int NUM_ITEMS = 2;
    private ArrayList<ApplicationInfo> mApplicationInfos;
    private ArrayList<AppRealm> mSafeApps;
    private ArrayList<AppRealm> mWarningApps;

    public AppsViewPagerAdapter(FragmentManager fragmentManager, ArrayList<ApplicationInfo> applicationInfos) {
        super(fragmentManager);
        mApplicationInfos = applicationInfos;
    }

    public AppsViewPagerAdapter(FragmentManager fragmentManager, ArrayList<AppRealm> safeApps, ArrayList<AppRealm> warningApps){
        super(fragmentManager);
        mSafeApps = safeApps;
        mWarningApps = warningApps;
    }

    public void setAdapter(ArrayList<AppRealm> safeApps, ArrayList<AppRealm> warningApps){
        mSafeApps = safeApps;
        mWarningApps = warningApps;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                Log.d(Settings.TAG, "getItem 0");
                return SalfAppsFragment.newInstance(mSafeApps);
            case 1:
                Log.d(Settings.TAG, "getItem 1");
                return WarningAppsFragment.newInstance(mWarningApps);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0){
            return "Safe";
        } else if (position == 1){
            return "Suspicious";
        }
        return "";
    }
}
