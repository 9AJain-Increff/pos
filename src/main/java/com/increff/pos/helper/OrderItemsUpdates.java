package com.increff.pos.helper;


import com.increff.pos.pojo.OrderItemPojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// TODO: 29/01/23 rename it
public class OrderItemsUpdates {

    public OrderItemsUpdates() {
        toAdd = new ArrayList<>();
        toDelete = new ArrayList<>();
        toUpdate = new ArrayList<>();
    }

    private final List<OrderItemPojo> toUpdate;
    private final List<OrderItemPojo> toAdd;
    private final List<OrderItemPojo> toDelete;

    public List<OrderItemPojo> getToUpdate() {
        return toUpdate;
    }

    public List<OrderItemPojo> getToAdd() {
        return toAdd;
    }

    public List<OrderItemPojo> getToDelete() {
        return toDelete;
    }


    public void updatesList(List<OrderItemPojo> orderBeforeUpdate,
                            Map<Integer, OrderItemPojo> mapping,
                            Integer orderId) {

        for (OrderItemPojo data : orderBeforeUpdate) {
            if (mapping.containsKey(data.getProductId())) {
                int requiredQuantity = mapping.get(data.getProductId()).getQuantity();
                data.setQuantity(requiredQuantity - data.getQuantity());
                data.setSellingPrice(mapping.get(data.getProductId()).getSellingPrice());
                toUpdate.add(data);
                mapping.remove(data.getProductId());
            } else {
                data.setQuantity(-1 * data.getQuantity());
                toDelete.add(data);
            }
        }

        for (Map.Entry<Integer, OrderItemPojo> entry : mapping.entrySet()) {
            OrderItemPojo updatedOrderItemPojo = entry.getValue();
            updatedOrderItemPojo.setOrderId(orderId);
            toAdd.add(updatedOrderItemPojo);
        }
    }

}
