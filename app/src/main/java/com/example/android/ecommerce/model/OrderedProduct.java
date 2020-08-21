package com.example.android.ecommerce.model;

import androidx.annotation.NonNull;

public class OrderedProduct {
    private long oid;
    private String productName;
    private String imgUrl;

    public OrderedProduct(long oid, String productName, String imgUrl) {
        this.oid = oid;
        this.productName = productName;
        this.imgUrl = imgUrl;
    }

    public long getOid() {
        return oid;
    }

    public String getProductName() {
        return productName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    @NonNull
    @Override
    public String toString() {
        return "OrderedProduct => oid: " + oid + " productName: " + productName + " imgUrl: " + imgUrl;
    }
}
