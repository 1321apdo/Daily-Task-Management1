package com.example.ui.rewards

import android.app.Activity
import com.example.ads.AdMobManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class RewardGameState(
    val points: Int = 120,
    val streak: Int = 1,
    val multiplier: Double = 1.0,
    val totalTaps: Int = 15,
    val chestsOpened: Int = 1,
    val nextChestTapsRemaining: Int = 10,
    val lastEarnedAmount: Int = 5,
    val levelName: String = "رائد أعمال صاعد 🥉"
)

object RewardGameManager {
    private val _state = MutableStateFlow(RewardGameState())
    val state: StateFlow<RewardGameState> = _state.asStateFlow()

    fun tapToEarn(activity: Activity? = null) {
        _state.update { current ->
            val pointsEarned = (10 * current.multiplier).toInt()
            val newTotalTaps = current.totalTaps + 1
            val newPoints = current.points + pointsEarned
            val newStreak = current.streak + 1

            val newMultiplier = when {
                newStreak >= 30 -> 3.5
                newStreak >= 20 -> 2.5
                newStreak >= 10 -> 2.0
                newStreak >= 5 -> 1.5
                else -> 1.0
            }

            val newLevel = when {
                newPoints >= 2500 -> "ملياردير أعمال 💎"
                newPoints >= 1000 -> "مدير تنفيذي 🥇"
                newPoints >= 500 -> "محترف أعمال 🥈"
                else -> "رائد أعمال صاعد 🥉"
            }

            val remainingChest = if (current.nextChestTapsRemaining <= 1) 15 else current.nextChestTapsRemaining - 1

            current.copy(
                points = newPoints,
                streak = newStreak,
                multiplier = newMultiplier,
                totalTaps = newTotalTaps,
                lastEarnedAmount = pointsEarned,
                nextChestTapsRemaining = remainingChest,
                levelName = newLevel
            )
        }

        // Trigger AdMob every 5 taps milestone to maximize legal revenue!
        if (_state.value.totalTaps % 5 == 0 && activity != null) {
            AdMobManager.showInterstitialAd(activity)
        }
    }

    fun openMysteryChest(activity: Activity? = null) {
        val chestBonus = 250
        _state.update { current ->
            current.copy(
                points = current.points + chestBonus,
                chestsOpened = current.chestsOpened + 1,
                nextChestTapsRemaining = 20,
                lastEarnedAmount = chestBonus
            )
        }
        activity?.let { AdMobManager.showInterstitialAd(it, force = true) }
    }

    fun watchAdForBonus(activity: Activity) {
        AdMobManager.showInterstitialAd(activity, force = true) {
            _state.update { current ->
                val bonus = 500
                current.copy(
                    points = current.points + bonus,
                    multiplier = 3.0,
                    lastEarnedAmount = bonus
                )
            }
        }
    }
}
