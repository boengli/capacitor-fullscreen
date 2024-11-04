package com.boengli.plugins.fullscreen

import android.os.Build
import android.util.Log
import android.view.WindowManager
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
          Log.d(TAG, "Activating immersive mode")
          hideSystemBars(activity)
          setupVisibilityListeners(activity)
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
          Log.d(TAG, "Deactivating immersive mode")
          showSystemBars(activity)
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

  private fun hideSystemBars(activity: android.app.Activity) {
    Log.d(TAG, "Hiding system bars and extending layout")
    val window = activity.window
    val decorView = window.decorView

    // Allow layout to extend into all screen areas
    window.setFlags(
      WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
      WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
    )

    // Ensure content extends into system bars
    WindowCompat.setDecorFitsSystemWindows(window, false)

    // Use WindowInsetsControllerCompat to hide system bars
    val controller = WindowInsetsControllerCompat(window, decorView)
    controller.hide(WindowInsetsCompat.Type.systemBars())
    controller.systemBarsBehavior =
      WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

    Log.d(TAG, "System bars hidden and layout extended")
  }

  private fun showSystemBars(activity: android.app.Activity) {
    Log.d(TAG, "Showing system bars")
    val window = activity.window
    val decorView = window.decorView

    // Remove the FLAG_LAYOUT_NO_LIMITS flag
    window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

    // Reset decor fits system windows
    WindowCompat.setDecorFitsSystemWindows(window, true)

    val controller = WindowInsetsControllerCompat(window, decorView)
    controller.show(WindowInsetsCompat.Type.systemBars())

    Log.d(TAG, "System bars shown")
  }

  private fun setupVisibilityListeners(activity: android.app.Activity) {
    Log.d(TAG, "Setting up visibility listeners")
    val decorView = activity.window.decorView

    // Listener to re-hide system bars when they reappear
    ViewCompat.setOnApplyWindowInsetsListener(decorView) { view: View, insets: WindowInsetsCompat ->
      val isVisible = insets.isVisible(WindowInsetsCompat.Type.systemBars())
      Log.d(TAG, "System bars visibility changed: $isVisible")
      if (isVisible) {
        activity.runOnUiThread {
          Log.d(TAG, "Re-hiding system bars")
          hideSystemBars(activity)
        }
      }
      insets
    }
  }
}
