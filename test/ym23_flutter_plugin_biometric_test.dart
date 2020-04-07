import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:ym23_flutter_plugin_biometric/ym23_flutter_plugin_biometric.dart';

void main() {
  const MethodChannel channel = MethodChannel('ym23_flutter_plugin_biometric');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await Ym23FlutterPluginBiometric.platformVersion, '42');
  });
}
