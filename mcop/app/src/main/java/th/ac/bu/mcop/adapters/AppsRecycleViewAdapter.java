package th.ac.bu.mcop.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;

import th.ac.bu.mcop.R;
import th.ac.bu.mcop.activities.InitializationActivity;
import th.ac.bu.mcop.models.realm.AppRealm;
import th.ac.bu.mcop.modules.api.ApplicationInfoManager;
import th.ac.bu.mcop.utils.Constants;
import th.ac.bu.mcop.utils.Settings;

public class AppsRecycleViewAdapter extends RecyclerView.Adapter<AppsRecycleViewAdapter.ViewHolder>{

    public interface OnAppListener {
        void onItemClickListener(int position);
    }

    //private ArrayList<ApplicationInfo> mApplicationInfos;
    private ArrayList<AppRealm> mApps;

    private Context mContext;
    private OnAppListener mOnAppListener;

    public AppsRecycleViewAdapter(Context context, ArrayList<AppRealm> apps){
        mApps = apps;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_apps, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        /*holder.appNameTextView.setText(mApplicationInfos.get(position).loadLabel(mContext.getPackageManager()));
        holder.packageNameTextView.setText(mApplicationInfos.get(position).packageName);
        holder.iconImageView.setImageDrawable(mApplicationInfos.get(position).loadIcon(mContext.getPackageManager()));
        holder.containerAppsRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnAppListener != null){
                    mOnAppListener.onItemClickListener(position);
                }
            }
        });*/

        ArrayList<ApplicationInfo> applicationInfos = ApplicationInfoManager.getTotalApplicationUsingInternet(mContext);
        for (ApplicationInfo appInfo : applicationInfos){
            if (appInfo.packageName.equals(mApps.get(position).getPackageName())){
                holder.iconImageView.setImageDrawable(appInfo.loadIcon(mContext.getPackageManager()));
                break;
            }
        }

        holder.appNameTextView.setText(mApps.get(position).getName());
        holder.packageNameTextView.setText(mApps.get(position).getPackageName());
        holder.containerAppsRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnAppListener != null){
                    mOnAppListener.onItemClickListener(position);
                }
            }
        });

        if (mApps.get(position).getAppStatus() == Constants.APP_STATUS_WARNING_RED){
            holder.iconStatusImageView.setImageResource(R.drawable.red);
        } else if (mApps.get(position).getAppStatus() == Constants.APP_STATUS_WARNING_ORANGE){
            holder.iconStatusImageView.setImageResource(R.drawable.orange);
        } else if (mApps.get(position).getAppStatus() == Constants.APP_STATUS_WARNING_YELLOW){
            holder.iconStatusImageView.setImageResource(R.drawable.yellow);
        } else {
            holder.iconStatusImageView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        if (mApps != null){
            return mApps.size();
        }
        return 0;
    }

    public void setOnAppListener(OnAppListener listener){
        mOnAppListener = listener;
    }

    public void setApps(ArrayList<AppRealm> apps){
        mApps = apps;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout containerAppsRelative;
        private ImageView iconImageView;
        private ImageView iconStatusImageView;
        private TextView appNameTextView;
        private TextView packageNameTextView;

        public ViewHolder(View view){
            super(view);
            containerAppsRelative = (RelativeLayout) view.findViewById(R.id.container_apps_relative);
            iconImageView = (ImageView) view.findViewById(R.id.icon_imageview);
            appNameTextView = (TextView) view.findViewById(R.id.app_name_textview);
            packageNameTextView = (TextView) view.findViewById(R.id.package_name_textview);
            iconStatusImageView = (ImageView) view.findViewById(R.id.icon_status_imageview);
        }
    }
}