package com.hci.nip.android;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum IntentActionType {

    UNRECOGNIZED("com.hci.nip.glass.UNRECOGNIZED"),
    DISPLAY_UPDATE("com.hci.nip.glass.DISPLAY_UPDATE"),
    NOTIFICATION_UPDATE("com.hci.nip.glass.NOTIFICATION_UPDATE"),

    // Camera
    CAMERA_TAKE_PICTURE("com.hci.nip.glass.CAMERA_TAKE_PICTURE"),
    CAMERA_START_VIDEO_RECORD("com.hci.nip.glass.CAMERA_START_VIDEO_RECORD"),
    CAMERA_STOP_VIDEO_RECORD("com.hci.nip.glass.CAMERA_STOP_VIDEO_RECORD"),
    CAMERA_ENABLE_PREVIEW("com.hci.nip.glass.CAMERA_ENABLE_PREVIEW"),
    CAMERA_DISABLE_PREVIEW("com.hci.nip.glass.CAMERA_DISABLE_PREVIEW"),

    // BLE
    BLE_STATE_CONNECTED("com.hci.nip.glass.BLE_STATE_CONNECTED"),
    BLE_STATE_DISCONNECTED("com.hci.nip.glass.BLE_STATE_DISCONNECTED"),

    EXTERNAL_APP_INTENT("com.hci.nip.glass.EXTERNAL_APP_INTENT"),

    ;

    private final String message;

    IntentActionType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    private static final Map<String, IntentActionType> LOOKUP_MAP;

    static {
        Map<String, IntentActionType> tempMap;
        tempMap = new HashMap<>();
        for (IntentActionType type : IntentActionType.values()) {
            tempMap.put(type.getMessage(), type);
        }
        LOOKUP_MAP = Collections.unmodifiableMap(tempMap);
    }

    /**
     * @param message
     * @return {@link IntentActionType} if found, else {@link #UNRECOGNIZED}
     */
    public static IntentActionType getIntentActionType(String message) {
        IntentActionType type = LOOKUP_MAP.get(message);
        return type == null ? UNRECOGNIZED : type;
    }
}
