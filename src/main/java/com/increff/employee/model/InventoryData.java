package com.increff.employee.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
//@AllArgsConstructor
public class InventoryData extends InventoryForm{
    private String productName;
    private int id;

}
