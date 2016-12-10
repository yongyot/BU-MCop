package th.ac.bu.mcop.activities;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import th.ac.bu.mcop.R;

/**
 * Created by jeeraphan on 12/10/16.
 */

public class InitializationActivity extends AppCompatActivity {

    private ImageView mCircleImageview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initialization);

        mCircleImageview = (ImageView) findViewById(R.id.circle_imageview);
        animateScal();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent intent = new  Intent(InitializationActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
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
}
