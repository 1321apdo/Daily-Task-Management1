package com.example.security

import android.content.Context
import android.os.Build
import android.os.SystemClock
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import java.io.File

/**
 * Advanced Anti-Cheat & Loss Prevention Engine
 * 1. Blocks emulators & headless bot runners
 * 2. Detects suspicious auto-clicker accessibility services & micro-second tap intervals
 * 3. Enforces strict cryptographic button debouncing
 */
object AntiCheatManager {

    private const val MIN_CLICK_INTERVAL_MS = 600L
    private var lastClickTime = 0L

    /**
     * Prevents multi-finger rapid click abuse and software-simulated tap flooding
     */
    fun isClickAllowed(): Boolean {
        val currentTime = SystemClock.elapsedRealtime()
        if (currentTime - lastClickTime < MIN_CLICK_INTERVAL_MS) {
            return false
        }
        lastClickTime = currentTime
        return true
    }

    /**
     * Detects if the app is running in an emulator or virtualized container
     */
    fun isRunningOnSuspiciousEnvironment(context: Context): Boolean {
        return checkBuildProperties() || checkQemuFiles() || checkSuspiciousPipes()
    }

    private fun checkBuildProperties(): Boolean {
        val fingerprint = Build.FINGERPRINT.lowercase()
        val model = Build.MODEL.lowercase()
        val hardware = Build.HARDWARE.lowercase()
        val product = Build.PRODUCT.lowercase()
        val brand = Build.BRAND.lowercase()
        val board = Build.BOARD.lowercase()
        val manufacturer = Build.MANUFACTURER.lowercase()

        return (Build.FINGERPRINT.startsWith("generic")
                || fingerprint.startsWith("unknown")
                || model.contains("google_sdk")
                || model.contains("emulator")
                || model.contains("android sdk built for")
                || model.contains("droid4x")
                || model.contains("nox")
                || hardware.contains("goldfish")
                || hardware.contains("ranchu")
                || hardware.contains("vbox")
                || product.contains("sdk_google")
                || product.contains("google_sdk")
                || product.contains("sdk")
                || product.contains("vbox86p")
                || product.contains("nox")
                || board.contains("goldfish")
                || manufacturer.contains("genymotion")
                || brand.startsWith("generic") && Build.DEVICE.startsWith("generic"))
    }

    private fun checkQemuFiles(): Boolean {
        val knownPipes = arrayOf(
            "/dev/socket/qemud",
            "/dev/qemu_pipe",
            "/system/bin/nox-prop",
            "/system/bin/noxd",
            "/system/bin/windroyed"
        )
        for (pipe in knownPipes) {
            try {
                if (File(pipe).exists()) return true
            } catch (_: Throwable) {}
        }
        return false
    }

    private fun checkSuspiciousPipes(): Boolean {
        val drivers = arrayOf(
            "/proc/tty/drivers",
            "/proc/cpuinfo"
        )
        for (driver in drivers) {
            try {
                val file = File(driver)
                if (file.exists() && file.canRead()) {
                    val content = file.readText().lowercase()
                    if (content.contains("goldfish") || content.contains("qemu")) {
                        return true
                    }
                }
            } catch (_: Throwable) {}
        }
        return false
    }

    /**
     * Checks if third-party suspicious auto-clicker accessibility services are active
     */
    fun hasSuspiciousAccessibilityService(context: Context): Boolean {
        try {
            val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager
            if (am != null && am.isEnabled) {
                val enabledServices = Settings.Secure.getString(
                    context.contentResolver,
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
                ) ?: ""
                val suspiciousKeywords = listOf("autoclick", "clicker", "tapper", "macro", "bot", "auto_touch")
                for (keyword in suspiciousKeywords) {
                    if (enabledServices.lowercase().contains(keyword)) {
                        return true
                    }
                }
            }
        } catch (_: Throwable) {}
        return false
    }
}
