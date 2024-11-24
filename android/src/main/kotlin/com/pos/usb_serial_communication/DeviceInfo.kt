package com.pos.usb_serial_communication


import android.hardware.usb.UsbDevice
import android.os.Build
import java.util.HashMap

internal class DeviceInfo(device: UsbDevice) {
    var deviceId: Int = device.getDeviceId()
    var version: String? = null
    var deviceName: String = device.getDeviceName()
    var manufacturerName: String? = null
    var productName: String? = null
    var productId: Int = device.getProductId()
    var vendorId: Int = device.getVendorId()
    var serialNumber: String? = null

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.manufacturerName = device.getManufacturerName()
            this.productName = device.getProductName()
            // this.serialNumber = device.getSerialNumber();
        } else {
            this.manufacturerName = ""
            this.serialNumber = ""
            this.productName = ""
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.version = device.getVersion()
        } else {
            this.version = ""
        }
    }

    fun toMap(): HashMap<String, String> {
        val hm: HashMap<String, String> = HashMap()

        hm.put("\"deviceId\"", "\"" + this.deviceId + "\"")
        hm.put("\"deviceName\"", "\"" + this.deviceName + "\"")
        hm.put("\"productId\"", "\"" + this.productId + "\"")
        hm.put("\"vendorId\"", "\"" + this.vendorId + "\"")
        hm.put("\"manufacturerName\"", "\"" + this.manufacturerName + "\"")
        hm.put("\"productName\"", "\"" + this.productName + "\"")
        hm.put("\"serialNumber\"", "\"" + this.serialNumber + "\"")
        hm.put("\"version\"", "\"" + this.version + "\"")

        return hm
    }
}