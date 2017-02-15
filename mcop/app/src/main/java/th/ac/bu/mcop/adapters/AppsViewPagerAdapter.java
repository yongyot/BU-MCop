package th.ac.bu.mcop.adapters;

import android.content.pm.ApplicationInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

import th.ac.bu.mcop.fragments.AppsFragment;

/**
 * Created by jeeraphan on 12/11/16.
 */

public class AppsViewPagerAdapter extends FragmentPagerAdapter {

    private final int NUM_ITEMS = 2;
    private ArrayList<ApplicationInfo> mApplicationInfos;

    public AppsViewPagerAdapter(FragmentManager fragmentManager, ArrayList<ApplicationInfo> applicationInfos) {
        super(fragmentManager);
        mApplicationInfos = applicationInfos;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return AppsFragment.newInstance(mApplicationInfos);
            case 1:
                return AppsFragment.newInstance(mApplicationInfos);
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
            return "Warning";
        }
        return "";
    }
}
