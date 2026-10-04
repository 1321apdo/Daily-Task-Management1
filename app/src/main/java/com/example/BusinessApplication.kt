package com.example

import android.app.Application
import android.os.Build
import android.webkit.WebView
import com.example.ads.AdMobManager
import com.example.data.local.AppDatabase
import com.example.data.repository.BusinessRepository
import com.example.security.PrivacyManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration

class BusinessApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    val repository: BusinessRepository by lazy { BusinessRepository(database.businessDao()) }
    val privacyManager: PrivacyManager by lazy { PrivacyManager.getInstance(this) }

    override fun onCreate() {
        super.onCreate()

        // Safely configure WebView for multi-process isolation on Android P+
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val currentProcessName = Application.getProcessName()
                if (packageName != currentProcessName) {
                    WebView.setDataDirectorySuffix(currentProcessName)
                }
            }
        } catch (_: Throwable) {}

        // Initialize background notification channel
        try {
            com.example.notifications.BackgroundReminderManager.createNotificationChannel(this)
        } catch (_: Throwable) {}

        // Initialize Google Mobile Ads safely on real devices without blocking UI
        try {
            val requestConfig = RequestConfiguration.Builder()
                .setTestDeviceIds(listOf(AdRequest.DEVICE_ID_EMULATOR))
                .build()
            MobileAds.setRequestConfiguration(requestConfig)

            if (!AdMobManager.isRunningOnEmulator()) {
                MobileAds.initialize(this) {}
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}
