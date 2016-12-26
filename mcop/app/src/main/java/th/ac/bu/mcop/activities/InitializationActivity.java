package th.ac.bu.mcop.activities;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import th.ac.bu.mcop.R;
import th.ac.bu.mcop.models.AppsInfo;
import th.ac.bu.mcop.models.response.ReportHeaderModel;
import th.ac.bu.mcop.models.response.ReportModel;
import th.ac.bu.mcop.models.response.ResponseModel;
import th.ac.bu.mcop.modules.HashGenManager;
import th.ac.bu.mcop.modules.VirusTotalResponse;
import th.ac.bu.mcop.modules.api.ApiManager;
import th.ac.bu.mcop.utils.Constants;
import th.ac.bu.mcop.utils.Settings;
import th.ac.bu.mcop.utils.SharePrefs;
import th.ac.bu.mcop.widgets.NotificationView;

/**
 * Created by jeeraphan on 12/10/16.
 */

public class InitializationActivity extends AppCompatActivity implements HashGenManager.OnHashGenListener{

    private ImageView mCircleImageview;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initialization);

        mCircleImageview = (ImageView) findViewById(R.id.circle_imageview);
        animateScal();

        Settings.loadSetting(this);
        //initHasFile();

        startHomeActivity();
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

        Log.d(Settings.TAG, "initHasFile: " + Settings.sMacAddress);

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
                    SharePrefs.setPreference(getBaseContext(), Constants.KEY_FIRST_TIME, true);
                    HashGenManager hashGen = new HashGenManager();
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
        getReport();
    }

    private void getReport(){
        ArrayList<String> mockups = new ArrayList<>();
        mockups.add("d1f3cd0f2b963d8fb9f527d1b3a60063");
        mockups.add("74c7e83af4ec0d4db0cbc02e4a148d98");
        mockups.add("e59208c6241720955338a8c0d37b3f15");
        mockups.add("bc08fb75c7b3a43bb34b909f1c4bf089");
        mockups.add("c3c7d676ae4edd28939fbd34fa4b7ea6");
        mockups.add("f138523f869128e962b1481cb861393e");
        mockups.add("827615eee2af7e3d74222016a21f4734");
        mockups.add("800bb4c04c8fd90ee4c210d6d2b5625e");
        mockups.add("ac5af37855bb7c8ca4df95bc5cd81790");
        mockups.add("eb7753ffe76bdfc6148baaf6e05d3402");
        mockups.add("0340e7fd93209990d9333543115c6a5e");
        mockups.add("4942f355ef2abb9f6a190bd655444bfe");
        mockups.add("4942f355ef2abb9f6a190bd655444bfe");
        mockups.add("3d01fa94750b079451d9cc93b5fb8636");
        mockups.add("f56eebe0e88241a288697d5e358a333b");
        mockups.add("f19414e9e7b65be3a57507823f1027eb");
        mockups.add("810ed60e1a2e7e8a0e7cbdcdffb30277");
        mockups.add("f6f00ec8c60ad4535b4a5d9197ad4226");
        mockups.add("18f91ff9a9f670a1114496c76e364c50");
        mockups.add("7aa1dad1d2ed99e6ad4470ff8cd0aa2c");
        mockups.add("cbb9126669405c9f5ad33bb69fb7e8a8");
        mockups.add("31bb35574ca4e01a5557ec3cbebb5060");
        mockups.add("39cee2caf7e85b433bce5b56f7209328");
        mockups.add("dc1ad8a1b869389ef121cfe6ce06b441");
        mockups.add("8dbd1e2e93aaa25cc10131d86e6c4408");
        mockups.add("70e1da00449457fdf8dce7b67294bc1b");
        mockups.add("d185de75d4108759ecc8717361a0dfd1");
        mockups.add("2d1ed21673068cc953c2416832e24ed6");



        ApiManager.getInstance().getReport(new Callback<ResponseModel<ReportHeaderModel<ReportModel>>>() {
            @Override
            public void onResponse(Call<ResponseModel<ReportHeaderModel<ReportModel>>> call, Response<ResponseModel<ReportHeaderModel<ReportModel>>> response) {
                Log.d(Settings.TAG, "onResponse");
                Log.d(Settings.TAG, response.body().toString());

                startHomeActivity();
            }

            @Override
            public void onFailure(Call<ResponseModel<ReportHeaderModel<ReportModel>>> call, Throwable t) {
                Log.d(Settings.TAG, "onFailure");
                Log.d(Settings.TAG, call.toString());
            }
        }, getAllHashReport());
    }

    public ArrayList<String> getAllHashReport() {

        String selfApk = "th.ac.bu.mcop";
        PackageManager packageManager = getPackageManager();
        final List<ApplicationInfo> apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        ArrayList<String> hashs = new ArrayList<>();

        for (ApplicationInfo appInfo : apps) {
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 && !appInfo.packageName.equals(selfApk)) {

                HashGenManager hashGen = new HashGenManager();
                AppsInfo app = hashGen.getPackageInfo(appInfo.packageName, getBaseContext());
                hashs.add(app.getHash());
            }
        }

        return hashs;
    }

    private void startHomeActivity(){
        mHandler.postDelayed(new Runnable() {
            public void run() {
                Intent intent = new  Intent(InitializationActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}
