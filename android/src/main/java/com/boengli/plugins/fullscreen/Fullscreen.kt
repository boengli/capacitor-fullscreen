package com.boengli.plugins.fullscreen

import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.Looper
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

    // Enables FullScreen on plugin load, if you need to handle it in your code, comment out!
    preloadImmersiveModeResources()
  }

  @PluginMethod
  fun activateImmersiveMode(call: PluginCall) {
    val activity = bridge.activity
    Log.d(TAG, "activateImmersiveMode called")
    if (activity != null && isImmersiveModeSupported()) {
      val handler = Handler(Looper.getMainLooper())
      val timeout = 3000L // 3 seconds timeout

      var isCallHandled = false // Track whether the call has been resolved/rejected

      // Timeout mechanism to prevent ANR
      val runnable = Runnable {
          if (!isCallHandled) {
              Log.e(TAG, "Immersive mode activation timed out")
              call.reject("Immersive mode activation timed out")
              isCallHandled = true // Mark call as handled
          }
      }

      handler.postDelayed(runnable, timeout)

      activity.runOnUiThread {
        try {
          setImmersiveMode(activity)
          isImmersiveModeActive = true
          if (!isCallHandled) {
            handler.removeCallbacks(runnable) // Clear timeout if successful
            call.resolve()
            isCallHandled = true // Mark call as handled
          }
        } catch (e: Exception) {
          if (!isCallHandled) {
            handler.removeCallbacks(runnable)
            Log.e(TAG, "Error activating immersive mode: ${e.message}")
            call.reject("Error activating immersive mode", e)
            isCallHandled = true // Mark call as handled
          }
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
          isImmersiveModeActive = false
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

  override fun handleOnResume() {
    super.handleOnResume()
    if (isImmersiveModeActive) {
      val activity = bridge.activity
      if (activity != null && isImmersiveModeSupported()) {
        activity.runOnUiThread {
          try {
            setImmersiveMode(activity)
          } catch (e: Exception) {
            Log.e(TAG, "Error re-activating immersive mode on resume: ${e.message}")
          }
        }
      }
    }
  }

  private fun isImmersiveModeSupported(): Boolean {
    Log.d(TAG, "isImmersiveModeSupported: true")
    return true
  }

  private var focusChangeListener: View.OnFocusChangeListener? = null

  private fun setImmersiveMode(activity: android.app.Activity) {
    Log.d(TAG, "Setting immersive mode")
    val window = activity.window
    val decorView = window.decorView

    // Remove existing focus change listener if any
    decorView.onFocusChangeListener = null
    focusChangeListener = null

    // Use WindowInsetsController for API 30+
    WindowCompat.setDecorFitsSystemWindows(window, false)
    window.statusBarColor = Color.TRANSPARENT
    window.navigationBarColor = Color.TRANSPARENT

    val controller = WindowCompat.getInsetsController(window, decorView)
    controller?.hide(WindowInsetsCompat.Type.systemBars())
    controller?.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

    // Debounce focus change listener to avoid repeated calls
    focusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
        if (hasFocus && isImmersiveModeActive) {
            decorView.postDelayed({
                controller?.hide(WindowInsetsCompat.Type.systemBars())
            }, 100) // 100ms debounce
        }
    }
    decorView.onFocusChangeListener = focusChangeListener

    Log.d(TAG, "Immersive mode activated")
  }

  private fun resetSystemBars(activity: android.app.Activity) {
      Log.d(TAG, "Resetting system bars")
      val window = activity.window
      val decorView = window.decorView

      // Remove focus change listener to prevent memory leaks
      decorView.onFocusChangeListener = null
      focusChangeListener = null

      WindowCompat.setDecorFitsSystemWindows(window, true)
      val controller = WindowCompat.getInsetsController(window, decorView)
      controller?.show(WindowInsetsCompat.Type.systemBars())

      Log.d(TAG, "System bars reset to visible")
  }

  private fun preloadImmersiveModeResources() {
    val activity = bridge.activity
    if (activity != null) {
      activity.runOnUiThread {
        try {
          val window = activity.window
          WindowCompat.setDecorFitsSystemWindows(window, false)
          Log.d(TAG, "Preloaded immersive mode resources")
        } catch (e: Exception) {
          Log.e(TAG, "Failed to preload immersive mode resources: ${e.message}")
        }
      }
    }
  }
}