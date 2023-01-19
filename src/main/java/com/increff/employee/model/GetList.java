package com.increff.employee.model;


import com.increff.employee.pojo.OrderItemPojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public  class GetList {

    public GetList() {
        toAdd = new ArrayList<>();
        toDelete = new ArrayList<>();
        toUpdate = new ArrayList<>();
    }

    private List<OrderItemPojo> toUpdate, toAdd, toDelete;

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
                            Map<String, OrderItemPojo> mapping, int orderId) {

        for (OrderItemPojo data : orderBeforeUpdate) {
            if (mapping.containsKey(data.getBarcode())) {
                int requiredQuantity = mapping.get(data.getBarcode()).getQuantity();
                data.setQuantity(requiredQuantity - data.getQuantity());
                toUpdate.add(data);
                mapping.remove(data.getBarcode());
            } else {
                int requiredQuantity = data.getQuantity();
                data.setQuantity(-1 * data.getQuantity());
                toDelete.add(data);
            }
        }

        for (Map.Entry<String, OrderItemPojo> entry : mapping.entrySet()) {
            OrderItemPojo updatedOrderItemPojo = entry.getValue();
            updatedOrderItemPojo.setOrderId(orderId);
            toAdd.add(updatedOrderItemPojo);
        }
    }

}
