package com.hci.nip.android.service;

import com.hci.nip.base.error.ErrorCode;

public enum ErrorCodes implements ErrorCode {

    REQUEST_NOT_SUPPORTED("4000", "Request is not supported"),
    DISPLAY_REQUEST_TIMEOUT("4001", "Display update request timeout"),

    // camera
    PICTURE_REQUEST_TIMEOUT("4060", "Camera request timeout"),
    VIDEO_REQUEST_TIMEOUT("4061", "Video request timeout"),
    VIDEO_START_FAILED("4062", "Video recording starting recorded"),
    VIDEO_STOP_FAILED("4063", "Video record stopping failed"),

    // sensors
    SENSOR_NOT_FOUND("4010", "Sensor is not found for given id"),
    SENSOR_UPDATE_FAILED("4011", "Sensor update failed"),

    // actuators
    ACTUATOR_NOT_FOUND("4020", "Actuator is not found for given id"),
    ACTUATOR_UPDATE_FAILED("4021", "Actuator update failed"),
    ACTUATOR_NOT_ACTIVE("4022", "Actuator is not active"),

    // actuators: haptic
    HAPTIC_DATA_INVALID("4030", "Haptic data is invalid"),
    // actuators: notifications
    NOTIFICATION_DATA_INVALID("4031", "Notification data is invalid"),
    TOUCH_DATA_INVALID("4032", "Touch data is invalid"),

    // audio
    UNSUPPORTED_AUDIO_FORMAT("4051", "Provided format is not supported"),
    AUDIO_PLAYBACK_FAILED("4052", "Failed to start the playback"),
    AUDIO_RECORD_FAILED("4053", "Failed to start the record"),
    AUDIO_OPERATION_AT_ILLEGAL_STATE("4054", "Operation is not supported at current state"),
    AUDIO_ID_MISMATCH("4055", "Provided id does not match the required id"),
    AUDIO_FILE_NOT_FOUND("4056", "Provided audio file not found"),

    // files
    FILE_NOT_FOUND("4070", "Provided file not found"),
    FILE_READ_FAILED("4071", "Could not read the files"),
    FILE_WRITE_FAILED("4072", "Could not write the file"),

    ;

    private final String code;
    private final String message;

    ErrorCodes(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
