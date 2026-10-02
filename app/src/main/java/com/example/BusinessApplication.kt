package com.example

import android.app.Application
import com.example.data.local.AppDatabase
import com.example.data.repository.BusinessRepository
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class BusinessApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    val repository: BusinessRepository by lazy { BusinessRepository(database.businessDao()) }

    override fun onCreate() {
        super.onCreate()
        // Initialize Google Mobile Ads asynchronously
        applicationScope.launch(Dispatchers.IO) {
            try {
                MobileAds.initialize(this@BusinessApplication) {}
            } catch (e: Throwable) {
                // Graceful fallback if Play Services or Ads SDK has an issue
                e.printStackTrace()
            }
        }
    }
}
