package com.jamierf.maestro.binding;

public interface AsyncBindingListener<T extends DriverBinding> {

    public void onBind(int vendorId, int productId, T driver);
    public void onException(Throwable t);
}
