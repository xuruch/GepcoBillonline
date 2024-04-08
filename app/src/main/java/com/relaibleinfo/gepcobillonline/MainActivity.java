package com.relaibleinfo.gepcobillonline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdActivity;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class MainActivity extends AppCompatActivity {
     Button btnshow, btnclear;
     EditText editText;
    AdView madView;
    String  interadid="ca-app-pub-6500396294030647/4755662957";

    InterstitialAd minterstitialAd, interstitialAd1;
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

            }
        });
        madView=findViewById(R.id.adViewmain);
         loadinter();
         loadinetr1();
         AdRequest adRequest=new AdRequest.Builder().build();
        madView.loadAd(adRequest);
        btnshow=findViewById(R.id.btnshowbill);
        editText=findViewById(R.id.billrefnum);
        btnclear=findViewById(R.id.btnclearbtn);

        btnclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              if(interstitialAd1!=null){
                  interstitialAd1.show(MainActivity.this);
              }
              else{
                  editText.getText().clear();
              }

            }
        });


         btnshow.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                if(minterstitialAd!=null)
                {
                    minterstitialAd.show(MainActivity.this);
                }
                else {
                    if (editText.getText().toString().trim().length() < 14 || editText.getText().toString().trim().length() > 14) {
                        editText.setError("14 Digit reference Number Required");
                    } else {
                        Intent i = new Intent(MainActivity.this, bill.class);
                        i.putExtra("Ref", editText.getText().toString());
                        startActivity(i);
                        finish();

                        // Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
                        //Toast.makeText(MainActivity.this, "Number"+Refnumber, Toast.LENGTH_SHORT).show();
                    }
                }
             }
         });

    }

    private void loadinter() {
         InterstitialAd.load(MainActivity.this, interadid, new AdRequest.Builder().build(), new InterstitialAdLoadCallback() {
             @Override
             public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                 super.onAdFailedToLoad(loadAdError);
               minterstitialAd=null;
             }

             @Override
             public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                 super.onAdLoaded(interstitialAd);
                 minterstitialAd=interstitialAd;
                 minterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                     @Override
                     public void onAdClicked() {
                         super.onAdClicked();
                     }

                     @Override
                     public void onAdDismissedFullScreenContent() {

                         super.onAdDismissedFullScreenContent();
                         if (editText.getText().toString().trim().length() < 14 || editText.getText().toString().trim().length() > 14) {
                             editText.setError("14 Digit reference Number Required");
                         } else {
                             Intent i = new Intent(MainActivity.this, bill.class);
                             i.putExtra("Ref", editText.getText().toString());
                             startActivity(i);
                             finish();
                         }
                         }

                     @Override
                     public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                         super.onAdFailedToShowFullScreenContent(adError);
                     }

                     @Override
                     public void onAdImpression() {
                         super.onAdImpression();
                     }

                     @Override
                     public void onAdShowedFullScreenContent() {
                         super.onAdShowedFullScreenContent();
                     }
                 });
             }
         });
    }
    void loadinetr1(){
        InterstitialAd.load(MainActivity.this, interadid, new AdRequest.Builder().build(), new InterstitialAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                interstitialAd1=null;
            }

            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);
                interstitialAd1=interstitialAd;
                interstitialAd1.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        editText.getText().clear();
                        super.onAdDismissedFullScreenContent();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);
                    }

                    @Override
                    public void onAdImpression() {
                        super.onAdImpression();
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent();
                    }
                });
            }
        });

    }
}