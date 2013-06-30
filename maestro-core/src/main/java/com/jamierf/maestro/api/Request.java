package com.jamierf.maestro.api;

public enum Request {
    GET_FIRMWARE_VERSION(0x06, RequestType.GET), // 6

    GET_PARAMETER(0x81, RequestType.BLA), // 129
    SET_PARAMETER(0x82, RequestType.SET), // 130

    GET_VARIABLES(0x83, RequestType.BLA), // 131
    SET_VARIABLE(0x84, RequestType.SET), // 132

    SET_TARGET(0x85, RequestType.SET), // 133

    CLEAR_ERRORS(0x86, RequestType.SET), // 134
    GET_SETTINGS(0x87, RequestType.BLA), // 135

    GET_STACK(0x88, RequestType.BLA), // 136
    GET_CALL_STACK(0x89, RequestType.BLA), // 139
    SET_PWM(0x8A, RequestType.SET); // 140

    private final int code;
    private final RequestType type;

    private Request(int code, RequestType type) {
        this.code = code;
        this.type = type;
    }

    public int getCode() {
        return code;
    }

    public RequestType getType() {
        return type;
    }
}
