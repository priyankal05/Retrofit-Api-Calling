package com.example.myapplication.ads

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MediaAspectRatio
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import java.util.Timer
import java.util.TimerTask

object NativeAdSingleton {
    private const val TAG = "NativeAdSingleton"
    private var nativeAd: NativeAd? = null
    private val refreshInterval: Long = 20000 // 30 seconds in milliseconds
    private var timer: Timer? = null

    fun loadAd(context: Context, adUnitId: String) {
        /*if (nativeAd != null) {
            // Ad already loaded, no need to load again
            return
        }*/

        val videoOptions = VideoOptions.Builder()
            .setStartMuted(false)
            .build()

        val adLoader = AdLoader.Builder(context, adUnitId)
            .forNativeAd { ad ->
                nativeAd = ad
                Log.d(TAG, "Ad loaded successfully")
//                startRefreshTimer(context)
            }
            .withAdListener(object :  AdListener() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    Log.d(TAG, "Ad failed to load: $p0")
                }
            })
            .withNativeAdOptions(NativeAdOptions.Builder().setMediaAspectRatio(MediaAspectRatio.SQUARE).setVideoOptions(videoOptions).build())
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    fun getNativeAd(): NativeAd? {
        return nativeAd
    }

    fun startRefreshTimer(context: Context) {
//        Log.e(TAG, "timer startRefreshTimer: " )
        timer?.cancel()
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
//                Log.d(TAG, "run startRefreshTimer")
//                loadAd(context, AppstartActivity.Gnative)
            }
        }, refreshInterval)
    }

    fun stopRefreshTimer() {
//        Log.e(TAG, "timer  stopRefreshTimer: " )
//        timer?.cancel()
    }
}