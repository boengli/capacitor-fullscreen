package com.boengli.plugins.fullscreen

import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.core.view.ViewCompat
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
            hideSystemBars(activity)
            setupVisibilityListeners(activity)
            call.resolve()
        } else {
            call.reject("Immersive mode is not supported on this device.")
        }
    }

    @PluginMethod
    fun deactivateImmersiveMode(call: PluginCall) {
        call.resolve()
    }

    private fun isImmersiveModeSupported(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
    }

    private fun hideSystemBars(activity: android.app.Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Use the new API for Android 11+
            activity.window.insetsController?.let {
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                it.hide(WindowInsets.Type.systemBars())
            }
        } else {
            // Use the old API for below Android 11
            val windowInsetsController = ViewCompat.getWindowInsetsController(activity.window.decorView)
            windowInsetsController?.let {
                it.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                it.hide(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    private fun setupVisibilityListeners(activity: android.app.Activity) {
        val decorView = activity.window.decorView

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Newer method not requiring deprecated flags on Android 11+
            decorView.setOnApplyWindowInsetsListener { view, insets ->
                if (insets.isVisible(WindowInsets.Type.systemBars())) {
                    hideSystemBars(activity)
                }
                insets
            }
        } else {
            // For older Android versions, use the deprecated API
            decorView.setOnSystemUiVisibilityChangeListener { visibility ->
                if ((visibility and View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    hideSystemBars(activity)
                }
            }

            decorView.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) hideSystemBars(activity)
            }
        }
    }
}