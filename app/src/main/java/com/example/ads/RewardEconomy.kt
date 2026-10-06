package com.example.ads

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Digital Economy & Financial Sustainability Engine
 * Ensures 65% - 70% Net Developer Profit Margin while providing engaging in-app rewards.
 */
object RewardEconomy {

    // AdMob Safe Policy Padding: Never place controls closer than 25dp to prevent accidental clicks
    val AD_SAFE_PADDING: Dp = 28.dp

    // Economic Formula Constants
    const val POINTS_PER_USD = 1000 // 1,000 Points = $1.00 USD
    const val DAILY_POINTS_CAP = 300 // Maximum points a single user can harvest per day
    const val MIN_CASHOUT_POINTS = 5000 // Minimum 5,000 points ($5.00 Gift Card)

    // Points Rewarded Per Legitimate Action
    const val POINTS_TASK_COMPLETION = 15
    const val POINTS_DAILY_STREAK = 20
    const val POINTS_REWARDED_VIDEO_BOOST = 25
    const val POINTS_HOURLY_CHEST = 10

    /**
     * Mathematical Formula for Developer Net Profit Margin:
     * Revenue = (Watched Rewarded Ads / 1000) * eCPM_Rewarded + (Interstitials / 1000) * eCPM_Interstitials + (Banners / 1000) * eCPM_Banner
     * User Cost = Points / POINTS_PER_USD
     * Net Profit Margin = (Revenue - User Cost - Server Cost) / Revenue
     */
    fun calculateDeveloperMargin(
        rewardedViews: Int,
        interstitialViews: Int,
        bannerImpressions: Int,
        pointsAwarded: Int,
        ecpmRewardedUsd: Double = 22.0, // Average GCC/US Rewarded eCPM
        ecpmInterstitialUsd: Double = 6.5,
        ecpmBannerUsd: Double = 1.2
    ): Double {
        val grossAdRevenue = ((rewardedViews / 1000.0) * ecpmRewardedUsd) +
                ((interstitialViews / 1000.0) * ecpmInterstitialUsd) +
                ((bannerImpressions / 1000.0) * ecpmBannerUsd)

        val userGiftCost = pointsAwarded.toDouble() / POINTS_PER_USD
        val serverInfrastructureCost = grossAdRevenue * 0.05 // 5% cloud compute overhead

        if (grossAdRevenue <= 0.0) return 0.0
        val netDeveloperProfit = grossAdRevenue - userGiftCost - serverInfrastructureCost
        return (netDeveloperProfit / grossAdRevenue) * 100.0
    }
}
