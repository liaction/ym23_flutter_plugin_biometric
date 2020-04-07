import 'dart:async';

import 'package:flutter/services.dart';

class Ym23FlutterPluginBiometric {
  static const MethodChannel _channel =
      const MethodChannel('ym23_flutter_plugin_biometric');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
