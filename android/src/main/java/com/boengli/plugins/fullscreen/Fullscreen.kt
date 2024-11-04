package com.boengli.plugins.fullscreen

import android.os.Build
import android.util.Log
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

  @PluginMethod
  fun deactivateImmersiveMode(call: PluginCall) {
    val activity = bridge.activity
    Log.d(TAG, "deactivateImmersiveMode called")
    if (activity != null && isImmersiveModeSupported()) {
      activity.runOnUiThread {
        try {
          resetSystemBars(activity)
          call.resolve()
        } catch (e: Exception) {
          Log.e(TAG, "Error deactivating immersive mode: ${e.message}")
          call.reject("Error deactivating immersive mode", e)
        }
      }
    } else {
      Log.e(TAG, "Cannot deactivate immersive mode.")
      call.reject("Cannot deactivate immersive mode.")
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

    WindowCompat.setDecorFitsSystemWindows(window, false)

    val windowInsetsController = WindowCompat.getInsetsController(window, decorView)
    windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

    Log.d(TAG, "Immersive mode activated")
  }

  private fun resetSystemBars(activity: android.app.Activity) {
    Log.d(TAG, "Resetting system bars")
    val window = activity.window
    val decorView = window.decorView

    WindowCompat.setDecorFitsSystemWindows(window, true)

    val windowInsetsController = WindowCompat.getInsetsController(window, decorView)
    windowInsetsController.show(WindowInsetsCompat.Type.systemBars())

    Log.d(TAG, "System bars reset to visible")
  }
}