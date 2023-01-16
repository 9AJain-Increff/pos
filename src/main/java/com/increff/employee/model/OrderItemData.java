package com.increff.employee.model;

import lombok.Data;

@Data
public class OrderItemData extends OrderItemForm{
    private String barcode;
    private int price;
    private int quantity;

    private int id;

    private int orderId;
}
