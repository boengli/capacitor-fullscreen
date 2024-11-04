package com.boengli.plugins.fullscreen

import android.os.Build
import android.util.Log
import android.view.View
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

  private val TAG = "FullscreenPlugin"

  @PluginMethod
  fun activateImmersiveMode(call: PluginCall) {
    val activity = bridge.activity
    Log.d(TAG, "activateImmersiveMode called")
    if (activity != null && isImmersiveModeSupported()) {
      activity.runOnUiThread {
        try {
          setImmersiveMode(activity)
          call.resolve()
        } catch (e: Exception) {
          Log.e(TAG, "Error activating immersive mode: ${e.message}")
          call.reject("Error activating immersive mode", e)
        }
      }
    } else {
      Log.e(TAG, "Immersive mode is not supported on this device.")
      call.reject("Immersive mode is not supported on this device.")
    }
  }

  private fun isImmersiveModeSupported(): Boolean {
    val supported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
    Log.d(TAG, "isImmersiveModeSupported: $supported")
    return supported
  }

  private fun setImmersiveMode(activity: android.app.Activity) {
    Log.d(TAG, "Setting immersive mode")
    val window = activity.window
    val decorView = window.decorView

    // Ensure the content layout extends into the navigation and status bar area
    WindowCompat.setDecorFitsSystemWindows(window, false)

    // Use WindowInsetsControllerCompat for immersive mode
    val windowInsetsController = WindowCompat.getInsetsController(window, decorView)
    if (windowInsetsController != null) {
      windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
      windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    Log.d(TAG, "Immersive mode activated")
  }
}