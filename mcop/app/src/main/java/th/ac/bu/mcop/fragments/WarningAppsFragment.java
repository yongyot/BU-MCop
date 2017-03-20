package th.ac.bu.mcop.fragments;

import android.app.Activity;
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
import th.ac.bu.mcop.utils.Constants;
import th.ac.bu.mcop.utils.Settings;

public class WarningAppsFragment extends Fragment implements AppsRecycleViewAdapter.OnAppListener{
    private static ArrayList<AppRealm> mApps;

    private RecyclerView mRecyclerView;
    private AppsRecycleViewAdapter mAppsRecycleViewAdapter;

    public static WarningAppsFragment newInstance(ArrayList<AppRealm> apps){
        WarningAppsFragment appsFragment = new WarningAppsFragment();
        mApps = apps;
        return appsFragment;
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
        startActivityForResult(intent, Constants.REQUEST_CODE_APP_INFO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_APP_INFO && resultCode == Constants.RESULT_DELETE){
            mApps = AppRealm.getWarningApps();
            mAppsRecycleViewAdapter.setApps(mApps);
            mAppsRecycleViewAdapter.notifyDataSetChanged();
        }
    }
}
