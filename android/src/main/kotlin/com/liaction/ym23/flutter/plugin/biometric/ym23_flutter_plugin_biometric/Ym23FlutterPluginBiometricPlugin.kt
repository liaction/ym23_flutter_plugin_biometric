package com.liaction.ym23.flutter.plugin.biometric.ym23_flutter_plugin_biometric

import android.app.Activity
import android.content.Intent
import androidx.annotation.NonNull;
import com.liaction.faceFinger23.activity.YM23FingerFaceAct
import com.liaction.faceFinger23.activity.YM23RegisterAndValidAct
import com.liaction.faceFinger23.faceserver.FaceServer
import com.liaction.faceFinger23.util.YM23FingerAndFaceCallback
import com.liaction.faceFinger23.util.YM23FingerAndFaceResult
import com.liaction.faceFinger23.util.YM23FingerAndFaceUtil
import com.liaction.faceFinger23.util.ym23Log

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import java.io.File

/** Ym23FlutterPluginBiometricPlugin */
public class Ym23FlutterPluginBiometricPlugin : FlutterPlugin, MethodCallHandler {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "ym23_flutter_plugin_biometric")
        channel.setMethodCallHandler(this)
    }

    // This static function is optional and equivalent to onAttachedToEngine. It supports the old
    // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
    // plugin registration via this function while apps migrate to use the new Android APIs
    // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
    //
    // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
    // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
    // depending on the user's project. onAttachedToEngine or registerWith must both be defined
    // in the same class.
    companion object {
        lateinit var mRegistrar: Registrar
        private const val REQUEST_FACE_REGISTER = 0x23
        private const val REQUEST_FACE_VALID = 0x28
        private lateinit var mResult23: Result

        @JvmStatic
        fun registerWith(registrar: Registrar) {
            mRegistrar = registrar
            mRegistrar.addActivityResultListener { requestCode, resultCode, data ->
                when (requestCode) {
                    in arrayListOf(REQUEST_FACE_REGISTER, REQUEST_FACE_VALID) -> {
                        if (resultCode == Activity.RESULT_OK) {
                            mResult23.success(mapOf(
                                    "code" to 23,
                                    "result" to true
                            ))
                        } else {
                            mResult23.success(mapOf(
                                    "code" to -23,
                                    "result" to false
                            ))
                        }

                        true
                    }
                    else -> false
                }
            }
            val channel = MethodChannel(registrar.messenger(), "ym23_flutter_plugin_biometric")
            channel.setMethodCallHandler(Ym23FlutterPluginBiometricPlugin())
        }
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result23: Result) {
        mResult23 = result23
        when (call.method) {
            // 启动测试
            "ym23FingerAndFace" -> {
                mRegistrar.activity().startActivity(Intent(mRegistrar.context(), YM23FingerFaceAct::class.java))
                result23.success(null)
            }
            // 指纹验证
            "ym23FingerValid" -> {
                YM23FingerAndFaceUtil.validFinger(mRegistrar.context(), object : YM23FingerAndFaceCallback {
                    var errorHaveReply = false
                    override fun onError(result: YM23FingerAndFaceResult) {
                        super.onError(result)
                        if (!errorHaveReply) {
                            errorHaveReply = true
                            result23.success(mapOf(
                                    "message" to result.message,
                                    "result" to false,
                                    "code" to result.data
                            ))
                        }
                    }

                    override fun onSuccess(result: YM23FingerAndFaceResult) {
                        super.onSuccess(result)
                        result23.success(mapOf(
                                "message" to result.message,
                                "result" to true,
                                "code" to result.data
                        ))
                    }
                })
            }
            // 是否支持
            "ym23CheckSupportFingerOrFace" -> {
                YM23FingerAndFaceUtil.checkFingerAndFace23(mRegistrar.context()) { finger, face ->
                    result23.success(mapOf(
                            "finger" to finger,
                            "face" to face
                    ))
                }
            }
            // 是否注册过
            "ym23HaveFaceRegisterWithName" -> {
                val userName = call.argument<String>("name")
                ym23Log("获取 userName : $userName")
                if (userName.isNullOrEmpty() || userName.isBlank()) {
                    result23.success(mapOf(
                            "code" to -1,
                            "message" to "name 不能为空"
                    ))
                    return
                }
                val userExisted = FaceServer.getInstance().checkHaveFaceByUserName(mRegistrar.context(), userName)
                result23.success(mapOf(
                        "code" to if (userExisted) 23 else -23,
                        "result" to userExisted,
                        "message" to "存在用户人脸注册信息:[$userName]"
                ))
            }
            // 人脸识别验证注册
            "ym23RegisterAndValidFace" -> {
                val register = call.argument<Boolean>("register")
                ym23Log("获取 register : $register")
                if (register == null) {
                    result23.success(mapOf(
                            "code" to -1,
                            "message" to "register 不能为空"
                    ))
                    return
                }
                val userName = call.argument<String>("name")
                ym23Log("获取 userName : $userName")
                if (userName.isNullOrEmpty() || userName.isBlank()) {
                    result23.success(mapOf(
                            "code" to -1,
                            "message" to "name 不能为空"
                    ))
                    return
                }
                mRegistrar.activity().startActivityForResult(Intent(mRegistrar.context(),
                        YM23RegisterAndValidAct::class.java).apply {
                    putExtra("show", register)
                    putExtra("user", userName)
                    putExtra("name", if (register) "人脸注册" else "人脸验证")
                }, if (register) REQUEST_FACE_REGISTER else REQUEST_FACE_VALID)
            }
            // 面部管理
            "ym23LocalFaceManager" -> {
                // 获取面部列表
                if (!FaceServer.getInstance().init(mRegistrar.context())) {
                    result23.success(mapOf(
                            "code" to -1,
                            "message" to "引擎初始化失败"
                    ))
                    return
                }
                val faceInfoList = FaceServer.getInstance().faceList
                val resultMap = mutableMapOf<String, Any>()
                resultMap["facePath"] = FaceServer.ROOT_PATH + File.separator + FaceServer.SAVE_IMG_DIR
                resultMap["faceSuffix"] = FaceServer.IMG_SUFFIX
                resultMap["faceList"] = if (!faceInfoList.isNullOrEmpty()) faceInfoList.map { it.name }.toList() else arrayListOf()
                resultMap["code"] = 23
                FaceServer.getInstance().unInit()
                result23.success(resultMap)
            }
            else -> {
                result23.notImplemented()
            }
        }
    }


    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }
}
