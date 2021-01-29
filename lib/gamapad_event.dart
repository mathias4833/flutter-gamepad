import 'package:flutter/foundation.dart';

class GamepadCreateEvent {
  final String type;
  final String viewName;

  GamepadCreateEvent({
    @required this.type,
    @required this.viewName,
  });
}

class GamepadButtonEvent {
  final String type;
  final String keyAction;
  final num keyCode;

  GamepadButtonEvent({
    @required this.type,
    @required this.keyAction,
    @required this.keyCode,
  });
}

class GamepadAxisEvent {
  final String type;
  final String sourceInput;
  final double x;
  final double y;

  GamepadAxisEvent({
    @required this.type,
    @required this.sourceInput,
    @required this.x,
    @required this.y,
  });
}

class GamepadDpadEvent {
  final String type;
  final int direction;

  GamepadDpadEvent({
    @required this.type,
    @required this.direction,
  });
}