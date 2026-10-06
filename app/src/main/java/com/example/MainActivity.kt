package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.ads.AdMobManager
import com.example.ui.BusinessViewModel
import com.example.ui.MainScreen
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: BusinessViewModel by viewModels {
        val app = application as BusinessApplication
        BusinessViewModel.Factory(app.repository, app.privacyManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreen(viewModel = viewModel)
                }
            }
        }

        // Request notification permission smoothly after UI has already rendered
        lifecycleScope.launch {
            try {
                delay(1500)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
                    }
                }
            } catch (_: Throwable) {}
        }

        // Delay ad preloading so the initial user interface opens instantly with 0 freeze
        lifecycleScope.launch {
            try {
                delay(2000)
                if (!AdMobManager.isRunningOnEmulator()) {
                    AdMobManager.loadInterstitialAd(this@MainActivity)
                    AdMobManager.loadRewardedAd(this@MainActivity)
                }
            } catch (_: Throwable) {
                // Ignore safe ad loading error
            }
        }
    }
}
