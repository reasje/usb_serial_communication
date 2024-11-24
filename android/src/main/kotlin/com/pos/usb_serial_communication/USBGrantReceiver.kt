package com.pos.usb_serial_communication


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbManager

internal class USBGrantReceiver(plugin: FlutterSerialCommunicationPlugin) : BroadcastReceiver() {
    var plugin: FlutterSerialCommunicationPlugin = plugin

    @Override
    fun onReceive(context: Context?, intent: Intent) {
        val action: String = intent.getAction()
        if (PluginConfig.INTENT_ACTION_GRANT_USB.equals(action)) {
            val granted: Boolean =
                intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)
            if (granted) {
                plugin.openPort()
            } else {
                plugin.onUsbPermissionDenied()
            }
        }
    }
}