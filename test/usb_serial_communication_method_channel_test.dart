import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:usb_serial_communication/usb_serial_communication_method_channel.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  MethodChannelUSBSerialCommunication platform =
      MethodChannelUSBSerialCommunication();
  const MethodChannel channel = MethodChannel('usb_serial_communication');

  setUp(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(
      channel,
      (MethodCall methodCall) async {
        return '42';
      },
    );
  });

  tearDown(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(channel, null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getAvailableDevices(), '42');
  });
}
