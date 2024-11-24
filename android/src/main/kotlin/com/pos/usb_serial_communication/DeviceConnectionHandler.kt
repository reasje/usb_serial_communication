package com.pos.usb_serial_communication


import io.flutter.Log
import io.flutter.plugin.common.EventChannel

internal class DeviceConnectionHandler : EventChannel.StreamHandler {
    private var eventSink: EventChannel.EventSink? = null

    fun success(data: Boolean) {
        if (eventSink != null) {
            eventSink.success(data)
        }
    }

    @Override
    fun onListen(arguments: Object?, events: EventChannel.EventSink?) {
        Log.w("DeviceConnectionHandler", "Listening")
        eventSink = events
    }

    @Override
    fun onCancel(arguments: Object?) {
        Log.w("DeviceConnectionHandler", "Cancel")
        eventSink = null
    }
}