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
  private var useLegacyFallback: Boolean = true // Enable or disable fallback for pre-API 30

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

  @PluginMethod
  fun setLegacyFallbackEnabled(call: PluginCall) {
    useLegacyFallback = call.getBoolean("useLegacyFallback", true) ?: true
    Log.d(TAG, "Legacy fallback enabled: $useLegacyFallback")
    call.resolve()
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

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      // Use WindowInsetsController for API 30+
      WindowCompat.setDecorFitsSystemWindows(window, false)
      window.statusBarColor = Color.TRANSPARENT
      window.navigationBarColor = Color.TRANSPARENT

      val controller = WindowCompat.getInsetsController(window, decorView)
      controller.hide(WindowInsetsCompat.Type.systemBars())
      controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

      // Reapply immersive mode when the window regains focus
      decorView.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
        if (hasFocus && isImmersiveModeActive) {
          Log.d(TAG, "Window gained focus; re-applying immersive mode")
          controller.hide(WindowInsetsCompat.Type.systemBars())
        }
      }
    } else if (useLegacyFallback) {
      // Fallback for pre-API 30
      decorView.systemUiVisibility = (
              View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                      or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                      or View.SYSTEM_UI_FLAG_FULLSCREEN
                      or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                      or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                      or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
              )

      // Reapply immersive mode on focus change for pre-API 30
      decorView.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
        if (hasFocus && isImmersiveModeActive) {
          Log.d(TAG, "Window gained focus; re-applying immersive mode")
          decorView.systemUiVisibility = (
                  View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                          or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                          or View.SYSTEM_UI_FLAG_FULLSCREEN
                          or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                          or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                          or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                  )
        }
      }

      // Deprecated: System UI Visibility Change Listener for pre-API 30
      decorView.setOnSystemUiVisibilityChangeListener { visibility ->
        if ((visibility and View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
          Log.d(TAG, "System bars are visible; re-applying immersive mode")
          decorView.systemUiVisibility = (
                  View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                          or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                          or View.SYSTEM_UI_FLAG_FULLSCREEN
                          or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                          or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                          or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                  )
        }
      }
    }

    Log.d(TAG, "Immersive mode activated")
  }

  private fun resetSystemBars(activity: android.app.Activity) {
    Log.d(TAG, "Resetting system bars")
    val window = activity.window
    val decorView = window.decorView

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      WindowCompat.setDecorFitsSystemWindows(window, true)
      val controller = WindowCompat.getInsetsController(window, decorView)
      controller.show(WindowInsetsCompat.Type.systemBars())
    } else {
      decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    }
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