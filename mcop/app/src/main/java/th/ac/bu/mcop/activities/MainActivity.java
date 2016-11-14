package th.ac.bu.mcop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import th.ac.bu.mcop.R;

/**
 * Created by jeeraphan on 10/28/16.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mUsageAccessButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsageAccessButton = (Button)findViewById(R.id.btnUsageAccess);
        mUsageAccessButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        Intent intent = new Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivity(intent);
    }
}