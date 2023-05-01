package com.example.myapplication.ads;

import static androidx.lifecycle.Lifecycle.Event.ON_START;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.example.myapplication.TinyDB;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;
import java.util.Objects;

public class AppOpenManager implements Application.ActivityLifecycleCallbacks, LifecycleObserver {
    private static final String LOG_TAG = "AppOpenManager";
    private AppOpenAd appOpenAd = null;

    private AppOpenAd.AppOpenAdLoadCallback loadCallback;
    private Activity currentActivity;
    private static boolean isShowingAd = false;

    private final Application myApplication;


    static TinyDB tinydb;

    /**
     * Constructor
     */
    public AppOpenManager(Application myApplication) {
        this.myApplication = myApplication;
        this.myApplication.registerActivityLifecycleCallbacks(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    /**
     * Request an ad
     */
    public void fetchAd() {
        tinydb = new TinyDB(currentActivity.getApplicationContext());

        if (isAdAvailable()) {
            return;
        }

        loadCallback = new AppOpenAd.AppOpenAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull AppOpenAd appOpenAd) {
                super.onAdLoaded(appOpenAd);
                AppOpenManager.this.appOpenAd = appOpenAd;
//                Log.e("fetchAd", "onAdLoaded:AppOpen_ad ");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
//                Log.e("fetchAd", "onAdFailedToLoad: ");
            }
        };

        AdRequest request = getAdRequest();
        AppOpenAd.load(myApplication, Objects.requireNonNull(tinydb.getString("appOpen_ad")), request, AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback);
//        Log.e("fetchAd", "fetchAd:appOpen_ad_ID:: "+tinydb.getString("appOpen_ad") );
    }

    /**
     * Shows the ad if one isn't already showing.
     */
    public void showAdIfAvailable() {
        // Only show ad if there is not already an app open ad currently showing
        // and an ad is available.
        if (!isShowingAd && isAdAvailable()) {
//            Log.d(LOG_TAG, "Will show ad.");

            FullScreenContentCallback fullScreenContentCallback =
                    new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            // Set the reference to null so isAdAvailable() returns false.
                            AppOpenManager.this.appOpenAd = null;
                            isShowingAd = false;
//                            fetchAd();
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
//                            Log.e("TAG", "onAdFailedToShowFullScreenContent:appopen_ad "+adError );
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            isShowingAd = true;
                        }
                    };

            /*if (!(this.currentActivity instanceof AppstartActivity)) {
                appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
                appOpenAd.show(currentActivity);
            }*/



        } else {
//            Log.d(LOG_TAG, "Can not show ad.");
            fetchAd();
        }
    }

    /**
     * Creates and returns ad request.
     */
    private AdRequest getAdRequest() {
        return new AdRequest.Builder().build();
    }

    /**
     * Utility method that checks if ad exists and can be shown.
     */
    public boolean isAdAvailable() {
        return appOpenAd != null;
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        currentActivity = activity;

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        currentActivity = activity;

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        currentActivity = null;

    }

    /**
     * LifecycleObserver methods
     */
    @OnLifecycleEvent(ON_START)
    public void onStart() {
        showAdIfAvailable();
//        Log.d(LOG_TAG, "onStart");
    }
}
