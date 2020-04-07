#import "Ym23FlutterPluginBiometricPlugin.h"
#if __has_include(<ym23_flutter_plugin_biometric/ym23_flutter_plugin_biometric-Swift.h>)
#import <ym23_flutter_plugin_biometric/ym23_flutter_plugin_biometric-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "ym23_flutter_plugin_biometric-Swift.h"
#endif

@implementation Ym23FlutterPluginBiometricPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftYm23FlutterPluginBiometricPlugin registerWithRegistrar:registrar];
}
@end
