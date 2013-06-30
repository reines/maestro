package com.jamierf.maestro.api;

public enum ChannelMode {
    SERVO(0),
    SERVO_MUTLIPLIED(1),
    OUTPUT(2),
    INPUT(3);

    private final byte code;

    private ChannelMode(int code) {
        this.code = (byte) code;
    }

    public byte getCode() {
        return code;
    }
}
