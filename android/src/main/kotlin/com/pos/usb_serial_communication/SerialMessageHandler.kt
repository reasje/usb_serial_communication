package com.pos.usb_serial_communication


import io.flutter.Log
import io.flutter.plugin.common.EventChannel

internal class SerialMessageHandler : EventChannel.StreamHandler {
    private var eventSink: EventChannel.EventSink? = null

    fun success(data: ByteArray?) {
        if (eventSink != null) {
            eventSink.success(data)
        }
    }

    @Override
    fun onListen(arguments: Object?, events: EventChannel.EventSink?) {
        Log.w("SerialMessageHandler", "Listening")
        eventSink = events
    }

    @Override
    fun onCancel(arguments: Object?) {
        Log.w("SerialMessageHandler", "Cancel")
        eventSink = null
    }
}