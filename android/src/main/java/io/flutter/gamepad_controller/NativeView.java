package io.flutter.gamepad_controller;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.flutter.embedding.android.AndroidTouchProcessor;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.platform.PlatformView;

class NativeView implements PlatformView {
    private LinearLayout rootViewTest;

    NativeView(@NonNull Context context, int id, @Nullable Map<String, Object> creationParams, EventChannel.EventSink eventSink) {
        LinearLayout rootView = new ContainerView(context);
        rootView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        rootView.setOrientation(LinearLayout.VERTICAL);

        EditText gamepadView = new GamepadView(context, eventSink);
        gamepadView.setBackgroundColor(Color.GREEN);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(1, 1);
        rootView.addView(gamepadView, lp);

        rootViewTest = rootView;
    }

    @NonNull
    @Override
    public View getView() {
        return rootViewTest;
    }

    @Override
    public void dispose() {}
}