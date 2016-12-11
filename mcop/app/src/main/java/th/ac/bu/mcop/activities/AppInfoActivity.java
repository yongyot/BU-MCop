package th.ac.bu.mcop.activities;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.media.audiofx.BassBoost;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import th.ac.bu.mcop.R;
import th.ac.bu.mcop.utils.Settings;

/**
 * Created by jeeraphan on 12/11/16.
 */

public class AppInfoActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mUninstallAppButton, mIgnoreButton;
    private ApplicationInfo mApplicationInfo;
    private String mPackageName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            mPackageName = bundle.getString("put_extra_package_name");
            Log.d(Settings.TAG, "package name: " + mPackageName);
        }

        mIgnoreButton = (Button) findViewById(R.id.ignore_button);
        mUninstallAppButton = (Button) findViewById(R.id.uninstall_app_button);

        mIgnoreButton.setOnClickListener(this);
        mUninstallAppButton.setOnClickListener(this);
    }

    /***********************************************
     OnClickListener
     ************************************************/

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ignore_button){

        } else if (view.getId() == R.id.uninstall_app_button){

            final Intent intent = new Intent();
            intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.parse("package:" + mPackageName));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            startActivity(intent);
        }
    }
}
