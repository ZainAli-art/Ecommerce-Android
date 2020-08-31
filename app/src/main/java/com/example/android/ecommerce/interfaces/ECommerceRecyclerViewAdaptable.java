package com.example.android.ecommerce.interfaces;

import java.util.List;

public interface ECommerceRecyclerViewAdaptable<Item> {
    List<Item> getItems();

    void setItems(List<Item> dataSet);

    default Item getItem(int pos) {
        return getItems().get(pos);
    }
}
