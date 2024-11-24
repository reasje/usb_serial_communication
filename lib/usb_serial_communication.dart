import 'package:flutter/services.dart';
import 'package:usb_serial_communication/usb_serial_communication_platform_interface.dart';
import 'package:usb_serial_communication/models/device_info.dart';

class UsbSerialCommunication {
  Future<List<DeviceInfo>> getAvailableDevices() {
    return USBSerialCommunicationPlatform.instance.getAvailableDevices();
  }

  Future<bool> connect(DeviceInfo deviceInfo, int baudRate) async {
    return USBSerialCommunicationPlatform.instance
        .connect(deviceInfo, baudRate);
  }

  Future<void> disconnect() async {
    return USBSerialCommunicationPlatform.instance.disconnect();
  }

  Future<bool> write(Uint8List data) async {
    final isSent = await USBSerialCommunicationPlatform.instance.write(data);
    return isSent;
  }

  EventChannel getSerialMessageListener() {
    return USBSerialCommunicationPlatform.instance.getSerialMessageListener();
  }

  EventChannel getDeviceConnectionListener() {
    return USBSerialCommunicationPlatform.instance
        .getDeviceConnectionListener();
  }

  Future<void> setDTR(bool set) async {
    return USBSerialCommunicationPlatform.instance.setDTR(set);
  }

  Future<void> setRTS(bool set) async {
    return USBSerialCommunicationPlatform.instance.setRTS(set);
  }

  Future<void> setParameters(
      int baudRate, int dataBits, int stopBits, int parity) async {
    return USBSerialCommunicationPlatform.instance
        .setParameters(baudRate, dataBits, stopBits, parity);
  }

  Future<void> purgeHwBuffers(
      bool purgeWriteBuffers, bool purgeReadBuffers) async {
    return USBSerialCommunicationPlatform.instance
        .purgeHwBuffers(purgeWriteBuffers, purgeReadBuffers);
  }
}
