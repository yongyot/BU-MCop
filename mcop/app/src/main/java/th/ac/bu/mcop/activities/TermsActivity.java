package th.ac.bu.mcop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import th.ac.bu.mcop.R;

/**
 * Created by jeeraphan on 12/10/16.
 */

public class TermsActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private Button mStartMonitoringButton;
    private CheckBox mAccpetCheckBox;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        mAccpetCheckBox = (CheckBox) findViewById(R.id.accept_checkbox);
        mAccpetCheckBox.setOnCheckedChangeListener(this);

        mStartMonitoringButton = (Button) findViewById(R.id.start_monitoring_button);
        mStartMonitoringButton.setOnClickListener(this);
    }

    /***********************************************
     OnClickListener
     ************************************************/

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.start_monitoring_button){
            Intent intent = new Intent(this, InitializationActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
        if (isCheck){
            mStartMonitoringButton.setEnabled(true);
        } else {
            mStartMonitoringButton.setEnabled(false);
        }
    }
}
