package com.increff.pos.model.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// FIXED: 29/01/23 use lombok annotations
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryReportData {
    private String brandName;
    private String brandCategory;
    private Integer quantity;

}
