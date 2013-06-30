package com.jamierf.maestro;

import com.jamierf.maestro.api.Request;
import com.jamierf.maestro.api.RequestType;
import com.jamierf.maestro.binding.DriverBinding;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;

public class MaestroUsbConnection implements Closeable {

    private final DriverBinding driver;
    private final int timeout;

    public MaestroUsbConnection(DriverBinding driver, int timeout) {
        this.driver = driver;
        this.timeout = timeout;
    }

    public String getSerialNumber() {
        return driver.getSerial();
    }

    public String getFirmwareVersion() {
        final ByteBuffer payload = this.request(Request.GET_FIRMWARE_VERSION, 0x0100, 0x0000, 14);

        final byte major = (byte)((payload.get(12) & 0xF) + ((payload.get(12) >> 4 & 0xF) * 10));
        final byte minor = (byte)((payload.get(13) & 0xF) + ((payload.get(13) >> 4 & 0xF) * 10));

        return String.format("%d.%d", major, minor);
    }

    public ByteBuffer request(Request request, int length) {
        return this.request(request, 0x0000, 0x0000, length);
    }

    public ByteBuffer request(Request request, int value, int index, int length) {
        final ByteBuffer buffer = driver.allocateBuffer(length);

        try {
            final RequestType type = request.getType();
            final int read = driver.controlTransfer(type.getCode(), request.getCode(), value, index, buffer, timeout);
            if (read != length)
                throw new IOException(String.format("Read incorrect length (expected: %d, actual: %d) from controller", length, read));

            return buffer;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void send(Request request) {
        this.send(request, 0x0000, 0x0000);
    }

    public void send(Request request, int value, int index) {
        try {
            final RequestType type = request.getType();
            final int read = driver.controlTransfer(type.getCode(), request.getCode(), value, index, null, timeout);
            if (read != 0)
                throw new IOException(String.format("Read incorrect length (expected: 0, actual: %d) from controller", read));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        driver.close();
    }
}
