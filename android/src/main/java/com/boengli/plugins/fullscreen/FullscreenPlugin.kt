package com.boengli.plugins.fullscreen

import com.getcapacitor.Plugin
import com.getcapacitor.annotation.CapacitorPlugin
import com.getcapacitor.PluginMethod
import com.getcapacitor.PluginCall

@CapacitorPlugin(name = "Fullscreen")
class FullscreenPlugin : Plugin() {

    override fun load() {
        Fullscreen()
    }
}