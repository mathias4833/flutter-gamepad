package io.flutter.gamepad_controller;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

import io.flutter.plugin.common.EventChannel;

class GamepadView extends EditText {
    DPad dpad = new DPad();
    private EventChannel.EventSink eventSink;
    private boolean isRTRIGGERZero = true;
    private boolean isLTRIGGERZero = true;
    private boolean isStickLeftZero = true;
    private boolean isStickRightZero = true;

    public GamepadView(Context context, EventChannel.EventSink eventSink) {
        super(context);
        this.eventSink = eventSink;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        // Without focus keys doesn't catch in Flutter
        this.requestFocus();
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e("TOUCH", event.toString());
        return false;
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        // Check if this event if from a D-pad and process accordingly.
        if (DPad.isDpadDevice(event)) {
            int press = dpad.getDirectionPressed(event);
            eventSink.success(
                    "type=" + TypeStreamData.DPAD + "," +
                            "direction=" + press
            );

            dpad.directionPressed = -1;
        }

        // Check that the event came from a game controller
        if ((event.getSource() & InputDevice.SOURCE_JOYSTICK) ==
                InputDevice.SOURCE_JOYSTICK &&
                event.getAction() == MotionEvent.ACTION_MOVE) {

            // Process all historical movement samples in the batch
            final int historySize = event.getHistorySize();

            // Process the movements starting from the
            // earliest historical position in the batch
            for (int i = 0; i < historySize; i++) {
                // Process the event at historical position i
                processJoystickInput(event, i);
            }

            // Process the current movement sample in the batch (position -1)
            processJoystickInput(event, -1);
            return true;
        }
        return super.onGenericMotionEvent(event);
    }

    private static float getCenteredAxis(MotionEvent event, InputDevice device, int axis, int historyPos) {
        final InputDevice.MotionRange range =
                device.getMotionRange(axis, event.getSource());

        // A joystick at rest does not always report an absolute position of
        // (0,0). Use the getFlat() method to determine the range of values
        // bounding the joystick axis center.
        if (range != null) {
            final float flat = range.getFlat();
            final float value =
                    historyPos < 0 ? event.getAxisValue(axis):
                            event.getHistoricalAxisValue(axis, historyPos);

            // Ignore axis values that are within the 'flat' region of the
            // joystick axis center.
            if (Math.abs(value) > flat) {
                return value;
            }
        }
        return 0;
    }

    private void processJoystickInput(MotionEvent event, int historyPos) {
        InputDevice inputDevice = event.getDevice();
        float axisRTrigger = event.getAxisValue(MotionEvent.AXIS_RTRIGGER);
        float axisLTrigger = event.getAxisValue(MotionEvent.AXIS_LTRIGGER);

        if (axisRTrigger != 0.0) {
            isRTRIGGERZero = false;
            eventSink.success(
                    "type=" + TypeStreamData.AXIS + "," +
                            "sourceInput=" + TypeStreamData.AXIS_RTRIGGER  + "," +
                            "x=" + axisRTrigger + "," + "y=0.0"
            );
        } else if (isRTRIGGERZero == false && axisRTrigger == 0) {
            isRTRIGGERZero = true;
            eventSink.success(
                    "type=" + TypeStreamData.AXIS + "," +
                            "sourceInput=" + TypeStreamData.AXIS_RTRIGGER  + "," +
                            "x=0.0" + "," + "y=0.0"
            );
        }

        if (axisLTrigger != 0.0) {
            isLTRIGGERZero = false;
            eventSink.success(
                    "type=" + TypeStreamData.AXIS + "," +
                            "sourceInput=" + TypeStreamData.AXIS_LTRIGGER  + "," +
                            "x=" + axisLTrigger + "," + "y=0.0"
            );
        } else if (isLTRIGGERZero == false && axisLTrigger == 0) {
            isLTRIGGERZero = true;
            eventSink.success(
                    "type=" + TypeStreamData.AXIS + "," +
                            "sourceInput=" + TypeStreamData.AXIS_LTRIGGER  + "," +
                            "x=0.0" + "," + "y=0.0"
            );
        }

        // AXIS_X AXIS_Y
        float x = getCenteredAxis(event, inputDevice,
                MotionEvent.AXIS_X, historyPos);
        float y = getCenteredAxis(event, inputDevice,
                MotionEvent.AXIS_Y, historyPos);
        if (x != 0 | y != 0) {
            isStickLeftZero = false;
            eventSink.success(
                    "type=" + TypeStreamData.AXIS + "," +
                            "sourceInput=" + TypeStreamData.STICK_LEFT + "," +
                            "x=" + x + "," +
                            "y=" + y
            );
        } else if (isStickLeftZero == false && x == 0 && y == 0) {
            isStickLeftZero = true;
            eventSink.success(
                    "type=" + TypeStreamData.AXIS + "," +
                            "sourceInput=" + TypeStreamData.STICK_LEFT + "," +
                            "x=" + x + "," +
                            "y=" + y
            );
        }

        // AXIS_Z AXIS_RZ
        float z = getCenteredAxis(event, inputDevice,
                    MotionEvent.AXIS_Z, historyPos);
        float rz = getCenteredAxis(event, inputDevice,
                MotionEvent.AXIS_RZ, historyPos);
        if (z != 0 | rz != 0) {
            isStickRightZero = false;
            eventSink.success(
                    "type=" + TypeStreamData.AXIS + "," +
                            "sourceInput=" + TypeStreamData.STICK_RIGHT + "," +
                            "x=" + z + "," +
                            "y=" + rz
            );
        } else if (isStickRightZero == false && z == 0 && rz == 0) {
            isStickRightZero = true;
            eventSink.success(
                    "type=" + TypeStreamData.AXIS + "," +
                            "sourceInput=" + TypeStreamData.STICK_RIGHT + "," +
                            "x=" + z + "," +
                            "y=" + rz
            );
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        eventSink.success(
                "type=" + TypeStreamData.BUTTON + "," +
                        "keyAction=ACTION_DOWN" + "," +
                        "keyCode=" + keyCode
        );

        // Allow action after pressed physical back button
        if (
                keyCode == KeyEvent.KEYCODE_BACK
                    | keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
                    | keyCode == KeyEvent.KEYCODE_VOLUME_UP
        ) {
            return false;
        }

        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        eventSink.success(
                "type=" + TypeStreamData.BUTTON + "," +
                        "keyAction=ACTION_UP" + "," +
                        "keyCode=" + keyCode
        );

        // Allow action after pressed physical back button
        if (
                keyCode == KeyEvent.KEYCODE_BACK
                    | keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
                    | keyCode == KeyEvent.KEYCODE_VOLUME_UP
        ) {
            return false;
        }

        return true;
    }
}