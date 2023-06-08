package com.rcm.calculator.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.rcm.calculator.R;
import com.rcm.calculator.api.ApiInterface;
import com.rcm.calculator.api.Constants;
import com.rcm.calculator.models.InstallUsers;
import com.rcm.calculator.models.Settings;
import com.rcm.calculator.utils.ConnectionReceiver;
import com.rcm.calculator.utils.Tools;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity implements ConnectionReceiver.ReceiverListener {

    private String versionCode = "0";
    private int versionCodeInt = 0;
    private FirebaseAnalytics mFirebaseAnalytics;
    private FrameLayout MainLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        MainLayout = findViewById(R.id.splash_main_layout);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        int firstTime = Tools.getInt(SplashActivity.this,"firstTime");

        if (firstTime==0)
        {
            ApiInterface.getApiRequestInterface().saveInstallation(Constants.API_KEY)
                    .enqueue(new Callback<InstallUsers>() {
                        @Override
                        public void onResponse(Call<InstallUsers> call, Response<InstallUsers> response) {
                            if (response.isSuccessful())
                            {
                                Tools.saveInt(SplashActivity.this,"firstTime",1);
                            }
                        }

                        @Override
                        public void onFailure(Call<InstallUsers> call, Throwable t) {

                        }
                    });
        }

        FirebaseApp.initializeApp(this);
        FirebaseMessaging.getInstance().subscribeToTopic("plistnew")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                        } else {
                        }
                    }
                });

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionCode = String.valueOf(pInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        loadData();
    }

    private void loadData() {
        // Initialize connectivity manager
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        // Initialize network info
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        // get connection status
        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();

        if (isConnected)
        {
            ApiInterface.getApiRequestInterface().getSettings(Constants.API_KEY).enqueue(new Callback<List<Settings>>() {
                @Override
                public void onResponse(Call<List<Settings>> call, Response<List<Settings>> response) {
                    if (response.isSuccessful())
                    {
                        List<Settings> settingsList = response.body();
                        Settings settings = settingsList.get(0);
                        Tools.saveString(SplashActivity.this,"id",settings.getId());
                        Tools.saveString(SplashActivity.this,"version",settings.getVersionCode());
                        Tools.saveString(SplashActivity.this,"AdsStatus",settings.getAdsStatus());
                        Tools.saveString(SplashActivity.this,"interstitialAdsCount",settings.getInterstitialAdsCount());
                        Tools.saveString(SplashActivity.this,"adsLimit",settings.getAdsLimit());
                        Tools.saveString(SplashActivity.this,"bannerAdmob",settings.getBannerAdmob());
                        Tools.saveString(SplashActivity.this,"interstitialAdmob",settings.getInterstitialAdmob());
                        Tools.saveString(SplashActivity.this,"nativeAdmob",settings.getNativeAdmob());
                        Tools.saveString(SplashActivity.this,"appOpenAdmob",settings.getAppOpenAdmob());

                        versionCodeInt = Integer.parseInt(versionCode);
                        if (versionCode.equals(settings.getVersionCode()) || versionCodeInt>Integer.parseInt(settings.getVersionCode()))
                        {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                    finish();
                                    finishAffinity();
                                }
                            },2000);

                        }
                        else
                        {
                            if (!versionCode.equals("0"))
                            {
                                Tools.showUpdateDialog(SplashActivity.this);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Settings>> call, Throwable t) {
                }
            });
        }
        else
        {
            Snackbar snackbar = Snackbar.make(MainLayout,"",Snackbar.LENGTH_INDEFINITE);
            View customView = getLayoutInflater().inflate(R.layout.custom_snackbar,null);
            snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
            Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
            snackbarLayout.setPadding(0,0,0,0);

            TextView titleText = customView.findViewById(R.id.custom_snackbar_text);
            Button Btn = customView.findViewById(R.id.custom_snackbar_btn);

            titleText.setText("Not Connected to Internet");
            Btn.setText("Retry");

            Btn.setOnClickListener(v ->
            {
                snackbar.dismiss();
                loadData();
            });

            snackbarLayout.addView(customView,0);
            snackbar.show();
        }
    }

    private void checkConnection(boolean recreate) {

        // Initialize connectivity manager
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        // Initialize network info
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        // get connection status
        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();

        // display snack bar
        showSnackBar(isConnected, recreate);
    }

    private void showSnackBar(boolean isConnected, boolean recreate) {
        if (isConnected)
        {
            if (recreate)
                recreate();
        }
        else
        {
            Snackbar snackbar = Snackbar.make(MainLayout,"",Snackbar.LENGTH_INDEFINITE);
            View customView = getLayoutInflater().inflate(R.layout.custom_snackbar,null);
            snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
            Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
            snackbarLayout.setPadding(0,0,0,0);

            TextView titleText = customView.findViewById(R.id.custom_snackbar_text);
            Button Btn = customView.findViewById(R.id.custom_snackbar_btn);

            titleText.setText("Not Connected to Internet");
            Btn.setText("Retry");

            Btn.setOnClickListener(v ->
            {
                snackbar.dismiss();
                checkConnection(true);
            });

            snackbarLayout.addView(customView,0);
            snackbar.show();
        }
    }

    @Override
    public void onNetworkChange(boolean isConnected) {
        checkConnection(false);
    }
}