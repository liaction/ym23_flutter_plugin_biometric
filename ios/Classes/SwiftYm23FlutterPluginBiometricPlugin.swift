import Flutter
import UIKit

public class SwiftYm23FlutterPluginBiometricPlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "ym23_flutter_plugin_biometric", binaryMessenger: registrar.messenger())
    let instance = SwiftYm23FlutterPluginBiometricPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    result("iOS " + UIDevice.current.systemVersion)
  }
}
