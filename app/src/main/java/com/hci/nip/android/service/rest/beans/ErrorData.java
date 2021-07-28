package com.hci.nip.android.service.rest.beans;

import com.hci.nip.base.error.ErrorCode;

public class ErrorData {

    private final String code;
    private final String error;

    public ErrorData(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.error = errorCode.getMessage();
    }

    public ErrorData(ErrorCode errorCode, String error) {
        this.code = errorCode.getCode();
        this.error = error;
    }

    public String getCode() {
        return code;
    }

    public String getError() {
        return error;
    }
}
