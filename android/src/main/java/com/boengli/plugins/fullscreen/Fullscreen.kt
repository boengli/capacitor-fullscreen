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
    Log.d(TAG, "isImmersiveModeSupported: true")
    return true
  }

  private fun setImmersiveMode(activity: android.app.Activity) {
    Log.d(TAG, "Setting immersive mode")
    val window = activity.window
    val decorView = window.decorView

    WindowCompat.setDecorFitsSystemWindows(window, false)
    window.statusBarColor = Color.TRANSPARENT
    window.navigationBarColor = Color.TRANSPARENT

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      val controller = WindowCompat.getInsetsController(window, decorView)
      controller.hide(WindowInsetsCompat.Type.systemBars())
      controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    } else {
      // Fallback to use SYSTEM_UI_FLAG_IMMERSIVE_STICKY on pre-API 30 devices
      decorView.systemUiVisibility = (
              View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                      or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                      or View.SYSTEM_UI_FLAG_FULLSCREEN
                      or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                      or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                      or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
              )
    }

    // Reapply immersive mode if the system UI becomes visible
    decorView.setOnSystemUiVisibilityChangeListener { visibility ->
      Log.d(TAG, "System UI visibility changed: $visibility")
      if ((visibility and View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
        setImmersiveMode(activity)
      }
    }

    // Reapply immersive mode when the window regains focus
    decorView.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
      if (hasFocus && isImmersiveModeActive) {
        Log.d(TAG, "Window gained focus; re-applying immersive mode")
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

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      val controller = WindowCompat.getInsetsController(window, decorView)
      controller.show(WindowInsetsCompat.Type.systemBars())
    } else {
      decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    }

    // Remove listeners to prevent memory leaks
    decorView.setOnSystemUiVisibilityChangeListener(null)
    decorView.onFocusChangeListener = null

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