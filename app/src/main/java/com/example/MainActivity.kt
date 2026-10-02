package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ads.AdMobManager
import com.example.ui.BusinessViewModel
import com.example.ui.MainScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: BusinessViewModel by viewModels {
        val app = application as BusinessApplication
        BusinessViewModel.Factory(app.repository)
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

        // Preload Interstitial Ad safely after first frame renders
        window.decorView.post {
            try {
                AdMobManager.loadInterstitialAd(this)
            } catch (e: Throwable) {
                // Ignore safe ad loading error
            }
        }
    }
}
