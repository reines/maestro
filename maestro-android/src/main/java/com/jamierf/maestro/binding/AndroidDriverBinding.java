package com.jamierf.maestro.binding;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import java.nio.ByteBuffer;
import java.util.Map;

public class AndroidDriverBinding implements DriverBinding {

    public static AndroidDriverBinding bindToDevice(Context context, int vendorId, int productId) {
        final UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);

        final Map<String, UsbDevice> devices = manager.getDeviceList();
        for (UsbDevice device : devices.values()) {
            if (device.getVendorId() == vendorId && device.getProductId() == productId)
                return new AndroidDriverBinding(manager, device);
        }

        throw new RuntimeException("Unable to find USB device");
    }

    private final UsbManager manager;
    private final UsbDevice device;

    private transient UsbDeviceConnection conn;

    public AndroidDriverBinding(UsbManager manager, UsbDevice device) {
        this.manager = manager;
        this.device = device;
    }

    @Override
    public ByteBuffer allocateBuffer(int length) {
        return ByteBuffer.allocate(length);
    }

    private synchronized UsbDeviceConnection getConnection() {
        if (conn == null)
            conn = manager.openDevice(device);

        return conn;
    }

    @Override
    public int getVendorId() {
        return device.getVendorId();
    }

    @Override
    public int getProductId() {
        return device.getProductId();
    }

    @Override
    public String getSerial() {
        final UsbDeviceConnection conn = this.getConnection();
        return conn.getSerial();
    }

    @Override
    public int controlTransfer(int requestType, int request, int value, int index, ByteBuffer buffer, int timeout) {
        if (buffer == null)
            buffer = this.allocateBuffer(0);

        final byte[] bytes = buffer.array();

        final UsbDeviceConnection conn = this.getConnection();
        final int read = conn.controlTransfer(requestType, request, value, index, bytes, bytes.length, timeout);

        return read;
    }

    @Override
    public void close() {
        if (conn == null)
            return;

        conn.close();
        conn = null;
    }
}
