package com.jamierf.maestro.api;

public enum SerialMode {
    // On the Command Port, user can send commands and receive responses.
    // TTL port/UART are connected to make a USB-to-serial adapter.
    USB_DUAL_PORT(0),

    // On the Command Port, user can send commands to Maestro and simultaneously
    // transmit bytes on the UART TX line, and user can receive bytes from the Maestro
    // and the UART RX line. TTL port does not do anything.
    USB_CHAINED(1),

    // On the UART, user can send commands and receive reponses after sending a 0xAA
    // byte to indicate the baud rate. Command Port receives bytes from the RX line.
    // TTL Port does not do anything.
    UART_DETECT_BAUD_RATE(2),

    // On the UART, user can send commands and receive reponses at a predetermined, fixed
    // baud rate. Command Port receives bytes from the RX line. TTL Port does not do anything.
    UART_FIXED_BAUD_RATE(3);

    private final byte code;

    private SerialMode(int code) {
        this.code = (byte) code;
    }

    public byte getCode() {
        return code;
    }
}
