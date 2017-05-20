package th.ac.bu.mcop.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import th.ac.bu.mcop.R;
import th.ac.bu.mcop.utils.Constants;
import th.ac.bu.mcop.utils.Settings;
import th.ac.bu.mcop.utils.SharePrefs;

public class TermsActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private Button mStartMonitoringButton;
    private CheckBox mAccpetCheckBox;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        if (SharePrefs.getPreferenceBoolean(this, Constants.KEY_ACCEPT_TERM, false)){
            startHomeActivity();
        }

        mAccpetCheckBox = (CheckBox) findViewById(R.id.accept_checkbox);
        mAccpetCheckBox.setOnCheckedChangeListener(this);

        mStartMonitoringButton = (Button) findViewById(R.id.start_monitoring_button);
        mStartMonitoringButton.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mStartMonitoringButton.setEnabled(false);
        mAccpetCheckBox.setChecked(false);
    }

    /***********************************************
     OnClickListener
     ************************************************/

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.start_monitoring_button){
            startInitializationActivity();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
        if (isCheck){

            if (Settings.isUsageAccessGranted(this)){
                mStartMonitoringButton.setEnabled(true);
                mAccpetCheckBox.setChecked(true);
            } else {
                mAccpetCheckBox.setChecked(false);
                new AlertDialog
                        .Builder(this)
                        .setTitle(getString(R.string.app_name))
                        .setMessage(getString(R.string.label_msg_tern_on_usage))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.label_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(getString(R.string.label_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mAccpetCheckBox.setChecked(false);
                            }
                        }).show();
            }
        } else {
            mStartMonitoringButton.setEnabled(false);
            mAccpetCheckBox.setChecked(false);
        }
    }

    private void startInitializationActivity(){
        Intent intent = new Intent(this, InitializationActivity.class);
        startActivity(intent);
        finish();
    }

    private void startHomeActivity(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
