package com.jamief.maestro.binding;

import com.jamierf.maestro.api.Product;
import com.jamierf.maestro.binding.AsyncBindingListener;
import com.jamierf.maestro.binding.DriverBinding;
import de.ailis.usb4java.libusb.*;

import java.io.IOException;
import java.nio.ByteBuffer;

public class LibUsbDriverBinding implements DriverBinding {

    public static void bindToDevice(Product product, AsyncBindingListener listener) {
        try {
            final Context context = new Context();
            LibUsb.init(context);

            final DeviceHandle handle = LibUsb.openDeviceWithVidPid(context, product.getVendorId(), product.getProductId());
            if (handle == null)
                throw new RuntimeException("Unable to find USB device");

            listener.onBind(product.getVendorId(), product.getProductId(), new LibUsbDriverBinding(handle));
        } catch (Exception e) {
            listener.onException(e);
        }
    }

    private final DeviceHandle handle;
    private final DeviceDescriptor descriptor;

    public LibUsbDriverBinding(DeviceHandle handle) {
        this.handle = handle;

        final Device device = LibUsb.getDevice(handle);

        final DeviceDescriptor descriptor = new DeviceDescriptor();
        LibUsb.getDeviceDescriptor(device, descriptor);

        this.descriptor = descriptor;
    }

    @Override
    public ByteBuffer allocateBuffer(int length) {
        return ByteBuffer.allocateDirect(length);
    }

    @Override
    public int getVendorId() {
        return descriptor.idVendor();
    }

    @Override
    public int getProductId() {
        return descriptor.idProduct();
    }

    @Override
    public String getSerial() {
        return String.valueOf(descriptor.iSerialNumber());
    }

    @Override
    public int controlTransfer(int type, int request, int value, int index, ByteBuffer buffer, int timeout) throws IOException {
        if (buffer == null)
            buffer = this.allocateBuffer(0);

        final int read = LibUsb.controlTransfer(handle, type, request, value, index, buffer, timeout);
        if (read >= 0)
            return read;

        switch (read) {
            case LibUsb.ERROR_TIMEOUT: throw new IOException("USB device timed out.");
            case LibUsb.ERROR_PIPE: throw new IOException("Control request type: " + type + ", request: " + request + " not supported by device.");
            default: throw new IOException("Unknown error occurred: " + read + ".");
        }
    }

    @Override
    public void close() {
        LibUsb.close(handle);
    }
}
