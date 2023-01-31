package com.increff.pos.model.data;

import lombok.Data;

@Data
public class InvoiceData {
    private String barcode;
    private Integer quantity;
    private Integer orderId;
    private Float price;
    private String name;
}
