package com.increff.employee.model;

import lombok.Data;

@Data
public class OrderItemForm {

    private String barcode;
    private Integer quantity;
    private Integer orderId;
    private  Float price;

}
