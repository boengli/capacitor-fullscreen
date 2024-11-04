package com.boengli.plugins.fullscreen

import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin

@CapacitorPlugin(name = "Fullscreen")
class Fullscreen : Plugin() {

    @PluginMethod
    fun activateImmersiveMode(call: PluginCall) {
        val activity = bridge.activity
        if (activity != null && isImmersiveModeSupported()) {
            hideSystemBars(activity)
            setupVisibilityListeners(activity)
            call.resolve()
        } else {
            call.reject("Immersive mode is not supported on this device.")
        }
    }

    @PluginMethod
    fun deactivateImmersiveMode(call: PluginCall) {
        val activity = bridge.activity
        if (activity != null && isImmersiveModeSupported()) {
            showSystemBars(activity)
            call.resolve()
        } else {
            call.reject("Cannot deactivate immersive mode.")
        }
    }

    private fun isImmersiveModeSupported(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
    }

    private fun hideSystemBars(activity: android.app.Activity) {
        val window = activity.window

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11 (API level 30) and above
            WindowCompat.setDecorFitsSystemWindows(window, false)
            val controller = window.insetsController
            controller?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            // For Android versions below 11
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            )
        }
    }

    private fun showSystemBars(activity: android.app.Activity) {
        val window = activity.window

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11 (API level 30) and above
            WindowCompat.setDecorFitsSystemWindows(window, true)
            val controller = window.insetsController
            controller?.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        } else {
            // For Android versions below 11
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
    }

    private fun setupVisibilityListeners(activity: android.app.Activity) {
        val decorView = activity.window.decorView

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11 and above
            decorView.setOnApplyWindowInsetsListener { _, insets ->
                val isVisible = insets.isVisible(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                if (isVisible) {
                    hideSystemBars(activity)
                }
                insets
            }
        } else {
            // For Android versions below 11
            @Suppress("DEPRECATION")
            decorView.setOnSystemUiVisibilityChangeListener { visibility ->
                if ((visibility and View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    hideSystemBars(activity)
                }
            }
        }
    }
}