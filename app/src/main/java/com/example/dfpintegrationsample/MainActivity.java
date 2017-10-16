package com.example.dfpintegrationsample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.cxense.LoadCallback;
import com.cxense.cxensesdk.CxenseConfiguration;
import com.cxense.cxensesdk.CxenseSdk;
import com.cxense.cxensesdk.model.UserIdentity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;

import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String EXTERNAL_ID_TYPE = "cx";
    private TextView textView;
    private PublisherAdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textView = findViewById(R.id.segment_name);
        adView = findViewById(R.id.ad_view);
        PublisherAdRequest.Builder adRequestBuilder = new PublisherAdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR);

        CxenseSdk cxenseSdk = CxenseSdk.getInstance();
        final CxenseConfiguration cxenseConfiguration = cxenseSdk.getConfiguration();
        cxenseConfiguration.setUsername(BuildConfig.CX_USERNAME);
        cxenseConfiguration.setApiKey(BuildConfig.CX_API_KEY);
        UserIdentity identity = new UserIdentity(getExternalId(), EXTERNAL_ID_TYPE);
        cxenseSdk.getUserSegmentIds(Collections.singletonList(identity), Collections.singletonList(BuildConfig.CX_SITEGROUP_ID), new LoadCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> segments) {
                textView.setText(getString(R.string.segment_text, TextUtils.join(", ", segments)));
                loadAds(adRequestBuilder.addCustomTargeting("CxSegments", segments).build());
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e(TAG, "Error at getting segments", throwable);
                loadAds(adRequestBuilder.build());
            }
        });
    }

    @Override
    protected void onPause() {
        if (adView != null)
            adView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adView != null)
            adView.resume();
    }

    @Override
    protected void onDestroy() {
        if (adView != null)
            adView.destroy();
        super.onDestroy();
    }

    private void loadAds(PublisherAdRequest adRequest) {
        adView.loadAd(adRequest);
    }

    private String getExternalId() {
        // You should return some external id, which previously was pushed to Cxense with event or profile update.
        return "";
    }
}
