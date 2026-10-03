package com.example.security

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class PrivacySettings(
    val isPinEnabled: Boolean = false,
    val isDiscreetMode: Boolean = false, // Hides amounts and replaces with ••••
    val isNonPersonalizedAds: Boolean = true, // Protects against third-party ad profiling
    val isDataEncryptedLocally: Boolean = true,
    val isAppLocked: Boolean = false
)

class PrivacyManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_privacy_prefs", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(
        PrivacySettings(
            isPinEnabled = prefs.getBoolean(KEY_PIN_ENABLED, false),
            isDiscreetMode = prefs.getBoolean(KEY_DISCREET_MODE, false),
            isNonPersonalizedAds = prefs.getBoolean(KEY_NON_PERSONALIZED_ADS, true),
            isDataEncryptedLocally = true,
            isAppLocked = prefs.getBoolean(KEY_PIN_ENABLED, false)
        )
    )
    val settings: StateFlow<PrivacySettings> = _settings.asStateFlow()

    fun checkPin(pin: String): Boolean {
        val savedPin = prefs.getString(KEY_SAVED_PIN, "")
        if (pin == savedPin) {
            _settings.update { it.copy(isAppLocked = false) }
            return true
        }
        return false
    }

    fun setPin(pin: String) {
        prefs.edit()
            .putString(KEY_SAVED_PIN, pin)
            .putBoolean(KEY_PIN_ENABLED, true)
            .apply()
        _settings.update { it.copy(isPinEnabled = true, isAppLocked = false) }
    }

    fun removePin() {
        prefs.edit()
            .remove(KEY_SAVED_PIN)
            .putBoolean(KEY_PIN_ENABLED, false)
            .apply()
        _settings.update { it.copy(isPinEnabled = false, isAppLocked = false) }
    }

    fun lockApp() {
        if (_settings.value.isPinEnabled) {
            _settings.update { it.copy(isAppLocked = true) }
        }
    }

    fun toggleDiscreetMode() {
        val newValue = !_settings.value.isDiscreetMode
        prefs.edit().putBoolean(KEY_DISCREET_MODE, newValue).apply()
        _settings.update { it.copy(isDiscreetMode = newValue) }
    }

    fun setNonPersonalizedAds(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NON_PERSONALIZED_ADS, enabled).apply()
        _settings.update { it.copy(isNonPersonalizedAds = enabled) }
    }

    companion object {
        private const val KEY_PIN_ENABLED = "key_pin_enabled"
        private const val KEY_SAVED_PIN = "key_saved_pin"
        private const val KEY_DISCREET_MODE = "key_discreet_mode"
        private const val KEY_NON_PERSONALIZED_ADS = "key_non_personalized_ads"

        @Volatile
        private var INSTANCE: PrivacyManager? = null

        fun getInstance(context: Context): PrivacyManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PrivacyManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
