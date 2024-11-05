package com.boengli.plugins.fullscreen

import android.os.Build
import android.util.Log
import android.view.ViewTreeObserver
import android.view.WindowInsetsController
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
  private var focusChangeListener: ViewTreeObserver.OnWindowFocusChangeListener? = null

  @PluginMethod
  fun activateImmersiveMode(call: PluginCall) {
    val activity = bridge.activity
    Log.d(TAG, "activateImmersiveMode called")
    if (activity != null && isImmersiveModeSupported()) {
      activity.runOnUiThread {
        try {
          setImmersiveMode(activity)
          isImmersiveModeActive = true
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

  private fun setImmersiveMode(activity: android.app.Activity) {
    Log.d(TAG, "Setting immersive mode")
    val window = activity.window
    val decorView = window.decorView

    // Allow content to extend into the system UI areas
    WindowCompat.setDecorFitsSystemWindows(window, false)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      // For API 30 and above, use WindowInsetsController with BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
      val controller = window.insetsController
      controller?.hide(WindowInsetsCompat.Type.systemBars())
      controller?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    } else {
      // For API < 30, use WindowInsetsControllerCompat with BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
      val controller = WindowCompat.getInsetsController(window, decorView)
      controller.hide(WindowInsetsCompat.Type.systemBars())
      controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    // Remove any existing listener to prevent multiple listeners being added
    focusChangeListener?.let {
      decorView.viewTreeObserver.removeOnWindowFocusChangeListener(it)
    }

    // Listen for window focus changes to re-apply immersive mode
    focusChangeListener = ViewTreeObserver.OnWindowFocusChangeListener { hasFocus ->
      if (hasFocus && isImmersiveModeActive) {
        activity.runOnUiThread {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsetsCompat.Type.systemBars())
          } else {
            WindowCompat.getInsetsController(window, decorView)
              .hide(WindowInsetsCompat.Type.systemBars())
          }
        }
      }
    }
    decorView.viewTreeObserver.addOnWindowFocusChangeListener(focusChangeListener)

    Log.d(TAG, "Immersive mode activated")
  }

  private fun resetSystemBars(activity: android.app.Activity) {
    Log.d(TAG, "Resetting system bars")
    val window = activity.window
    val decorView = window.decorView

    WindowCompat.setDecorFitsSystemWindows(window, true)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      val controller = window.insetsController
      controller?.show(WindowInsetsCompat.Type.systemBars())
    } else {
      val controller = WindowCompat.getInsetsController(window, decorView)
      controller.show(WindowInsetsCompat.Type.systemBars())
    }

    // Remove the window focus listener to prevent leaks
    focusChangeListener?.let {
      decorView.viewTreeObserver.removeOnWindowFocusChangeListener(it)
      focusChangeListener = null
    }

    Log.d(TAG, "System bars reset to visible")
  }
}