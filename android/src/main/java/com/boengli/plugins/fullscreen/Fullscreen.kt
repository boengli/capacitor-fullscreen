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

    val activateOnLoad = getConfig().getBoolean("activateOnLoad", true)
    if (activateOnLoad) {
      val activity = bridge.activity
      if (activity != null) {
        activity.runOnUiThread {
          try {
            setImmersiveMode(activity)
            isImmersiveModeActive = true
            Log.d(TAG, "Auto-activated immersive mode on load")
          } catch (e: Exception) {
            Log.e(TAG, "Failed to auto-activate immersive mode: ${e.message}")
          }
        }
      }
    }
  }

  @PluginMethod
  fun activateImmersiveMode(call: PluginCall) {
    val activity = bridge.activity
    Log.d(TAG, "activateImmersiveMode called")
    if (activity != null) {
      val handler = Handler(Looper.getMainLooper())
      val timeout = 3000L

      var isCallHandled = false

      val runnable = Runnable {
        if (!isCallHandled) {
          Log.e(TAG, "Immersive mode activation timed out")
          call.reject("Immersive mode activation timed out")
          isCallHandled = true
        }
      }

      handler.postDelayed(runnable, timeout)

      activity.runOnUiThread {
        try {
          setImmersiveMode(activity)
          isImmersiveModeActive = true
          if (!isCallHandled) {
            handler.removeCallbacks(runnable)
            call.resolve()
            isCallHandled = true
          }
        } catch (e: Exception) {
          if (!isCallHandled) {
            handler.removeCallbacks(runnable)
            Log.e(TAG, "Error activating immersive mode: ${e.message}")
            call.reject("Error activating immersive mode", e)
            isCallHandled = true
          }
        }
      }
    } else {
      Log.e(TAG, "Activity is null, cannot activate immersive mode.")
      call.reject("Activity is null, cannot activate immersive mode.")
    }
  }

  @PluginMethod
  fun deactivateImmersiveMode(call: PluginCall) {
    val activity = bridge.activity
    Log.d(TAG, "deactivateImmersiveMode called")
    if (activity != null) {
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
      Log.e(TAG, "Activity is null, cannot deactivate immersive mode.")
      call.reject("Activity is null, cannot deactivate immersive mode.")
    }
  }

  override fun handleOnResume() {
    super.handleOnResume()
    if (isImmersiveModeActive) {
      val activity = bridge.activity
      if (activity != null) {
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

  private var focusChangeListener: View.OnFocusChangeListener? = null

  private fun setImmersiveMode(activity: android.app.Activity) {
    Log.d(TAG, "Setting immersive mode")
    val window = activity.window
    val decorView = window.decorView

    decorView.onFocusChangeListener = null
    focusChangeListener = null

    WindowCompat.setDecorFitsSystemWindows(window, false)
    if (Build.VERSION.SDK_INT < 35) {
      @Suppress("DEPRECATION")
      window.statusBarColor = Color.TRANSPARENT
      @Suppress("DEPRECATION")
      window.navigationBarColor = Color.TRANSPARENT
    }

    val controller = WindowCompat.getInsetsController(window, decorView)
    controller?.hide(WindowInsetsCompat.Type.systemBars())
    controller?.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

    focusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
      if (hasFocus && isImmersiveModeActive) {
        decorView.postDelayed({
          controller?.hide(WindowInsetsCompat.Type.systemBars())
        }, 100)
      }
    }
    decorView.onFocusChangeListener = focusChangeListener

    Log.d(TAG, "Immersive mode activated")
  }

  private fun resetSystemBars(activity: android.app.Activity) {
    Log.d(TAG, "Resetting system bars")
    val window = activity.window
    val decorView = window.decorView

    decorView.onFocusChangeListener = null
    focusChangeListener = null

    WindowCompat.setDecorFitsSystemWindows(window, true)
    val controller = WindowCompat.getInsetsController(window, decorView)
    controller?.show(WindowInsetsCompat.Type.systemBars())

    Log.d(TAG, "System bars reset to visible")
  }
}
