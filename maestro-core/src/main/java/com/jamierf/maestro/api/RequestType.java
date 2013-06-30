package com.jamierf.maestro.api;

public enum RequestType {
    GET(0x80), // 128
    SET(0x40), // 64
    BLA(0xC0); // 192

    private final int code;

    private RequestType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
