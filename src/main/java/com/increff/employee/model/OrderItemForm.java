package com.increff.employee.model;

import lombok.Data;

@Data
public class OrderItemForm {

    private String barcode;
    private int quantity;
    private String name;
    private  Float price;

}
