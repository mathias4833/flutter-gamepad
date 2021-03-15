import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';
import 'package:gamepad_controller/gamapad_event.dart';
import 'package:gamepad_controller/gamepad_controller.dart';
import 'package:gamepad_controller/gamepad_model.dart';

class GamepadView extends StatefulWidget {
  final Function(GamepadButtonEvent button) onButtonHandle;
  final Function(GamepadAxisEvent axis) onAxisHandle;
  final Function(GamepadDpadEvent dpad) onDpadHandle;
  GamepadView({
    @required this.onButtonHandle,
    @required this.onAxisHandle,
    @required this.onDpadHandle,
  }) : assert(onButtonHandle != null),
        assert(onAxisHandle != null),
        super();

  @override
  _GamepadViewState createState() => _GamepadViewState();
}

class _GamepadViewState extends State<GamepadView> {
  static const platformEvent = const EventChannel('gamepad_controller_listener');
  final Map<String, dynamic> creationParams = <String, dynamic>{};
  List<GamepadModel> _gameControllers;
  String viewName;
  StreamSubscription _platformEvent;

  @override
  void initState() {
    super.initState();
    _platformEvent = platformEvent.receiveBroadcastStream().listen((event) {
      final Map<String, dynamic> eventParameters = <String, dynamic>{};

      if (event != null && event is String) {
        event.split(',').forEach((e) {
          var createPairList = e.split('=');

          eventParameters[createPairList[0]] = createPairList[1];
        });
      }

      if (eventParameters['type'] == GamepadLibraryTypeEvent.create) {
        var createEventParameters = GamepadCreateEvent(
          type: eventParameters['type'],
          viewName: eventParameters['viewName']
        );

        setState(() {
          viewName = createEventParameters.viewName;
        });
      } else if (eventParameters['type'] == GamepadLibraryTypeEvent.button) {
        var buttonEventParameters = GamepadButtonEvent(
            type: eventParameters['type'],
            keyAction: eventParameters['keyAction'],
            keyCode: int.parse(eventParameters['keyCode'])
        );

        widget.onButtonHandle(buttonEventParameters);
      } else if (eventParameters['type'] == GamepadLibraryTypeEvent.axis) {
        var axisEventParameters = GamepadAxisEvent(
            type: eventParameters['type'],
            sourceInput: eventParameters['sourceInput'],
            x: double.parse(eventParameters['x']),
            y: double.parse(eventParameters['y'])
        );

        widget.onAxisHandle(axisEventParameters);
      } else if (eventParameters['type'] == GamepadLibraryTypeEvent.dpad) {
        var dpadEventParameters = GamepadDpadEvent(
            type: eventParameters['type'],
            direction: int.parse(eventParameters['direction']),
        );

        widget.onDpadHandle(dpadEventParameters);
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    if (viewName == null) {
      return Container();
    }

    return Container(
      height: 1,
      width: 1,
      child: PlatformViewLink(
        viewType: viewName,
        surfaceFactory: (BuildContext context, PlatformViewController controller) {
          return AndroidViewSurface(
            controller: controller,
            gestureRecognizers: const <Factory<OneSequenceGestureRecognizer>>{},
            hitTestBehavior: PlatformViewHitTestBehavior.transparent,
          );
        },
        onCreatePlatformView: (PlatformViewCreationParams params) {
          var service = PlatformViewsService.initSurfaceAndroidView(
            id: params.id,
            viewType: viewName,
            layoutDirection: TextDirection.ltr,
            creationParams: creationParams,
            creationParamsCodec: StandardMessageCodec(),
          )
            ..addOnPlatformViewCreatedListener(params.onPlatformViewCreated)
            ..create();

          return service;
        },
      )
    );
  }

  @override
  void dispose() {
    _platformEvent?.cancel();
    _platformEvent = null;
    super.dispose();
  }
}
