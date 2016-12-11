package th.ac.bu.mcop.adapters;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import th.ac.bu.mcop.R;

/**
 * Created by jeeraphan on 12/11/16.
 */

public class AppsRecycleViewAdapter extends RecyclerView.Adapter<AppsRecycleViewAdapter.ViewHolder> {

    private ArrayList<ApplicationInfo> mApplicationInfos;
    private Context mContext;

    public AppsRecycleViewAdapter(Context context, ArrayList<ApplicationInfo> applicationInfos){
        mApplicationInfos = applicationInfos;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_apps, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.appNameTextView.setText(mApplicationInfos.get(position).loadLabel(mContext.getPackageManager()));
        holder.packageNameTextView.setText(mApplicationInfos.get(position).packageName);
        holder.iconImageView.setImageDrawable(mApplicationInfos.get(position).loadIcon(mContext.getPackageManager()));
    }

    @Override
    public int getItemCount() {
        if (mApplicationInfos != null){
            return mApplicationInfos.size();
        }
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView iconImageView;
        private TextView appNameTextView;
        private TextView packageNameTextView;

        public ViewHolder(View view){
            super(view);

            iconImageView = (ImageView) view.findViewById(R.id.icon_imageview);
            appNameTextView = (TextView) view.findViewById(R.id.app_name_textview);
            packageNameTextView = (TextView) view.findViewById(R.id.package_name_textview);
        }
    }
}