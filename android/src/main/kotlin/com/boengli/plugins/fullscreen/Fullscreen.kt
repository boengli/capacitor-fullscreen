package com.boengli.plugins.fullscreen

import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
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
            activity.runOnUiThread {
                hideSystemBars(activity)
                setupVisibilityListeners(activity)
                call.resolve()
            }
        } else {
            call.reject("Immersive mode is not supported on this device.")
        }
    }

    @PluginMethod
    fun deactivateImmersiveMode(call: PluginCall) {
        val activity = bridge.activity
        if (activity != null && isImmersiveModeSupported()) {
            activity.runOnUiThread {
                showSystemBars(activity)
                call.resolve()
            }
        } else {
            call.reject("Cannot deactivate immersive mode.")
        }
    }

    private fun isImmersiveModeSupported(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
    }

    private fun hideSystemBars(activity: android.app.Activity) {
        val window = activity.window
        val decorView = window.decorView

        // Ensure that we do not fit system windows
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Use WindowInsetsControllerCompat for better compatibility
        val controller = WindowInsetsControllerCompat(window, decorView)

        // Hide the system bars
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    private fun showSystemBars(activity: android.app.Activity) {
        val window = activity.window
        val decorView = window.decorView

        // Reset to fit system windows
        WindowCompat.setDecorFitsSystemWindows(window, true)

        val controller = WindowInsetsControllerCompat(window, decorView)

        // Show the system bars
        controller.show(WindowInsetsCompat.Type.systemBars())
    }

    private fun setupVisibilityListeners(activity: android.app.Activity) {
        val decorView = activity.window.decorView

        // Listener to re-hide system bars when they reappear
        ViewCompat.setOnApplyWindowInsetsListener(decorView) { _, insets ->
            val isVisible = insets.isVisible(WindowInsetsCompat.Type.systemBars())
            if (isVisible) {
                activity.runOnUiThread {
                    hideSystemBars(activity)
                }
            }
            insets
        }
    }
}