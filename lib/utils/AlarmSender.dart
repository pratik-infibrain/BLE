import 'package:flutter/services.dart';

class AlarmSender {
  static const MethodChannel _channel = MethodChannel('alarm_sender');

  static Future<void> createSocket(String data) async {
    try {
      await _channel.invokeMethod('createSocket', {'data': data});
    } on PlatformException catch (e) {
      print("Failed to send alarm data: '${e.message}'.");
    }
  }

  static Future<void> sendAlarmData(String data) async {
    try {
      await _channel.invokeMethod('sendAlarmData', {'data': data});
    } on PlatformException catch (e) {
      print("Failed to send alarm data: '${e.message}'.");
    }
  }

}
