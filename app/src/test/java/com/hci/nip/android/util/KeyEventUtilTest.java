package com.hci.nip.android.util;

import android.view.KeyEvent;

import com.hci.nip.android.sensors.model.TouchBarEventType;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;


public class KeyEventUtilTest {

    @DataProvider
    public Object[][] getDataGetTouchBarEventType() {
        return new Object[][]{
                {
                        KeyEvent.KEYCODE_ENTER,
                        TouchBarEventType.ONE_FINGER_TAP
                },
                {
                        KeyEvent.KEYCODE_BACK,
                        TouchBarEventType.TWO_FINGER_TAP
                },
                {
                        KeyEvent.KEYCODE_MEDIA_PAUSE,
                        TouchBarEventType.UNKNOWN
                },
        };
    }

    @Test(dataProvider = "getDataGetTouchBarEventType")
    public void getTouchBarEventType(int keyCode, TouchBarEventType expected) {
        assertEquals(KeyEventUtil.getTouchBarEventType(keyCode), expected);
    }


    @DataProvider
    public Object[][] getDataGetKeyCode() {
        return new Object[][]{
                {
                        TouchBarEventType.ONE_FINGER_TAP,
                        KeyEvent.KEYCODE_ENTER
                },
                {
                        TouchBarEventType.TWO_FINGER_TAP,
                        KeyEvent.KEYCODE_BACK
                },
                {
                        TouchBarEventType.UNKNOWN,
                        KeyEvent.KEYCODE_UNKNOWN
                },
        };
    }

    @Test(dataProvider = "getDataGetKeyCode")
    public void getKeyCode(TouchBarEventType type, int expected) {
        assertEquals(KeyEventUtil.getKeyCode(type), expected);
    }
}