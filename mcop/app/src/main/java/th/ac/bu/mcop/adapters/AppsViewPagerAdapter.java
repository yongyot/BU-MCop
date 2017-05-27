package th.ac.bu.mcop.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

import th.ac.bu.mcop.fragments.SalfAppsFragment;
import th.ac.bu.mcop.fragments.WarningAppsFragment;
import th.ac.bu.mcop.models.realm.AppRealm;

public class AppsViewPagerAdapter extends FragmentPagerAdapter {

    private final String SAFE_TITLE = "Safe";
    private final String SUSPICIOUS_TITLE = "Suspicious";

    private final int NUM_ITEMS = 2;

    public AppsViewPagerAdapter(FragmentManager fragmentManager){
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return SalfAppsFragment.newInstance();
            case 1:
                return WarningAppsFragment.newInstance();
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
            return SAFE_TITLE;
        } else if (position == 1){
            return SUSPICIOUS_TITLE;
        }
        return "";
    }
}
