package com.boengli.plugins.fullscreen

import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.View
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin

@CapacitorPlugin(name = "Fullscreen")
class Fullscreen : Plugin() {

  companion object {
    const val TAG = "FullscreenPlugin"
  }

  private var isImmersiveModeActive: Boolean = false

  override fun load() {
    super.load()
    Log.d(TAG, "Fullscreen plugin loaded")

    // Automatically activate fullscreen when the plugin is loaded
    activateImmersiveModeInternal()
  }

  @PluginMethod
  fun activateImmersiveMode(call: PluginCall) {
    val activity = bridge.activity
    Log.d(TAG, "activateImmersiveMode called")
    if (activity != null && isImmersiveModeSupported()) {
      try {
        setImmersiveMode(activity)
        isImmersiveModeActive = true
        call.resolve()
      } catch (e: Exception) {
        Log.e(TAG, "Error activating immersive mode: ${e.message}")
        call.reject("Error activating immersive mode", e)
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
      try {
        resetSystemBars(activity)
        isImmersiveModeActive = false
        call.resolve()
      } catch (e: Exception) {
        Log.e(TAG, "Error deactivating immersive mode: ${e.message}")
        call.reject("Error deactivating immersive mode", e)
      }
    } else {
      Log.e(TAG, "Cannot deactivate immersive mode.")
      call.reject("Cannot deactivate immersive mode.")
    }
  }

  override fun handleOnResume() {
    super.handleOnResume()
    if (isImmersiveModeActive) {
      val activity = bridge.activity
      if (activity != null && isImmersiveModeSupported()) {
        try {
          setImmersiveMode(activity)
        } catch (e: Exception) {
          Log.e(TAG, "Error re-activating immersive mode on resume: ${e.message}")
        }
      }
    }
  }

  private fun isImmersiveModeSupported(): Boolean {
    // Implement any device-specific checks if necessary
    Log.d(TAG, "isImmersiveModeSupported: true")
    return true
  }

  private fun setImmersiveMode(activity: android.app.Activity) {
    Log.d(TAG, "Setting immersive mode")
    val window = activity.window
    val decorView = window.decorView

    // Allow content to extend into the system UI areas
    WindowCompat.setDecorFitsSystemWindows(window, false)

    // Set the status bar and navigation bar to transparent
    window.statusBarColor = Color.TRANSPARENT
    window.navigationBarColor = Color.TRANSPARENT

    val controller = WindowCompat.getInsetsController(window, decorView)
    controller.hide(WindowInsetsCompat.Type.systemBars())
    controller.systemBarsBehavior =
      WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

    // Add a focus change listener to reapply immersive mode when focus is regained
    decorView.setOnSystemUiVisibilityChangeListener { visibility ->
      if ((visibility and View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
        // System bars are visible; re-hide them
        setImmersiveMode(activity)
      }
    }

    Log.d(TAG, "Immersive mode activated")
  }

  private fun resetSystemBars(activity: android.app.Activity) {
    Log.d(TAG, "Resetting system bars")
    val window = activity.window
    val decorView = window.decorView

    WindowCompat.setDecorFitsSystemWindows(window, true)

    val controller = WindowCompat.getInsetsController(window, decorView)
    controller.show(WindowInsetsCompat.Type.systemBars())

    // Remove the system UI visibility change listener to prevent memory leaks
    decorView.setOnSystemUiVisibilityChangeListener(null)

    Log.d(TAG, "System bars reset to visible")
  }

  private fun activateImmersiveModeInternal() {
    val activity = bridge.activity
    if (activity != null && isImmersiveModeSupported()) {
      setImmersiveMode(activity)
      isImmersiveModeActive = true
      Log.d(TAG, "Fullscreen mode activated internally")
    }
  }
}