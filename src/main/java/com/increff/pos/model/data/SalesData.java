package com.increff.pos.model.data;

import lombok.Data;

@Data
public class SalesData {
    private String brandName;
    private String brandCategory;
    private Integer quantity;
    private Float revenue;
    private Integer productId;

}
