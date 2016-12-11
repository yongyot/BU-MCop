package th.ac.bu.mcop.activities;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import th.ac.bu.mcop.R;
import th.ac.bu.mcop.modules.ApplicationInfoManager;

/**
 * Created by jeeraphan on 12/10/16.
 */

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mAboutButton, mManageAppButton;
    private TextView mTotalInstalledAppTextView, mAppUsingInternetTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mTotalInstalledAppTextView = (TextView) findViewById(R.id.number_total_install_textview);
        mAppUsingInternetTextView = (TextView) findViewById(R.id.number_app_using_internet_textview);
        mAboutButton = (Button) findViewById(R.id.about_button);
        mManageAppButton = (Button) findViewById(R.id.manage_app_button);

        mManageAppButton.setOnClickListener(this);
        mAboutButton.setOnClickListener(this);

        ArrayList<ApplicationInfo> applicationInstalled = ApplicationInfoManager.getTotalApplication(this);
        ArrayList<ApplicationInfo> applicationUsingInternet = ApplicationInfoManager.getTotalApplicationUsingInternet(this);

        mTotalInstalledAppTextView.setText(applicationInstalled.size() + "");
        mAppUsingInternetTextView.setText(applicationUsingInternet.size() + "");
    }

    /***********************************************
     OnClickListener
     ************************************************/

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.about_button){
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.manage_app_button){
            Intent intent = new Intent(this, ApplistActivity.class);
            startActivity(intent);
        }
    }
}
