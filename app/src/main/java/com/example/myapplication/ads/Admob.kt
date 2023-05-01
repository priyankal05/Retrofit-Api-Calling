package com.example.myapplication.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class Admob {

    companion object {

        var sInterstitial: InterstitialAd? = null
        var sInterstitiaSplash: InterstitialAd? = null


        fun loadInter(context: Activity?, id: String) {
            MobileAds.initialize(context!!)
            Log.e("inter", "loadInter fun")

            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(context, id, adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        sInterstitial = interstitialAd
                        Log.e("inter", "onAdLoaded interstitial")
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        // Handle the error
                        Log.e("inter", "inter failed " + loadAdError.toString() )
                        sInterstitial = null
                    }
                })
        }

        fun loadInterSplash(context: Context?, id: String) {
            MobileAds.initialize(context!!)

            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(context, id, adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        sInterstitiaSplash = interstitialAd
                        Log.e("TAG", "onAdLoaded interstitial")
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        // Handle the error
                        Log.e("TAG", "inter splash " + loadAdError.toString() )
                        sInterstitiaSplash = null
                    }
                })
        }

    }



}