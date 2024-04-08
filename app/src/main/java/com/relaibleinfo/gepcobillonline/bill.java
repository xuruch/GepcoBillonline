package com.relaibleinfo.gepcobillonline;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;

public class bill extends AppCompatActivity {
    WebView webView;
    String url,refnum;
    AdView adView;
    RewardedInterstitialAd rewardedInterstitialAd;
    @SuppressLint("SetJavaScriptEnabled")
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);
        webView=findViewById(R.id.billwebview);
        adView=findViewById(R.id.adViewbill);
        AdRequest adRequest=new AdRequest.Builder().build();
        adView.loadAd(adRequest);
       refnum=getIntent().getStringExtra("Ref");
        url="https://bill.pitc.com.pk/gepcobill/general?refno=";
       url=url+refnum;
       webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setInitialScale(1);
        webView.loadUrl(url);



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(bill.this,MainActivity.class);
        webView.destroy();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}