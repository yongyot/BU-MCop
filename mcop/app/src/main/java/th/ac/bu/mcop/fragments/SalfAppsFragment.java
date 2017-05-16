package th.ac.bu.mcop.fragments;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import th.ac.bu.mcop.R;
import th.ac.bu.mcop.activities.AppInfoActivity;
import th.ac.bu.mcop.adapters.AppsRecycleViewAdapter;
import th.ac.bu.mcop.models.realm.AppRealm;
import th.ac.bu.mcop.modules.api.ApplicationInfoManager;
import th.ac.bu.mcop.utils.Constants;

public class SalfAppsFragment extends Fragment implements AppsRecycleViewAdapter.OnAppListener{
    private static ArrayList<AppRealm> mApps;
    private ArrayList<Drawable> mDrawables;

    private RecyclerView mRecyclerView;
    private AppsRecycleViewAdapter mAppsRecycleViewAdapter;

    public static SalfAppsFragment newInstance(ArrayList<AppRealm> apps){
        SalfAppsFragment salfAppsFragment = new SalfAppsFragment();
        mApps = apps;
        return salfAppsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apps, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.app_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        mDrawables = getDrawable();
        mAppsRecycleViewAdapter = new AppsRecycleViewAdapter(container.getContext(), mApps, mDrawables);
        mAppsRecycleViewAdapter.setOnAppListener(this);
        mRecyclerView.setAdapter(mAppsRecycleViewAdapter);

        return view;
    }

    private ArrayList<Drawable> getDrawable(){

        ArrayList<Drawable> drawables = new ArrayList<>();

        ArrayList<ApplicationInfo> applicationInfos = ApplicationInfoManager.getTotalApplicationUsingInternet(getContext());

        for (int i = 0; i < mApps.size(); i++){
            for (ApplicationInfo appInfo : applicationInfos){
                if (appInfo.packageName.equals(mApps.get(i).getPackageName())){
                    drawables.add(appInfo.loadIcon(getContext().getPackageManager()));
                }
            }
        }

        return drawables;
    }

    /***********************************************
     AppsRecycleViewAdapter.OnAppListener
     ************************************************/

    @Override
    public void onItemClickListener(int position) {
        Intent intent = new Intent(getActivity(), AppInfoActivity.class);
        intent.putExtra("put_extra_package_name", mApps.get(position).getPackageName());
        intent.putExtra("put_extra_app_status", mApps.get(position).getAppStatus());
        getActivity().startActivityForResult(intent, Constants.REQUEST_CODE_APP_INFO);
    }
}
