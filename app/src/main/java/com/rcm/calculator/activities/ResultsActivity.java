package com.rcm.calculator.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.snackbar.Snackbar;
import com.rcm.calculator.R;
import com.rcm.calculator.adapters.TotalIncentiveAdapters;
import com.rcm.calculator.adapters.DownLineAdapters;
import com.rcm.calculator.models.FieldData;
import com.rcm.calculator.utils.ConnectionReceiver;
import com.rcm.calculator.utils.Tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ResultsActivity extends AppCompatActivity implements ConnectionReceiver.ReceiverListener {

    TextView bv, rate, pb, total, total1, total2, ResultSelfText, ResultSelfPv;
    LinearLayout tds, net;
    RecyclerView rv2, ResultsRecyclerView2;
    Bitmap myBitmap;
    AlertDialog dialog;

    private LinearLayout admobBanner;

    private String adType, adStatus, interstitialAdsCount, adMobBannerID, FacebookBannerID, interstitialAdmob, interstitialFacebook, nativeAdmob, catID, catTitle;
    private int Click = 0,countInt;
    private FrameLayout MainLayout;

    private ImageView BackBtn, ScreenShotBtn;
    private double userPv = 0.0;

    private List<FieldData> fieldData = new ArrayList<FieldData>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        adMobBannerID = Tools.getString(this,"bannerAdmob");
        adStatus = Tools.getString(this,"AdsStatus");
        interstitialAdsCount = Tools.getString(this,"interstitialAdsCount");
        interstitialAdmob = Tools.getString(this,"interstitialAdmob");
        Click = Tools.getInt(this,"click");
        countInt = Integer.parseInt(interstitialAdsCount);

        fieldData = (List<FieldData>) getIntent().getSerializableExtra("fieldata");

        userPv = getIntent().getDoubleExtra("userpv",0.0);

        bv = findViewById(R.id.bv);
        rate = findViewById(R.id.rate);
        pb = findViewById(R.id.pb);
        total = findViewById(R.id.total);
        total1 = findViewById(R.id.total1);
        total2 = findViewById(R.id.total2);
        tds = findViewById(R.id.tds);
        net = findViewById(R.id.net);
        rv2 = findViewById(R.id.rv2);
        ResultsRecyclerView2 = findViewById(R.id.result_recyclerview2);
        admobBanner = findViewById(R.id.adView);
        MainLayout = findViewById(R.id.result_main_layout);
        BackBtn = findViewById(R.id.results_toolbar_back);
        ScreenShotBtn = findViewById(R.id.results_toolbar_screenshot);
        ResultSelfText = findViewById(R.id.results_self_pvtext);
        ResultSelfPv = findViewById(R.id.results_self_pv);

        Intent i = getIntent();
        if(i.getStringExtra("percent") == null){
            finish();
        }else{
            double pvP =  Double.parseDouble(i.getStringExtra("percent"))/ 100;
            double selfPv = i.getDoubleExtra("userpv",0.0)*pvP;
            ResultSelfPv.setText(String.valueOf(selfPv));
            bv.setText(String.valueOf(round(Double.parseDouble(i.getStringExtra("value")),2)));
            rate.setText(i.getStringExtra("percent") +"%");

            Double nn = Double.valueOf(i.getStringExtra("value"));
            Double totalpercent = Double.parseDouble(i.getStringExtra("percent")) / 100;
            double bonus = nn*totalpercent;
            pb.setText(String.valueOf(round(bonus,2)));
            for (FieldData d: fieldData) {
                if (Tools.getPercent(Double.parseDouble(d.getUserPV()))==0)
                {
                    double pt =  Double.parseDouble(i.getStringExtra("percent")) / 100;
                    selfPv = selfPv + Double.parseDouble(d.getUserPV()) * pt;
                }
                else
                {
                    double percent = Double.parseDouble(i.getStringExtra("percent"))-Tools.getPercent(Double.parseDouble(d.getUserPV()));
                    double pt = percent / 100;
                    selfPv = selfPv + Double.parseDouble(d.getUserPV()) * pt;
                }
            }

            if(bonus>1000){
                net.setVisibility(View.VISIBLE);
                total.setText(String.valueOf(round(selfPv,2)));
                tds.setVisibility(View.VISIBLE);
                double ttt = selfPv * 0.02;
                total2.setText(String.valueOf(round(ttt,2)));
                total1.setText(String.valueOf(round(selfPv-ttt,2)));
            }else{
                total1.setText(String.valueOf(round(selfPv,2)));
                total.setText(String.valueOf(round(selfPv,2)));
            }
        }

        DownLineAdapters adapter = new DownLineAdapters(this,fieldData,Double.parseDouble(i.getStringExtra("percent")));
        rv2.setHasFixedSize(true);
        rv2.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        rv2.setAdapter(adapter);

        TotalIncentiveAdapters adapter2 = new TotalIncentiveAdapters(fieldData,Double.parseDouble(i.getStringExtra("percent")));
        ResultsRecyclerView2.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        ResultsRecyclerView2.setAdapter(adapter2);




        if (adStatus.equals("1"))
        {
            loadAdMobBanner();
        }

        BackBtn.setOnClickListener(view ->
        {
            onBackPressed();
        });

        ScreenShotBtn.setOnClickListener(view ->{
           screenShot();
        });

    }

    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    private void loadAdMobBanner() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdView mAdView = new AdView(this);
        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId(adMobBannerID);
        AdRequest adRequest = new AdRequest.Builder().build();
        if(mAdView.getAdSize() != null || mAdView.getAdUnitId() != null)
            mAdView.loadAd(adRequest);

        admobBanner.addView(mAdView);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }
        });
    }

    public void screenShot(){

        LinearLayout r = findViewById(R.id.scren);
        r.setDrawingCacheEnabled(true);
        r.buildDrawingCache();
        r.setDrawingCacheEnabled(true);
        myBitmap = r.getDrawingCache();


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View customLayout = getLayoutInflater().inflate(R.layout.screenshot, null);

        ImageView img = customLayout.findViewById(R.id.img);
        CardView save = customLayout.findViewById(R.id.save);
        CardView share = customLayout.findViewById(R.id.share);
        img.setImageBitmap(myBitmap);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                saveBitmap();
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                shareBitmap();
            }
        });
        builder.setView(customLayout);

        dialog = builder.create();
        dialog.show();
    }

    public void saveBitmap()  {
        String filePath = Environment.getExternalStorageDirectory()+"/Download/RCM_Commission_Calculator_"+ Calendar.getInstance().getTime().toString().replaceAll(":", ".")+".jpg";
        File imagePath = new File(filePath);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imagePath);
            myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            Tools.showSnackbar(ResultsActivity.this,MainLayout,"Image Saved To Downloads","Okay");
            //dialog.dismiss();
        } catch (IOException e) {
            Log.e("GREC", e.getMessage(), e);
        }
    }
    public void shareBitmap()  {
        try {
            File cachePath = new File(ResultsActivity.this.getCacheDir(), "images");
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
            myBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        File imagePath = new File(ResultsActivity.this.getCacheDir(), "images");
        File newFile = new File(imagePath, "image.png");
        Uri contentUri = FileProvider.getUriForFile(ResultsActivity.this, "com.rcm.calculator.fileprovider", newFile);

        if (contentUri != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_TEXT,"Download RCM commission from play store : https://play.google.com/store/apps/details?id=" + getPackageName());
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            startActivity(Intent.createChooser(shareIntent, "Choose an app"));
        }
    }

    @Override
    public void onBackPressed() {
        if (adStatus.equals("1"))
        {
            Click = Tools.getInt(this,"click");

            if (Click==0){
                Tools.showInterstitialAdfromAdmob(this);
                Tools.loadInterstitialAdfromAdmob(this);
            }
            else if (Click==countInt)
            {
                Tools.showInterstitialAdfromAdmob(this);
                Tools.loadInterstitialAdfromAdmob(this);
            }
            else
            {
                int value = Tools.getInt(this,"click");
                Tools.saveInt(this,"click",1+value);
            }
        }

        super.onBackPressed();
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
    protected void onPause() {
        super.onPause();
        // call method
        checkConnection(false);
    }

    protected void onResume() {
        super.onResume();
        if (adStatus.equals("1"))
        {
            Tools.loadInterstitialAdfromAdmob(this);
        }
        checkConnection(false);
    }

    @Override
    public void onNetworkChange(boolean isConnected) {
        checkConnection(false);
    }
}