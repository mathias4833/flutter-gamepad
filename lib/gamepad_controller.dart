import 'package:flutter/services.dart';

class GamepadLibraryTypeEvent {
  static String create = 'CREATE';
  static String button = 'BUTTON';
  static String axis = 'AXIS';
  static String dpad = 'DPAD';
  static String stickLeft = 'STICK_LEFT';
  static String stickRight = 'STICK_RIGHT';
}

class GamepadController {
  static const MethodChannel _methodChannel =
      const MethodChannel('gamepad_controller');

  static dynamic get getGameControllers async {
    final dynamic gameControllers = await _methodChannel.invokeMethod('getGameControllers');
    return gameControllers;
  }
}
