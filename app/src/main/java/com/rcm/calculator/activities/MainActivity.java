package com.rcm.calculator.activities;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.rcm.calculator.R;
import com.rcm.calculator.adapters.FieldAdapters;
import com.rcm.calculator.adapters.MyListAdapter;
import com.rcm.calculator.databinding.ActivityMainBinding;
import com.rcm.calculator.models.FieldData;
import com.rcm.calculator.utils.ConnectionReceiver;
import com.rcm.calculator.utils.PermissionUtil;
import com.rcm.calculator.utils.Tools;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  implements PermissionUtil.PermissionsCallBack, ConnectionReceiver.ReceiverListener {
    public static String[] hints  = {"A group", "B group", "C group", "D group", "E group", "F group", "g group", "H group", "I group", "J group", "K group", "L group", "M group", "N group", "O group", "P group", "Q group", "R group", "S group", "T group"};

    ActionBarDrawerToggle toggle;
    private String adStatus, adMobBannerID;
    private List<FieldData> fieldDataList = new ArrayList<>();
    private ActivityMainBinding binding;
    private FieldAdapters fieldAdapters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adMobBannerID = Tools.getString(this,"bannerAdmob");
        adStatus = Tools.getString(this,"AdsStatus");

        toggle = new ActionBarDrawerToggle(MainActivity.this, binding.mainDrawerLayout, R.string.open, R.string.close);
        binding.mainDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        requestPermissions();

        binding.homeNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId = item.getItemId();
                if (itemId == R.id.store)
                {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://store.jayrcm.com")));
                    return true;
                }
                else if (itemId == R.id.other_app)
                {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/dev?id=8398019413364187771")));
                    return true;
                }
                else if (itemId == R.id.feedback)
                {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://support.jayrcm.com/")));
                    return true;
                }
                else if (itemId == R.id.rate_us) {
                    String appPackageName = getPackageName();
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                    return true;
                } else if (itemId == R.id.disclaimer) {
                    Intent disIntent = new Intent(MainActivity.this, WebActivity.class);
                    disIntent.putExtra("data", "disclaimer");
                    startActivity(disIntent);
                    return true;
                } else if (itemId == R.id.privacy_policy) {
                    Intent priIntent = new Intent(MainActivity.this, WebActivity.class);
                    priIntent.putExtra("data", "privacy_policy");
                    startActivity(priIntent);
                    return true;
                } else if (itemId == R.id.terms_conditions) {
                    Intent termIntent = new Intent(MainActivity.this, WebActivity.class);
                    termIntent.putExtra("data", "terms_conditions");
                    startActivity(termIntent);
                    return true;
                } else if (itemId == R.id.contact_us) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://support.jayrcm.com/")));
                    return true;
                }
                return false;
            }
        });

        binding.homeDrawerClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Code Here
                binding.mainDrawerLayout.openDrawer(GravityCompat.START);
            }
        });


        binding.reset.setOnClickListener(view -> {
            finish();
            fieldDataList.clear();
            startActivity(getIntent());
        });


        fieldDataList.add(new FieldData());
        fieldDataList.add(new FieldData());

        binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fieldAdapters.getItemCount() < 20) {
                    fieldAdapters.addNewField();
                } else {
                    Tools.showSnackbar(MainActivity.this, binding.getRoot(), "Maximum 20 Group can be Added", "Okay");
                }
            }
        });

        binding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fieldDataList.size()>0){
                    fieldDataList.remove(fieldDataList.size()-1);
                    fieldAdapters.notifyDataSetChanged();
                }
            }
        });

        fieldAdapters = new FieldAdapters(MainActivity.this,fieldDataList,hints);
        binding.rv.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        binding.rv.setAdapter(fieldAdapters);


        binding.calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ttt = binding.totalbusiness.getText().toString();
                if(ttt.length()>0){
                    double pv = Double.parseDouble(ttt);
                    double totalpercent = Tools.getPercent(pv), userPv = Double.parseDouble(ttt);
                    if(totalpercent==0.0){
                        Tools.showSnackbar(MainActivity.this,binding.getRoot(),"Please Enter Pv More Then 5000","Okay");
                        return;
                    }
                    boolean fff = false;

                    boolean tu = false;
                    for (FieldData d: fieldDataList) {
                        if(d.getUserPV()==null){
                            fff= true;
                        }
                        else
                        {
                            if(Double.parseDouble(d.getUserPV())>= pv){
                                tu = true;
                            }
                            if (fieldDataList.size()>0)
                            {
                                userPv = userPv-Double.parseDouble(d.getUserPV());
                            }
                        }
                    }
                    if(totalpercent >= 0.0 ){
                        if(pv< 5000){
                            Tools.showSnackbar(MainActivity.this,binding.getRoot(),"Please Enter At least 5000 PV!","Okay");
                        }
                        else{
                            if(fff){
                                Tools.showSnackbar(MainActivity.this,binding.getRoot(),"Each Group PV is Required.","Okay");
                            }
                            else{
                                if(tu){
                                    Tools.showSnackbar(MainActivity.this,binding.getRoot(),"Total Business Is Invalid.","Okay");
                                }else{
                                    Intent i = new Intent(MainActivity.this, ResultsActivity.class);
                                    i.putExtra("percent", String.valueOf(totalpercent));
                                    i.putExtra("value", ttt);
                                    i.putExtra("userpv", userPv);
                                    i.putExtra("fieldata", (Serializable) fieldDataList);
                                    startActivity(i);
                                }
                            }
                        }
                    }
                }
                else{
                    Tools.showSnackbar(MainActivity.this,binding.getRoot(),"Total Business is Required.","Okay");
                }
            }
        });

        if (adStatus.equals("1"))
        {
            loadAdMobBanner();
        }
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

        binding.adView.addView(mAdView);

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

    @Override
    public void onBackPressed() {
        if (binding.mainDrawerLayout.isDrawerOpen(GravityCompat.START))
        {
            binding.mainDrawerLayout.closeDrawer(GravityCompat.START);
        }
        showExitDialog();
    }

    private void showExitDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(R.layout.custom_sheet_dialog);
        Button rateusBtn = dialog.findViewById(R.id.modal_sheet_rate_us);
        Button shareBtn = dialog.findViewById(R.id.modal_sheet_share);
        Button quitBtn = dialog.findViewById(R.id.modal_sheet_quit);

        quitBtn.setOnClickListener(v -> {
            finishAffinity();
        });

        rateusBtn.setOnClickListener(v -> {
            String appPackageName = getPackageName();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        });

        shareBtn.setOnClickListener(v -> {
            String appPackageName = getPackageName();
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out this app: https://play.google.com/store/apps/details?id=" + appPackageName);
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "Share using"));
        });

        if (dialog.isShowing())
            dialog.dismiss();
        else
            dialog.show();
    }

    public void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PermissionUtil.checkAndRequestPermissions(this,
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.onRequestPermissionsResult(this, requestCode, permissions, grantResults, this);
    }

    @Override
    public void permissionsGranted() {
    }

    @Override
    public void permissionsDenied() {
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
            Snackbar snackbar = Snackbar.make(binding.mainDrawerLayout,"",Snackbar.LENGTH_INDEFINITE);
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

        checkConnection(false);
    }

    @Override
    public void onNetworkChange(boolean isConnected) {
        checkConnection(false);
    }
}