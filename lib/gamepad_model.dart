import 'package:flutter/foundation.dart';

class GamepadModel {
  final String name;
  final String descriptor;
  final num deviceId;

  GamepadModel({
    @required this.name,
    @required this.descriptor,
    @required this.deviceId,
  });
}