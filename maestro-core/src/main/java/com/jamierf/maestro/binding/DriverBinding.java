package com.jamierf.maestro.binding;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface DriverBinding {

    public ByteBuffer allocateBuffer(int length);

    public int getVendorId();
    public int getProductId();
    public String getSerial();

    public int controlTransfer(int requestType, int request, int value, int index, ByteBuffer buffer, int timeout) throws IOException;

    public void close();

}
