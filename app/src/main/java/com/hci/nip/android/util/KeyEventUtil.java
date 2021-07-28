package com.hci.nip.android.util;

import android.view.KeyEvent;

import com.hci.nip.android.sensors.model.TouchBarEventType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class KeyEventUtil {

    private static final Map<Integer, TouchBarEventType> keyToTouchBarEventMap;
    private static final Map<TouchBarEventType, Integer> touchBarEventToKeyMap;

    static {
        //    ONE_FINGER_TAP, Key Event: KeyEvent { action=ACTION_DOWN, keyCode=KEYCODE_ENTER
        //    ONE_FINGER_SWIPE_FORWARD, KeyEvent { action=ACTION_DOWN, keyCode=KEYCODE_DPAD_RIGHT
        //    ONE_FINGER_SWIPE_BACKWARD, KeyEvent { action=ACTION_DOWN, keyCode=KEYCODE_DPAD_LEFT
        //    ONE_FINGER_SWIPE_UP, KeyEvent { action=ACTION_DOWN, keyCode=KEYCODE_DPAD_UP
        //    ONE_FINGER_SWIPE_DOWN, KeyEvent { action=ACTION_DOWN, keyCode=KEYCODE_DPAD_DOWN
        //    ONE_FINGER_HOLD_ONE_SECOND, KeyEvent { action=ACTION_DOWN, keyCode=KEYCODE_MENU

        //    TWO_FINGER_TAP, KeyEvent { action=ACTION_DOWN, keyCode=KEYCODE_BACK
        //    TWO_FINGER_SWIPE_FORWARD, KeyEvent { action=ACTION_DOWN, keyCode=KEYCODE_FORWARD_DEL
        //    TWO_FINGER_SWIPE_BACKWARD, KeyEvent { action=ACTION_DOWN, keyCode=KEYCODE_DEL
        //    TWO_FINGER_SLIDE_UP, KeyEvent { action=ACTION_DOWN, keyCode=KEYCODE_VOLUME_UP
        //    TWO_FINGER_SLIDE_DOWN, KeyEvent { action=ACTION_DOWN, keyCode=KEYCODE_VOLUME_DOWN

        Map<Integer, TouchBarEventType> tempMap1 = new HashMap<>(12);
        tempMap1.put(KeyEvent.KEYCODE_ENTER, TouchBarEventType.ONE_FINGER_TAP);
        tempMap1.put(KeyEvent.KEYCODE_DPAD_RIGHT, TouchBarEventType.ONE_FINGER_SWIPE_FORWARD);
        tempMap1.put(KeyEvent.KEYCODE_DPAD_LEFT, TouchBarEventType.ONE_FINGER_SWIPE_BACKWARD);
        tempMap1.put(KeyEvent.KEYCODE_DPAD_UP, TouchBarEventType.ONE_FINGER_SWIPE_UP);
        tempMap1.put(KeyEvent.KEYCODE_DPAD_DOWN, TouchBarEventType.ONE_FINGER_SWIPE_DOWN);
        tempMap1.put(KeyEvent.KEYCODE_MENU, TouchBarEventType.ONE_FINGER_HOLD_ONE_SECOND);

        tempMap1.put(KeyEvent.KEYCODE_BACK, TouchBarEventType.TWO_FINGER_TAP);
        tempMap1.put(KeyEvent.KEYCODE_FORWARD_DEL, TouchBarEventType.TWO_FINGER_SWIPE_FORWARD);
        tempMap1.put(KeyEvent.KEYCODE_DEL, TouchBarEventType.TWO_FINGER_SWIPE_BACKWARD);
        tempMap1.put(KeyEvent.KEYCODE_VOLUME_UP, TouchBarEventType.TWO_FINGER_SLIDE_UP);
        tempMap1.put(KeyEvent.KEYCODE_VOLUME_DOWN, TouchBarEventType.TWO_FINGER_SLIDE_DOWN);

        keyToTouchBarEventMap = Collections.unmodifiableMap(tempMap1);

        Map<TouchBarEventType, Integer> tempMap2 = new HashMap<>(12);
        tempMap2.put(TouchBarEventType.ONE_FINGER_TAP, KeyEvent.KEYCODE_ENTER);
        tempMap2.put(TouchBarEventType.ONE_FINGER_SWIPE_FORWARD, KeyEvent.KEYCODE_DPAD_RIGHT);
        tempMap2.put(TouchBarEventType.ONE_FINGER_SWIPE_BACKWARD, KeyEvent.KEYCODE_DPAD_LEFT);
        tempMap2.put(TouchBarEventType.ONE_FINGER_SWIPE_UP, KeyEvent.KEYCODE_DPAD_UP);
        tempMap2.put(TouchBarEventType.ONE_FINGER_SWIPE_DOWN, KeyEvent.KEYCODE_DPAD_DOWN);
        tempMap2.put(TouchBarEventType.ONE_FINGER_HOLD_ONE_SECOND, KeyEvent.KEYCODE_MENU);

        tempMap2.put(TouchBarEventType.TWO_FINGER_TAP, KeyEvent.KEYCODE_BACK);
        tempMap2.put(TouchBarEventType.TWO_FINGER_SWIPE_FORWARD, KeyEvent.KEYCODE_FORWARD_DEL);
        tempMap2.put(TouchBarEventType.TWO_FINGER_SWIPE_BACKWARD, KeyEvent.KEYCODE_DEL);
        tempMap2.put(TouchBarEventType.TWO_FINGER_SLIDE_UP, KeyEvent.KEYCODE_VOLUME_UP);
        tempMap2.put(TouchBarEventType.TWO_FINGER_SLIDE_DOWN, KeyEvent.KEYCODE_VOLUME_DOWN);

        touchBarEventToKeyMap = Collections.unmodifiableMap(tempMap2);
    }

    /**
     * @param keyCode
     * @return {@link TouchBarEventType#UNKNOWN} if not found
     */
    public static TouchBarEventType getTouchBarEventType(int keyCode) {
        TouchBarEventType eventType = keyToTouchBarEventMap.get(keyCode);
        return eventType == null ? TouchBarEventType.UNKNOWN : eventType;
    }

    /**
     * @param type
     * @return {@link KeyEvent#KEYCODE_UNKNOWN}  if not found
     */
    public static int getKeyCode(TouchBarEventType type) {
        Integer keyCode = touchBarEventToKeyMap.get(type);
        return keyCode == null ? KeyEvent.KEYCODE_UNKNOWN : keyCode;
    }

}
