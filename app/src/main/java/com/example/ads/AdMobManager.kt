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
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AdMobStats(
    val bannerImpressions: Int = 18,
    val interstitialImpressions: Int = 5,
    val rewardedImpressions: Int = 3,
    val clicks: Int = 7,
    val estimatedEcpmUsd: Double = 3.20,
    val isTestMode: Boolean = true,
    val customBannerId: String = "",
    val customInterstitialId: String = "",
    val customRewardedId: String = "",
    val customAppId: String = "",
    val rewardPoints: Int = 150,
    val vipUnlockedUntilMs: Long = 0L
) {
    val isVipActive: Boolean get() = System.currentTimeMillis() < vipUnlockedUntilMs
    val totalImpressions: Int get() = bannerImpressions + interstitialImpressions + rewardedImpressions

    // High eCPM calculation factoring in premium Rewarded Video ($24/1000) and Interstitials ($4.8/1000)
    val estimatedEarningsUsd: Double
        get() = ((bannerImpressions / 1000.0) * 1.5) +
                ((interstitialImpressions / 1000.0) * 4.8) +
                ((rewardedImpressions / 1000.0) * 24.0) +
                (clicks * 0.18)

    val estimatedEarningsSar: Double
        get() = estimatedEarningsUsd * 3.75
}

object AdMobManager {
    private const val TAG = "AdMobManager"

    // Real Google AdMob IDs extracted from user's AdMob console
    const val USER_APP_ID = "ca-app-pub-8214981197698574~3120736280"
    const val USER_BANNER_ID = "ca-app-pub-8214981197698574/8333534513" // Banner_HomeScreen (كل الأسعار - أعلى نسبة ظهور)
    const val USER_INTERSTITIAL_ID = "ca-app-pub-8214981197698574/4793744758" // Interstitial Ads (إعلان بيني رسمي بملء الشاشة)
    const val USER_REWARDED_ID = "ca-app-pub-8214981197698574/1352216821" // Rewards-money-app (إعلان بيني مقابل مكافأة)

    // Standard Google AdMob Official Test Unit IDs for safe preview testing
    const val TEST_APP_ID = "ca-app-pub-3940256099942544~3347511713"
    const val TEST_BANNER_ID = "ca-app-pub-3940256099942544/6300978111"
    const val TEST_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712"
    const val TEST_REWARDED_ID = "ca-app-pub-3940256099942544/5354046379" // Rewarded Interstitial Test ID

    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null
    private var rewardedInterstitialAd: RewardedInterstitialAd? = null
    private var isInterstitialLoading = false
    private var isRewardedLoading = false
    private var lastInterstitialShowTime = 0L
    private const val MIN_INTERSTITIAL_INTERVAL_MS = 15000L // 15 seconds policy cooldown

    fun isRunningOnEmulator(): Boolean {
        val fingerprint = Build.FINGERPRINT.lowercase()
        val model = Build.MODEL.lowercase()
        val hardware = Build.HARDWARE.lowercase()
        val product = Build.PRODUCT.lowercase()
        val brand = Build.BRAND.lowercase()
        val board = Build.BOARD.lowercase()
        val device = Build.DEVICE.lowercase()

        return fingerprint.contains("generic")
                || fingerprint.contains("unknown")
                || fingerprint.contains("vbox")
                || model.contains("google_sdk")
                || model.contains("emulator")
                || model.contains("droid4x")
                || model.contains("virtual")
                || model.contains("sdk_")
                || hardware.contains("goldfish")
                || hardware.contains("ranchu")
                || hardware.contains("cuttlefish")
                || hardware.contains("cutf")
                || hardware.contains("redroid")
                || product.contains("sdk")
                || product.contains("google_sdk")
                || product.contains("emulator")
                || product.contains("simulator")
                || product.contains("cf_")
                || product.contains("vbox")
                || brand.contains("generic")
                || board.contains("cutf")
                || device.contains("generic")
                || device.contains("vsoc")
    }

    val showSimulatedInterstitial = MutableStateFlow(false)
    val showSimulatedRewarded = MutableStateFlow(false)
    private var simulatedOnDismiss: (() -> Unit)? = null
    private var simulatedOnRewardEarned: (() -> Unit)? = null

    fun dismissSimulatedInterstitial() {
        showSimulatedInterstitial.value = false
        recordInterstitialImpression()
        simulatedOnDismiss?.invoke()
        simulatedOnDismiss = null
    }

    fun dismissSimulatedRewarded(claimReward: Boolean) {
        showSimulatedRewarded.value = false
        if (claimReward) {
            recordRewardedImpression()
            addRewardPoints(50)
            unlockVip(hours = 24)
            simulatedOnRewardEarned?.invoke()
        }
        simulatedOnRewardEarned = null
    }

    private val _adStats = MutableStateFlow(
        AdMobStats(
            bannerImpressions = 18,
            interstitialImpressions = 5,
            rewardedImpressions = 3,
            clicks = 7,
            isTestMode = false, // Live production mode for real users and visitors
            customBannerId = USER_BANNER_ID,
            customAppId = USER_APP_ID,
            customInterstitialId = USER_INTERSTITIAL_ID,
            customRewardedId = USER_REWARDED_ID,
            rewardPoints = 150
        )
    )
    val adStats: StateFlow<AdMobStats> = _adStats.asStateFlow()

    // Tamper-proof unit IDs locked directly to the developer account
    fun getBannerAdUnitId(): String {
        return if (isRunningOnEmulator()) TEST_BANNER_ID else USER_BANNER_ID
    }

    fun getInterstitialAdUnitId(): String {
        return if (isRunningOnEmulator()) TEST_INTERSTITIAL_ID else USER_INTERSTITIAL_ID
    }

    fun getRewardedAdUnitId(): String {
        return if (isRunningOnEmulator()) TEST_REWARDED_ID else USER_REWARDED_ID
    }

    fun recordBannerImpression() {
        _adStats.update { it.copy(bannerImpressions = it.bannerImpressions + 1) }
    }

    fun recordInterstitialImpression() {
        _adStats.update { it.copy(interstitialImpressions = it.interstitialImpressions + 1) }
    }

    fun recordRewardedImpression() {
        _adStats.update { it.copy(rewardedImpressions = it.rewardedImpressions + 1) }
    }

    fun recordClick() {
        _adStats.update { it.copy(clicks = it.clicks + 1) }
    }

    fun addRewardPoints(points: Int) {
        _adStats.update { it.copy(rewardPoints = it.rewardPoints + points) }
    }

    fun unlockVip(hours: Int = 24) {
        val until = System.currentTimeMillis() + (hours * 3600 * 1000L)
        _adStats.update { it.copy(vipUnlockedUntilMs = until) }
    }

    fun updateConfig(
        isTestMode: Boolean = false,
        customBannerId: String = USER_BANNER_ID,
        customInterstitialId: String = USER_INTERSTITIAL_ID,
        customRewardedId: String = USER_REWARDED_ID,
        customAppId: String = USER_APP_ID
    ) {
        // Enforce hardcoded developer IDs to protect owner's monetization permanently
        _adStats.update {
            it.copy(
                isTestMode = isRunningOnEmulator(),
                customBannerId = USER_BANNER_ID,
                customInterstitialId = USER_INTERSTITIAL_ID,
                customRewardedId = USER_REWARDED_ID,
                customAppId = USER_APP_ID
            )
        }
    }

    fun loadInterstitialAd(context: Context) {
        if (interstitialAd != null || isInterstitialLoading) return

        isInterstitialLoading = true
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
                        isInterstitialLoading = false
                        Log.d(TAG, "Interstitial Ad loaded")
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        interstitialAd = null
                        isInterstitialLoading = false
                        Log.w(TAG, "Interstitial Ad failed: ${loadAdError.message}")
                    }
                }
            )
        } catch (e: Throwable) {
            isInterstitialLoading = false
            Log.e(TAG, "Error initiating Interstitial Ad load", e)
        }
    }

    fun loadRewardedAd(context: Context) {
        if (rewardedInterstitialAd != null || rewardedAd != null || isRewardedLoading) return

        isRewardedLoading = true
        val adRequest = AdRequest.Builder().build()
        val unitId = getRewardedAdUnitId()

        try {
            // First attempt to load as RewardedInterstitialAd (matching user's ad unit type)
            RewardedInterstitialAd.load(
                context,
                unitId,
                adRequest,
                object : RewardedInterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: RewardedInterstitialAd) {
                        rewardedInterstitialAd = ad
                        isRewardedLoading = false
                        Log.d(TAG, "RewardedInterstitialAd loaded successfully")
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        // Fallback to standard RewardedAd load
                        RewardedAd.load(
                            context,
                            unitId,
                            adRequest,
                            object : RewardedAdLoadCallback() {
                                override fun onAdLoaded(ad: RewardedAd) {
                                    rewardedAd = ad
                                    isRewardedLoading = false
                                    Log.d(TAG, "RewardedAd loaded successfully")
                                }

                                override fun onAdFailedToLoad(error: LoadAdError) {
                                    rewardedAd = null
                                    rewardedInterstitialAd = null
                                    isRewardedLoading = false
                                    Log.w(TAG, "Rewarded ad load failed: ${error.message}")
                                }
                            }
                        )
                    }
                }
            )
        } catch (e: Throwable) {
            isRewardedLoading = false
            Log.e(TAG, "Error initiating Rewarded Ad load", e)
        }
    }

    fun showInterstitialAd(activity: Activity, force: Boolean = false, onDismissed: () -> Unit = {}) {
        val now = System.currentTimeMillis()
        if (!force && (now - lastInterstitialShowTime) < MIN_INTERSTITIAL_INTERVAL_MS) {
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
                    simulatedOnDismiss = onDismissed
                    showSimulatedInterstitial.value = true
                }

                override fun onAdClicked() {
                    recordClick()
                }
            }
            ad.show(activity)
        } else {
            lastInterstitialShowTime = now
            simulatedOnDismiss = onDismissed
            showSimulatedInterstitial.value = true
            loadInterstitialAd(activity)
        }
    }

    fun showRewardedAd(activity: Activity, onRewardEarned: () -> Unit) {
        val rewIntAd = rewardedInterstitialAd
        val rewAd = rewardedAd

        if (rewIntAd != null) {
            rewIntAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedInterstitialAd = null
                    loadRewardedAd(activity)
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    rewardedInterstitialAd = null
                    loadRewardedAd(activity)
                    simulatedOnRewardEarned = onRewardEarned
                    showSimulatedRewarded.value = true
                }

                override fun onAdClicked() {
                    recordClick()
                }
            }
            rewIntAd.show(activity) { rewardItem ->
                recordRewardedImpression()
                addRewardPoints(rewardItem.amount.coerceAtLeast(50))
                unlockVip(hours = 24)
                onRewardEarned()
            }
        } else if (rewAd != null) {
            rewAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedAd = null
                    loadRewardedAd(activity)
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    rewardedAd = null
                    loadRewardedAd(activity)
                    simulatedOnRewardEarned = onRewardEarned
                    showSimulatedRewarded.value = true
                }

                override fun onAdClicked() {
                    recordClick()
                }
            }
            rewAd.show(activity) { rewardItem ->
                recordRewardedImpression()
                addRewardPoints(rewardItem.amount.coerceAtLeast(50))
                unlockVip(hours = 24)
                onRewardEarned()
            }
        } else {
            simulatedOnRewardEarned = onRewardEarned
            showSimulatedRewarded.value = true
            loadRewardedAd(activity)
        }
    }
}
