package com.jamierf.maestro.binding;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import java.nio.ByteBuffer;

public class AndroidDriverBinding implements DriverBinding {

//    public static Collection<UsbDevice> findDevices(UsbManager manager) {
//        final ImmutableSet.Builder<UsbDevice> controllers = ImmutableSet.builder();
//
//        final Map<String, UsbDevice> devices = manager.getDeviceList();
//        for (UsbDevice device : devices.values()) {
//            final Optional<Product> product = Product.fromDevice(device);
//
//            // Skip unrecognised devices
//            if (!product.isPresent())
//                continue;
//
//            controllers.add(device);
//        }
//
//        return controllers.build();
//    }

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
