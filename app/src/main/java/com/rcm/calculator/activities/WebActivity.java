package com.rcm.calculator.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.rcm.calculator.R;
import com.rcm.calculator.api.ApiInterface;
import com.rcm.calculator.api.Constants;
import com.rcm.calculator.models.PrivacyPolicy;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WebActivity extends AppCompatActivity {

    private TextView title;
    private ImageView backbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        String data = getIntent().getStringExtra("data");

        title = findViewById(R.id.webview_toolbar_title);
        backbtn = findViewById(R.id.webview_toolbar_back);

        WebView webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        WebViewClient webViewClient = new WebViewClient();
        webView.setWebViewClient(webViewClient);

        ApiInterface.getApiRequestInterface().getAllText(Constants.API_KEY)
                .enqueue(new Callback<List<PrivacyPolicy>>() {
                    @Override
                    public void onResponse(Call<List<PrivacyPolicy>> call, Response<List<PrivacyPolicy>> response) {
                        if (response.isSuccessful())
                        {
                            PrivacyPolicy privacy = response.body().get(0);
                            if (data.equals("disclaimer"))
                            {
                                title.setText("Disclaimer");
                                webView.loadData(""+privacy.getDisclaimer(),
                                        "text/html", "UTF-8");
                            }
                            else if (data.equals("privacy_policy"))
                            {
                                title.setText("Privacy Policy");
                                webView.loadData(""+privacy.getPrivacyPolicy(),
                                        "text/html", "UTF-8");
                            }
                            else if (data.equals("terms_conditions"))
                            {
                                title.setText("Terms & Conditions");
                                webView.loadData(""+privacy.getTermsConditions(),
                                        "text/html", "UTF-8");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<PrivacyPolicy>> call, Throwable t) {

                    }
                });

        backbtn.setOnClickListener(v ->
        {
            onBackPressed();
        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}