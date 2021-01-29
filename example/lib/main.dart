import 'package:flutter/material.dart';
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
  void _gamepadAxis(GamepadAxisEvent axis) {
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
