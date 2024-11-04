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
import java.util.concurrent.atomic.AtomicBoolean

@CapacitorPlugin(name = "Fullscreen")
class Fullscreen : Plugin() {

  private val TAG = "FullscreenPlugin"
  private val isVisibilityBeingSet = AtomicBoolean(false)

  @PluginMethod
  fun activateImmersiveMode(call: PluginCall) {
    val activity = bridge.activity
    Log.d(TAG, "activateImmersiveMode called")
    if (activity != null && isImmersiveModeSupported()) {
      activity.runOnUiThread {
        try {
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

    // Use system UI visibility flags for immersive mode
    decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            )

    // Ensure content extends into system bars
    WindowCompat.setDecorFitsSystemWindows(window, false)

    Log.d(TAG, "System bars hidden and layout extended")
  }

  private fun showSystemBars(activity: android.app.Activity) {
    Log.d(TAG, "Showing system bars")
    val window = activity.window
    val decorView = window.decorView

    // Clear immersive mode flags to show system bars
    decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE

    // Reset decor fits system windows
    WindowCompat.setDecorFitsSystemWindows(window, true)

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
          if (!isVisibilityBeingSet.get()) {
            isVisibilityBeingSet.set(true)
            hideSystemBars(activity)
            isVisibilityBeingSet.set(false)
          }
        }
      }
      insets
    }
  }
}