package th.ac.bu.mcop.activities;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import java.net.NetworkInterface;
import java.util.Set;

import th.ac.bu.mcop.R;
import th.ac.bu.mcop.modules.HashGen;
import th.ac.bu.mcop.utils.Settings;
import th.ac.bu.mcop.utils.SharePrefs;
import th.ac.bu.mcop.widgets.NotificationView;

/**
 * Created by jeeraphan on 12/10/16.
 */

public class InitializationActivity extends AppCompatActivity implements HashGen.OnHashGenListener{

    private ImageView mCircleImageview;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initialization);

        mCircleImageview = (ImageView) findViewById(R.id.circle_imageview);
        animateScal();

        Settings.loadSetting(this);
        initHasFile();
    }

    private void animateScal(){

        AnimatorSet animator = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.scal);
        animator.setTarget(mCircleImageview);
        animator.start();
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {}

            @Override
            public void onAnimationEnd(Animator animator) {
                animateScal();
            }

            @Override
            public void onAnimationCancel(Animator animator) {}

            @Override
            public void onAnimationRepeat(Animator animator) {}
        });
    }

    private void initHasFile(){

        if(Settings.sMacAddress != null) {

            try {
                if (NetworkInterface.getByName("rmnet0") == null) {
                    NotificationView.show(this, "Data interface error.");
                }
            } catch(Exception ex) {
                Log.d(Settings.TAG, "Can not find data interface name. Details: " + ex.toString());
            }

            new Thread(new Runnable() {
                public void run() {
                    SharePrefs.setPreference(getBaseContext(), "firstTime", true);
                    HashGen hashGen = new HashGen();
                    hashGen.setOnHashGenListener(InitializationActivity.this);
                    hashGen.getAllAppInfo(getBaseContext());
                }
            }).start();
        }
    }

    /***********************************************
     OnHashGenListener
     ************************************************/

    @Override
    public void onHashGenFinished() {
        Log.d(Settings.TAG, "onHashGenFinished");
        mHandler.postDelayed(new Runnable() {
            public void run() {
                Intent intent = new  Intent(InitializationActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}
