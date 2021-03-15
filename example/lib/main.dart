import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:gamepad_controller/gamapad_event.dart';
import 'package:gamepad_controller/gamepad_view.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  GamepadAxisEvent _gamepadAxisEvent;

  void _gamepadAxis(GamepadAxisEvent axis) {
    setState(() {
      _gamepadAxisEvent = axis;
    });
    print("${axis.sourceInput} ${axis.x} ${axis.y}");
  }

  void _gamepadButton(GamepadButtonEvent button) {
    print(button.keyCode);
  }

  void _gamepadDpad(GamepadDpadEvent dpad) {
    print(dpad.direction);
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      builder: (context, child) {
        return Scaffold(
          body: Column(
            children: [
              Text('TEST TEST'),
              Text(_gamepadAxisEvent?.sourceInput ?? ''),
              Text(_gamepadAxisEvent?.x.toString() ?? ''),
              Text(_gamepadAxisEvent?.y.toString() ?? ''),
              GamepadView(
                onAxisHandle: _gamepadAxis,
                onButtonHandle: _gamepadButton,
                onDpadHandle: _gamepadDpad,
              ),
            ],
          ),
        );
      },
    );
  }
}
