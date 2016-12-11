package th.ac.bu.mcop.fragments;

import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import th.ac.bu.mcop.R;
import th.ac.bu.mcop.adapters.AppsRecycleViewAdapter;

/**
 * Created by jeeraphan on 12/11/16.
 */

public class AppsFragment extends Fragment{

    private static ArrayList<ApplicationInfo> mApplicationInfos;

    private RecyclerView mRecyclerView;
    private AppsRecycleViewAdapter mAppsRecycleViewAdapter;

    public static AppsFragment newInstance(ArrayList<ApplicationInfo> applicationInfos) {
        AppsFragment appsFragment = new AppsFragment();
        mApplicationInfos = applicationInfos;
        return appsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apps, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.app_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(layoutManager);

        mAppsRecycleViewAdapter = new AppsRecycleViewAdapter(container.getContext(), mApplicationInfos);
        mRecyclerView.setAdapter(mAppsRecycleViewAdapter);

        return view;
    }
}
