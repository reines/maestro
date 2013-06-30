package com.jamierf.maestro.api;

import com.google.common.base.Optional;

// TODO: Device product IDs
public enum Product {
    MICRO6(8187, 137, 6, 1024),
    MINI12(8187, 0, 12, 8192),

    MINI18(8187, 0, 18, 8192),
    MINI24(8187, 0, 24, 8192);

    public static Optional<Product> fromId(int vendorId, int productId) {
        for (Product product : Product.values()) {
            if (vendorId != product.getVendorId())
                continue;

            if (productId != product.getProductId())
                continue;

            return Optional.of(product);
        }

        return Optional.absent();
    }

    private final int vendorId;
    private final int productId;
    private final int ports;
    private final int maxScriptLength;

    Product(int vendorId, int productId, int ports, int maxScriptLength) {
        this.vendorId = vendorId;
        this.productId = productId;
        this.ports = ports;
        this.maxScriptLength = maxScriptLength;
    }

    public int getVendorId() {
        return vendorId;
    }

    public int getProductId() {
        return productId;
    }

    public int getPorts() {
        return ports;
    }

    public int getMaxScriptLength() {
        return maxScriptLength;
    }
}
