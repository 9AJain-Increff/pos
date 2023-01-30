package com.increff.pos.model.data;

import lombok.Data;

// TODO: 29/01/23 use lombok annotations
@Data
public class InventoryReportData {
    private String brandName;
    private String brandCategory;
    private Integer quantity;

}
