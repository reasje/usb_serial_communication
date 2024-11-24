package com.pos.usb_serial_communication

import androidx.annotation.NonNull

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** UsbSerialCommunicationPlugin */
class UsbSerialCommunicationPlugin: FlutterPlugin, MethodCallHandler,ActivityAware,
SerialInputOutputManager.Listener {
  private val serialMessageHandler: SerialMessageHandler = SerialMessageHandler()
  private val deviceConnectionHandler: DeviceConnectionHandler = DeviceConnectionHandler()

  private var usbIoManager: SerialInputOutputManager? = null
  private var binaryMessenger: BinaryMessenger? = null
  private var usbSerialPort: UsbSerialPort? = null
  private var usbManager: UsbManager? = null
  private var driver: UsbSerialDriver? = null

  private var connectResult: Result? = null
  private val write_wait_millis = 2000
  private var baudRate = 9600
  private var dataBits: Int = UsbSerialPort.DATABITS_8
  private var stopBits: Int = UsbSerialPort.STOPBITS_1
  private var parity: Int = UsbSerialPort.PARITY_NONE
  private var connected = false
  private var usbGrantReceiver: USBGrantReceiver? = null
  private var purgeWriteBuffers = false
  private var purgeReadBuffers = false

  private var activity: FlutterActivity? = null
  private lateinit var channel : MethodChannel

  override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "usb_serial_communication")
    channel.setMethodCallHandler(this)
    binaryMessenger = flutterPluginBinding.getBinaryMessenger()

    EventChannel(
      binaryMessenger,
      PluginConfig.SERIAL_STREAM_CHANNEL
    )
      .setStreamHandler(serialMessageHandler)

    EventChannel(
      binaryMessenger,
      PluginConfig.DEVICE_CONNECTION_STREAM_CHANNEL
    )
      .setStreamHandler(deviceConnectionHandler)
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    when (call.method) {
      "getAvailableDevices" -> {
        result.success(getConvertedAvailableDevices(availableDevices))
      }

      "write" -> {
        val data: ByteArray = call.arguments()
        result.success(write(data))
      }

      "setRTS" -> {
        setRTS(call.arguments(), result)
      }

      "setDTR" -> {
        setDTR(call.arguments(), result)
      }

      "setParameters" -> {
        baudRate = call.argument("baudRate")
        dataBits = call.argument("dataBits")
        stopBits = call.argument("stopBits")
        parity = call.argument("parity")
        setParameters(baudRate, dataBits, stopBits, parity, result)
      }

      "connect" -> {
        val name: String = call.argument("name")
        baudRate = call.argument("baudRate")

        connectResult = result
        connect(name)
      }

      "disconnect" -> {
        disconnect()
        result.success(true)
      }

      "purgeHwBuffers" -> {
        purgeWriteBuffers = call.argument("purgeWriteBuffers")
        purgeReadBuffers = call.argument("purgeReadBuffers")
        purgeHwBuffers(purgeWriteBuffers, purgeReadBuffers, result)
      }

      else -> {
        result.notImplemented()
      }
    }
  }

   
  fun setRTS(set: Boolean, result: Result) {
    var success = false
    if (usbSerialPort != null) {
      try {
        usbSerialPort.setRTS(set)
        success = true
      } catch (exception: IOException) {
      }
    }
    result.success(success)
  }

  fun setDTR(set: Boolean, result: Result) {
    var success = false
    if (usbSerialPort != null) {
      try {
        usbSerialPort.setDTR(set)
        success = true
      } catch (exception: IOException) {
      }
    }
    result.success(success)
  }

  fun setParameters(baudRate: Int, dataBits: Int, stopBits: Int, parity: Int, result: Result) {
    var success = false
    if (usbSerialPort != null) {
      try {
        usbSerialPort.setParameters(baudRate, dataBits, stopBits, parity)
        success = true
      } catch (exception: IOException) {
      }
    }
    result.success(success)
  }

  fun purgeHwBuffers(purgeWriteBuffers: Boolean, purgeReadBuffers: Boolean, result: Result) {
    var success = false
    if (usbSerialPort != null) {
      try {
        usbSerialPort.purgeHwBuffers(purgeWriteBuffers, purgeReadBuffers)
        success = true
      } catch (exception: IOException) {
      }
    }
    result.success(success)
  }


  override fun onDetachedFromEngine(@NonNull binding: FlutterPluginBinding?) {
    channel.setMethodCallHandler(null)
  }

  override fun onAttachedToActivity(@NonNull binding: ActivityPluginBinding) {
    activity = binding.getActivity() as FlutterActivity
    usbManager = activity.getSystemService(Context.USB_SERVICE) as UsbManager
    // TODO: Application cold started by an ACTION_USB_DEVICE_ATTACHED intent - propagate to Flutter?
    // TODO: For example connect to the device:
    // Intent intent = activity.getIntent();
    // String action = intent.getAction();
    // if(action.equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
    //   UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
    //   Log.i("Action", action);
    //   Log.i("UsbDevice", device.getDeviceName());
    //   connect(device.getDeviceName());
    // }
  }

  override fun onReattachedToActivityForConfigChanges(@NonNull binding: ActivityPluginBinding) {
    onAttachedToActivity(binding)
  }

  override fun onDetachedFromActivityForConfigChanges() {
  }

  override fun onDetachedFromActivity() {
  }

  private val availableDevices: List<Any>
    get() {
      val availableDrivers: List<UsbSerialDriver> =
        UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)

      val deviceInfoList: List<DeviceInfo> = ArrayList(availableDrivers.size())

      for (driver in availableDrivers) {
        val device: UsbDevice = driver.getDevice()
        deviceInfoList.add(DeviceInfo(device))
      }

      return deviceInfoList
    }

  private fun getConvertedAvailableDevices(deviceInfoList: List<DeviceInfo>): String {
    val convertedDeviceInfoList: List<Map<String, String>> = ArrayList(deviceInfoList.size())

    for (deviceInfo in deviceInfoList) {
      convertedDeviceInfoList.add(deviceInfo.toMap())
    }

    return convertedDeviceInfoList.toString()
  }

  private fun write(data: ByteArray): Boolean {
    try {
      usbSerialPort.write(data, write_wait_millis)
      return true
    } catch (e: IOException) {
      Log.e("Error Sending", e.getMessage())
      disconnect()
    }
    return false
  }

  private fun connect(name: String) {
    if (connected) {
      handleConnectResult(false)
      return
    }

    val availableDrivers: List<UsbSerialDriver> = UsbSerialProber
      .getDefaultProber().findAllDrivers(usbManager)
    val availableDevices: List<DeviceInfo> = availableDevices
    if (availableDrivers.size() === 0 || availableDevices.size() === 0) {
      handleConnectResult(false)
      return
    }

    var index = -1
    for (i in 0 until availableDevices.size()) {
      if (availableDevices[i].deviceName.equals(name)) {
        index = i
        break
      }
    }

    if (index < 0) {
      handleConnectResult(false)
      return
    }

    driver = availableDrivers[index]

    if (usbManager.hasPermission(driver.getDevice()) === false) {
      val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
      ) PendingIntent.FLAG_MUTABLE else 0
      usbGrantReceiver = USBGrantReceiver(this)
      if (Build.VERSION.SDK_INT >= 34) {
        activity.registerReceiver(
          usbGrantReceiver,
          IntentFilter(PluginConfig.INTENT_ACTION_GRANT_USB),
          Context.RECEIVER_NOT_EXPORTED // this is correct, but android studio seems to have issues recognizing.
        )
      } else {
        activity.registerReceiver(
          usbGrantReceiver,
          IntentFilter(PluginConfig.INTENT_ACTION_GRANT_USB)
        )
      }

      val usbGrantIntent: PendingIntent = PendingIntent.getBroadcast(
        activity,
        0,
        Intent(PluginConfig.INTENT_ACTION_GRANT_USB).setPackage(activity.getPackageName()),
        flags
      )

      usbManager.requestPermission(driver.getDevice(), usbGrantIntent)
    } else {
      openPort()
    }
  }

  fun openPort() {
    try {
      usbSerialPort = driver.getPorts().get(0)
      //usbSerialPort.getControlLines();
      usbSerialPort.getSupportedControlLines()

      val usbConnection: UsbDeviceConnection = usbManager.openDevice(driver.getDevice())
      usbSerialPort.open(usbConnection)

      usbSerialPort.setParameters(
        baudRate,
        UsbSerialPort.DATABITS_8,
        UsbSerialPort.STOPBITS_1,
        UsbSerialPort.PARITY_NONE
      )
      connected = true
      deviceConnectionHandler.success(true)
      usbIoManager = SerialInputOutputManager(usbSerialPort, this)
      usbIoManager.start()

      handleConnectResult(true)
      return
    } catch (e: IOException) {
      e.printStackTrace()
      disconnect()
    }

    handleConnectResult(false)
  }

  fun onUsbPermissionDenied() {
    handleConnectResult(false)
  }

  private fun disconnect() {
    connected = false
    deviceConnectionHandler.success(false)
    try {
      if (usbIoManager != null) {
        usbIoManager.setListener(null)
        usbIoManager.stop()
      }
      usbIoManager = null
      if (usbSerialPort.isOpen()) {
        usbSerialPort.close()
      }
    } catch (ignored: IOException) {
    }
  }

  private fun handleConnectResult(value: Boolean) {
    if (connectResult != null) {
      connectResult.success(value)
      connectResult = null
    }
    if (usbGrantReceiver != null) {
      activity.unregisterReceiver(usbGrantReceiver)
      usbGrantReceiver = null
    }
  }


  override fun onNewData(data: ByteArray?) {
    activity.runOnUiThread {
      serialMessageHandler.success(data)
    }
  }

  override fun onRunError(e: Exception?) {
    activity.runOnUiThread {
      disconnect()
    }
  }
}
