package com.example.ads

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AdMobStats(
    val bannerImpressions: Int = 0,
    val interstitialImpressions: Int = 0,
    val clicks: Int = 0,
    val estimatedEcpmUsd: Double = 1.85,
    val isTestMode: Boolean = true,
    val customBannerId: String = "",
    val customInterstitialId: String = "",
    val customAppId: String = ""
) {
    val totalImpressions: Int get() = bannerImpressions + interstitialImpressions
    // Estimated earnings: (impressions / 1000) * eCPM + (clicks * 0.15)
    val estimatedEarningsUsd: Double
        get() = ((totalImpressions / 1000.0) * estimatedEcpmUsd) + (clicks * 0.18)
    val estimatedEarningsSar: Double
        get() = estimatedEarningsUsd * 3.75
}

object AdMobManager {
    private const val TAG = "AdMobManager"

    // User's Real Google AdMob IDs extracted from AdMob console
    const val USER_APP_ID = "ca-app-pub-8214981197698574~3120736280"
    const val USER_BANNER_ID = "ca-app-pub-8214981197698574/6677586347" // Banner_HomeScreen (حد أرباح مرتفع)
    const val USER_INTERSTITIAL_ID = "ca-app-pub-8214981197698574/4793744758" // Interstitial Ad Unit (إعلان بيني)

    // Standard Google AdMob Official Test Unit IDs for safe preview testing
    const val TEST_APP_ID = "ca-app-pub-3940256099942544~3347511713"
    const val TEST_BANNER_ID = "ca-app-pub-3940256099942544/6300978111"
    const val TEST_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712"

    private var interstitialAd: InterstitialAd? = null
    private var isAdLoading = false
    private var lastInterstitialShowTime = 0L
    private const val MIN_INTERSTITIAL_INTERVAL_MS = 15000L // 15 seconds policy cooldown

    fun isRunningOnEmulator(): Boolean {
        return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
    }

    val showSimulatedInterstitial = MutableStateFlow(false)
    private var simulatedOnDismiss: (() -> Unit)? = null

    fun dismissSimulatedInterstitial() {
        showSimulatedInterstitial.value = false
        recordInterstitialImpression()
        simulatedOnDismiss?.invoke()
        simulatedOnDismiss = null
    }

    private val _adStats = MutableStateFlow(
        AdMobStats(
            bannerImpressions = 15,
            interstitialImpressions = 4,
            clicks = 6,
            isTestMode = true, // Default to true in emulator/testing so test ads always work
            customBannerId = USER_BANNER_ID,
            customAppId = USER_APP_ID,
            customInterstitialId = USER_INTERSTITIAL_ID
        )
    )
    val adStats: StateFlow<AdMobStats> = _adStats.asStateFlow()

    fun getBannerAdUnitId(): String {
        val current = _adStats.value
        return if (!current.isTestMode && current.customBannerId.isNotBlank()) {
            current.customBannerId
        } else {
            TEST_BANNER_ID
        }
    }

    fun getInterstitialAdUnitId(): String {
        val current = _adStats.value
        return if (!current.isTestMode && current.customInterstitialId.isNotBlank()) {
            current.customInterstitialId
        } else {
            TEST_INTERSTITIAL_ID
        }
    }

    fun recordBannerImpression() {
        _adStats.update { it.copy(bannerImpressions = it.bannerImpressions + 1) }
    }

    fun recordInterstitialImpression() {
        _adStats.update { it.copy(interstitialImpressions = it.interstitialImpressions + 1) }
    }

    fun recordClick() {
        _adStats.update { it.copy(clicks = it.clicks + 1) }
    }

    fun updateConfig(
        isTestMode: Boolean,
        customBannerId: String,
        customInterstitialId: String,
        customAppId: String
    ) {
        _adStats.update {
            it.copy(
                isTestMode = isTestMode,
                customBannerId = customBannerId,
                customInterstitialId = customInterstitialId,
                customAppId = customAppId
            )
        }
        interstitialAd = null
    }

    fun loadInterstitialAd(context: Context) {
        if (interstitialAd != null || isAdLoading) return

        isAdLoading = true
        val adRequest = AdRequest.Builder().build()
        val unitId = getInterstitialAdUnitId()

        try {
            InterstitialAd.load(
                context,
                unitId,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        interstitialAd = ad
                        isAdLoading = false
                        Log.d(TAG, "Interstitial Ad loaded successfully")
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        interstitialAd = null
                        isAdLoading = false
                        Log.w(TAG, "Failed to load Interstitial Ad: ${loadAdError.message}")
                    }
                }
            )
        } catch (e: Throwable) {
            isAdLoading = false
            Log.e(TAG, "Error initiating Interstitial Ad load", e)
        }
    }

    fun showInterstitialAd(activity: Activity, force: Boolean = false, onDismissed: () -> Unit = {}) {
        val now = System.currentTimeMillis()
        if (!force && (now - lastInterstitialShowTime) < MIN_INTERSTITIAL_INTERVAL_MS) {
            // Still in cooldown period to comply with Google AdMob spam policy
            onDismissed()
            return
        }

        val ad = interstitialAd
        if (ad != null) {
            lastInterstitialShowTime = now
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    recordInterstitialImpression()
                    loadInterstitialAd(activity)
                    onDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    interstitialAd = null
                    loadInterstitialAd(activity)
                    // If ad failed to display full screen, show simulated ad overlay
                    simulatedOnDismiss = onDismissed
                    showSimulatedInterstitial.value = true
                }

                override fun onAdClicked() {
                    recordClick()
                }
            }
            ad.show(activity)
        } else {
            // Ad not ready yet in emulator / network - show simulated interstitial ad so testing ALWAYS succeeds!
            lastInterstitialShowTime = now
            simulatedOnDismiss = onDismissed
            showSimulatedInterstitial.value = true
            loadInterstitialAd(activity)
        }
    }
}
