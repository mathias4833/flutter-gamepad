package io.flutter.gamepad_controller;

import android.app.Activity;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.Log;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** GamepadControllerPlugin */
public class GamepadControllerPlugin extends Activity implements FlutterPlugin, MethodCallHandler {
  private MethodChannel channel;
  private EventChannel controllerChannel;
  private FlutterPluginBinding mFlutterPluginBinding;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    mFlutterPluginBinding = flutterPluginBinding;

    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "gamepad_controller");
    channel.setMethodCallHandler(this);

    controllerChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), "gamepad_controller_listener");
    controllerChannel.setStreamHandler(new EventChannel.StreamHandler() {
      @Override
      public void onListen(Object arguments, EventChannel.EventSink events) {
        String renderNameToFlutter = "";
        boolean isCreated = false;
        int renderId = 0;

        while (isCreated == false) {
          String renderName = "hybrid-view-type" + renderId;
          renderId++;

          isCreated = mFlutterPluginBinding
                  .getPlatformViewRegistry()
                  .registerViewFactory(renderName, new NativeViewFactory(events));
          renderNameToFlutter = renderName;
        }

        if (isCreated) {
          events.success(
                  "type=" + TypeStreamData.CREATE + ',' +
                          "viewName=" + renderNameToFlutter
          );

          Log.d("CREATE_EVENT", "CREATE GAMEPAD EVENT LISTENER");
        }
      }

      @Override
      public void onCancel(Object arguments) {
        Log.e("CANCEL", "CANCEL GAMEPAD EVENT LISTENER");
      }
    });
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getGameControllers")) {
      List<Map<String, Object>> gameControllerDevices = new ArrayList<>();
      int[] deviceIds = InputDevice.getDeviceIds();
      for (int deviceId : deviceIds) {
        InputDevice dev = InputDevice.getDevice(deviceId);
        String descriptor = dev.getDescriptor();
        String name = dev.getName();
        int sources = dev.getSources();

        // Verify that the device has gamepad buttons, control sticks, or both.
        if (((sources & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD)
                || ((sources & InputDevice.SOURCE_JOYSTICK)
                == InputDevice.SOURCE_JOYSTICK)) {
          Map<String, Object> gameControllerDevice = new HashMap<>();
          // This device is a game controller. Store its device ID.
          gameControllerDevice.put("deviceId", deviceId);
          gameControllerDevice.put("descriptor", descriptor);
          gameControllerDevice.put("name", name);

          gameControllerDevices.add(gameControllerDevice);
        }
      }

      result.success(gameControllerDevices);
    } else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
    channel = null;
    controllerChannel.setStreamHandler(null);
    controllerChannel = null;
  }
}
