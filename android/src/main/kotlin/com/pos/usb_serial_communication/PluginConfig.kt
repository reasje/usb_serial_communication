package com.pos.usb_serial_communication


internal object PluginConfig {
    val INTENT_ACTION_GRANT_USB: String = BuildConfig.LIBRARY_PACKAGE_NAME + ".GRANT_USB"
    val SERIAL_STREAM_CHANNEL: String =
        BuildConfig.LIBRARY_PACKAGE_NAME + ".flutter_event_channel/serialStreamChannel"
    val DEVICE_CONNECTION_STREAM_CHANNEL: String =
        BuildConfig.LIBRARY_PACKAGE_NAME + ".flutter_event_channel/deviceConnectionStreamChannel"
}