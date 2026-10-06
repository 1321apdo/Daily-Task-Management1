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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BusinessApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    val repository: BusinessRepository by lazy { BusinessRepository(database.businessDao()) }
    val privacyManager: PrivacyManager by lazy { PrivacyManager.getInstance(this) }

    override fun onCreate() {
        super.onCreate()

        // Global crash guard to ensure the app never hard-crashes silently
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            android.util.Log.e("BusinessApp", "Caught uncaught exception on thread ${thread.name}", throwable)
            defaultHandler?.uncaughtException(thread, throwable)
        }

        // Initialize persistent user rewards and points system
        try {
            AdMobManager.initialize(this)
        } catch (_: Throwable) {}

        // Initialize background notification channel safely
        try {
            com.example.notifications.BackgroundReminderManager.createNotificationChannel(this)
        } catch (_: Throwable) {}

        // Initialize Google Mobile Ads for 100% Live Commercial Ads on physical devices
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            try {
                if (!AdMobManager.isRunningOnEmulator()) {
                    MobileAds.initialize(this@BusinessApplication) {}
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }
}
