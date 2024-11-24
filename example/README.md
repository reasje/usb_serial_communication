
# USB Serial Communication Plugin for Flutter

A Flutter plugin to interact with USB devices via serial communication. This plugin provides functionalities for listing USB devices, connecting to them, sending/receiving data, and managing serial port configurations.

## Features

- List available USB devices.
- Connect to a specific USB device using serial communication.
- Send and receive data through the serial port.
- Listen to incoming serial messages.
- Monitor device connection status.
- Configure USB connection parameters (baud rate, data bits, stop bits, parity).
- Manage control signals like DTR (Data Terminal Ready) and RTS (Request to Send).
- Purge hardware buffers for read/write operations.

## Installation

Add the following to your `pubspec.yaml` file:

```yaml
dependencies:
  usb_serial_communication: ^0.0.1
```

Then, run:

```bash
flutter pub get
```

## Usage

### Import the Plugin

```dart
import 'package:usb_serial_communication/usb_serial_communication.dart';
import 'package:usb_serial_communication/models/device_info.dart';
```

### Get Available USB Devices

```dart
final usbCommunication = UsbSerialCommunication();

Future<void> listDevices() async {
  final devices = await usbCommunication.getAvailableDevices();
  devices.forEach((device) {
    print('Device Name: ${device.deviceName}');
  });
}
```

### Connect to a USB Device

```dart
final device = devices.first; // Select a device from the list
final baudRate = 9600; // Set the desired baud rate

final success = await usbCommunication.connect(device, baudRate);
if (success) {
  print('Connected to ${device.deviceName}');
} else {
  print('Failed to connect to ${device.deviceName}');
}
```

### Send Data

```dart
final data = Uint8List.fromList([0x01, 0x02, 0x03]); // Example data
final success = await usbCommunication.write(data);
if (success) {
  print('Data sent successfully.');
} else {
  print('Failed to send data.');
}
```

### Listen to Serial Messages

```dart
final listener = usbCommunication.getSerialMessageListener();

listener.receiveBroadcastStream().listen((message) {
  print('Received message: $message');
});
```

### Disconnect from a Device

```dart
await usbCommunication.disconnect();
print('Disconnected from the device.');
```

### Additional Features

- **Set USB Parameters**:
  ```dart
  await usbCommunication.setParameters(baudRate, dataBits, stopBits, parity);
  ```
- **Set Control Signals (DTR, RTS)**:
  ```dart
  await usbCommunication.setDTR(true); // Enable DTR
  await usbCommunication.setRTS(true); // Enable RTS
  ```
- **Purge Hardware Buffers**:
  ```dart
  await usbCommunication.purgeHwBuffers(true, true); // Purge both write and read buffers
  ```

## Example

Below is a complete example showcasing how to use the plugin:

```dart
import 'package:flutter/material.dart';
import 'package:usb_serial_communication/usb_serial_communication.dart';
import 'package:usb_serial_communication/models/device_info.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: SerialCommunicationExample(),
    );
  }
}

class SerialCommunicationExample extends StatefulWidget {
  @override
  _SerialCommunicationExampleState createState() =>
      _SerialCommunicationExampleState();
}

class _SerialCommunicationExampleState
    extends State<SerialCommunicationExample> {
  final usbCommunication = UsbSerialCommunication();
  List<DeviceInfo> devices = [];
  String status = "No device connected";

  @override
  void initState() {
    super.initState();
    listDevices();
  }

  Future<void> listDevices() async {
    final deviceList = await usbCommunication.getAvailableDevices();
    setState(() {
      devices = deviceList;
    });
  }

  Future<void> connectDevice(DeviceInfo device) async {
    final success = await usbCommunication.connect(device, 9600);
    setState(() {
      status = success
          ? "Connected to ${device.deviceName}"
          : "Failed to connect to ${device.deviceName}";
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text("USB Serial Communication Example")),
      body: Column(
        children: [
          Text(status),
          Expanded(
            child: ListView.builder(
              itemCount: devices.length,
              itemBuilder: (context, index) {
                final device = devices[index];
                return ListTile(
                  title: Text(device.deviceName),
                  onTap: () => connectDevice(device),
                );
              },
            ),
          ),
        ],
      ),
    );
  }
}
```

## API Reference

### Methods

| Method                          | Description                                            |
|---------------------------------|--------------------------------------------------------|
| `getAvailableDevices()`         | Lists all connected USB devices.                      |
| `connect(device, baudRate)`     | Connects to a USB device with the specified baud rate. |
| `disconnect()`                  | Disconnects from the currently connected device.       |
| `write(data)`                   | Sends data to the connected USB device.               |
| `getSerialMessageListener()`    | Listens to incoming serial messages.                  |
| `getDeviceConnectionListener()` | Monitors device connection status.                    |
| `setParameters(...)`            | Configures USB connection parameters.                 |
| `setDTR(value)`                 | Sets the Data Terminal Ready (DTR) signal.            |
| `setRTS(value)`                 | Sets the Request to Send (RTS) signal.                |
| `purgeHwBuffers(...)`           | Clears hardware buffers for reading/writing.          |

## Supported Platforms

- Android
- iOS (planned)

## License

This plugin is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
