package com.increff.pos.model;

import lombok.Data;

@Data
public class OrderItemForm {

    private String barcode;
    private Integer quantity;
    private Integer orderId;
    private  Float price;
    private String name;

}
