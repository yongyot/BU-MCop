package th.ac.bu.mcop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import th.ac.bu.mcop.R;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mRateButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        mRateButton = (Button) findViewById(R.id.rate_button);
        mRateButton.setOnClickListener(this);
    }

    /***********************************************
     OnClickListener
     ************************************************/

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.rate_button){

        }
    }
}
