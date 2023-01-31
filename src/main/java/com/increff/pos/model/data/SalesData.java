package com.increff.pos.model.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesData {
    private String brandName;
    private String brandCategory;
    private Integer quantity;
    private Float revenue;


}
