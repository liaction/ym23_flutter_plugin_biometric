import 'dart:async';
import 'dart:ffi';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:ym23_flutter_plugin_biometric/ym_colors.dart';

class Ym23FlutterPluginBiometric {
  static const MethodChannel _channel =
      const MethodChannel('ym23_flutter_plugin_biometric');

  @Deprecated("no mean")
  static Future<String> get platformVersion async {
    final String version = "Android ym232820 by liaction";
    return version;
  }

  ///
  /// 是否停止继续后续操作
  /// return [true|false]
  ///
  static Future<bool> shouldGoBack() async => false;

  ///
  /// 启动测试
  ///
  static Future<Void> ym23FingerAndFace() {
    return _channel.invokeMethod("ym23FingerAndFace");
  }

  ///
  /// 指纹验证
  ///
  static Future<Map> ym23FingerValid() {
    return _channel.invokeMethod("ym23FingerValid");
  }

  ///
  /// 是否注册过
  ///
  static Future<Map> ym23HaveFaceRegisterWithName({String name}) {
    return _channel
        .invokeMethod("ym23HaveFaceRegisterWithName", {"name": name});
  }

  ///
  /// 是否支持生物验证
  ///
  static Future<Map> ym23CheckSupportFingerOrFace() {
    return _channel.invokeMethod("ym23CheckSupportFingerOrFace");
  }

  ///
  /// 人脸识别验证注册
  ///
  static Future<Map> ym23RegisterAndValidFace(
      {bool register = true, String name}) {
    return _channel.invokeMethod(
        "ym23RegisterAndValidFace", {"register": register, "name": name});
  }

  ///
  /// 指纹验证
  ///
  static Future<bool> ym23ShowFingerValidDialog({
    BuildContext context,
    bool dark = false,
    Color errorColor = ymAppRedColor,
    Color rightColor = ymAppBarColor,
  }) async {
    var result = await showGeneralDialog(
      barrierDismissible: false,
      barrierColor: dark ? Color(0x99333333) : Color(0x55333333),
      context: context,
      pageBuilder: (BuildContext context, Animation<double> animation,
          Animation<double> secondaryAnimation) {
        var fingerMessage = "请验证指纹";
        var errorResult = false;
        var validContinue = true;
        return StatefulBuilder(
            builder: (BuildContext context, StateSetter setState) {
          if (validContinue) {
            ym23FingerValid().then((value) {
              if (value["result"]) {
                errorResult = false;
                Navigator.of(context).pop(true);
                return;
              }
              setState(() {
                fingerMessage = value["message"];
                errorResult = true;
                validContinue = value["code"] != 23;
              });
            });
          }
          return Center(
            child: Material(
              color: Colors.transparent,
              child: Container(
                width: MediaQuery.of(context).size.width - 80,
                constraints: BoxConstraints(
                  minHeight: 160,
                ),
                decoration: BoxDecoration(),
                child: Stack(
                  children: <Widget>[
                    Container(
                      decoration: BoxDecoration(
                        color: Colors.white,
                        borderRadius: BorderRadius.circular(8),
                      ),
                      padding: const EdgeInsets.only(top: 20, bottom: 10),
                      margin: const EdgeInsets.only(top: 40),
                      child: Column(
                        mainAxisSize: MainAxisSize.min,
                        children: <Widget>[
                          Container(
                            constraints: BoxConstraints(
                              minHeight: 80,
                            ),
                            margin: const EdgeInsets.only(top: 20),
                            child: Column(
                              mainAxisAlignment: MainAxisAlignment.center,
                              children: <Widget>[
                                Text(
                                  "$fingerMessage",
                                  style: TextStyle(
                                    color:
                                        errorResult ? errorColor : rightColor,
                                  ),
                                ),
                              ],
                            ),
                          ),
                          Row(
                            children: <Widget>[
                              FlatButton(
                                  onPressed: () {
                                    Navigator.of(context).pop(false);
                                  },
                                  child: Text("取消",
                                      style: TextStyle(color: errorColor))),
                            ],
                            mainAxisAlignment: MainAxisAlignment.end,
                          ),
                        ],
                      ),
                    ),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: <Widget>[
                        CircleAvatar(
                          backgroundColor: ymAppBarColor,
                          child: Icon(
                            Icons.fingerprint,
                            size: 40,
                            color: Colors.white,
                          ),
                          radius: 40,
                        ),
                      ],
                    )
                  ],
                ),
              ),
            ),
          );
        });
      },
      transitionDuration: Duration(microseconds: 300),
    );
    return result;
  }
}
