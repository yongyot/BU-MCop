package th.ac.bu.mcop.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import th.ac.bu.mcop.R;
import th.ac.bu.mcop.activities.AppInfoActivity;
import th.ac.bu.mcop.adapters.AppsRecycleViewAdapter;
import th.ac.bu.mcop.models.realm.AppRealm;
import th.ac.bu.mcop.utils.Settings;

public class SalfAppsFragment extends Fragment implements AppsRecycleViewAdapter.OnAppListener{
    private static ArrayList<AppRealm> mApps;

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

        mAppsRecycleViewAdapter = new AppsRecycleViewAdapter(container.getContext(), mApps);
        mAppsRecycleViewAdapter.setOnAppListener(this);
        mRecyclerView.setAdapter(mAppsRecycleViewAdapter);

        return view;
    }

    /***********************************************
     AppsRecycleViewAdapter.OnAppListener
     ************************************************/

    @Override
    public void onItemClickListener(int position) {
        Intent intent = new Intent(getActivity(), AppInfoActivity.class);
        intent.putExtra("put_extra_package_name", mApps.get(position).getPackageName());
        startActivity(intent);
    }
}
