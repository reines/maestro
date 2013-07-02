package com.jamierf.maestro.binding;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import com.jamierf.maestro.api.Product;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

public class AndroidDriverBinding implements DriverBinding {

    private static final String ACTION_USB_PERMISSION = AndroidDriverBinding.class.getPackage() + ".USB_PERMISSION";

    public static void bindToDevice(Context context, Product product, AsyncBindingListener listener) {
        try {
            final UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);

            final Map<String, UsbDevice> devices = manager.getDeviceList();
            for (final UsbDevice device : devices.values()) {
                if (device.getVendorId() == product.getVendorId() && device.getProductId() == product.getProductId()) {
                    AndroidDriverBinding.bindToDevice(context, device, listener);
                    return;
                }
            }

            throw new IOException("Unable to find USB device.");
        } catch (Exception e) {
            listener.onException(e);
        }
    }

    private static void bindToDevice(Context context, final UsbDevice device, final AsyncBindingListener listener) {
        final UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);

        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (!ACTION_USB_PERMISSION.equals(intent.getAction())) {
                    listener.onException(new IOException("No permission to access USB device."));
                    return;
                }

                listener.onBind(device.getVendorId(), device.getProductId(), new AndroidDriverBinding(manager, device));
            }
        }, new IntentFilter(ACTION_USB_PERMISSION));

        manager.requestPermission(device, PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0));
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
        return ByteBuffer.wrap(new byte[length]);
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

        if (!buffer.hasArray())
            throw new IllegalArgumentException("Buffer must be array based (should be allocated using allocateBuffer(int)).");

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
