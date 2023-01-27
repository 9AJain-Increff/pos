package com.increff.pos.model;

import lombok.Data;

@Data
public class ProductForm {

    private String name;

    private String brandName;
    private String brandCategory;
    private Float price;
    private String barcode;


}
