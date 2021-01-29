package io.flutter.gamepad_controller;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Map;

import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;

public class NativeViewFactory extends PlatformViewFactory {
    private final EventChannel.EventSink eventSink;

    public NativeViewFactory(EventChannel.EventSink eventSink) {
        super(StandardMessageCodec.INSTANCE);
        this.eventSink = eventSink;
    }

    @NonNull
    @Override
    public PlatformView create(Context context, int id, Object args) {
        final Map<String, Object> creationParams = (Map<String, Object>) args;
        return new NativeView(context, id, creationParams, eventSink);
    }
}
