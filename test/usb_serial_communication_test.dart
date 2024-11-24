import 'dart:typed_data';

import 'package:flutter/src/services/platform_channel.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:usb_serial_communication/usb_serial_communication_platform_interface.dart';
import 'package:usb_serial_communication/models/device_info.dart';
import 'package:usb_serial_communication/usb_serial_communication.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockUsbSerialCommunicationPlatform
    with MockPlatformInterfaceMixin
    implements USBSerialCommunicationPlatform {
  @override
  Future<String?> getPlatformVersion() => Future.value('42');

  @override
  Future<bool> connect(DeviceInfo deviceInfo, int baudRate) {
    // TODO: implement connect
    throw UnimplementedError();
  }

  @override
  Future<void> disconnect() {
    // TODO: implement disconnect
    throw UnimplementedError();
  }

  @override
  Future<List<DeviceInfo>> getAvailableDevices() {
    // TODO: implement getAvailableDevices
    throw UnimplementedError();
  }

  @override
  EventChannel getDeviceConnectionListener() {
    // TODO: implement getDeviceConnectionListener
    throw UnimplementedError();
  }

  @override
  EventChannel getSerialMessageListener() {
    // TODO: implement getSerialMessageListener
    throw UnimplementedError();
  }

  @override
  Future<void> purgeHwBuffers(bool purgeWriteBuffers, bool purgeReadBuffers) {
    // TODO: implement purgeHwBuffers
    throw UnimplementedError();
  }

  @override
  Future<void> setDTR(bool set) {
    // TODO: implement setDTR
    throw UnimplementedError();
  }

  @override
  Future<void> setParameters(
      int baudRate, int dataBits, int stopBits, int parity) {
    // TODO: implement setParameters
    throw UnimplementedError();
  }

  @override
  Future<void> setRTS(bool set) {
    // TODO: implement setRTS
    throw UnimplementedError();
  }

  @override
  Future<bool> write(Uint8List data) {
    // TODO: implement write
    throw UnimplementedError();
  }
}

void main() {
  final USBSerialCommunicationPlatform initialPlatform =
      USBSerialCommunicationPlatform.instance;

  test('$USBSerialCommunicationPlatform is the default instance', () {
    expect(initialPlatform, isInstanceOf<USBSerialCommunicationPlatform>());
  });

  test('getPlatformVersion', () async {
    UsbSerialCommunication usbSerialCommunicationPlugin =
        UsbSerialCommunication();
    MockUsbSerialCommunicationPlatform fakePlatform =
        MockUsbSerialCommunicationPlatform();
    USBSerialCommunicationPlatform.instance = fakePlatform;

    expect(await usbSerialCommunicationPlugin.getAvailableDevices(), '42');
  });
}
