package com.increff.pos.model;

import lombok.Data;

@Data
public class InvoiceData {
    private String barcode;
    private Integer quantity;
    private Integer orderId;
    private  Float price;
    private String name;
}
