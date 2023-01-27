package com.increff.pos.model;

import lombok.Data;

@Data
//@AllArgsConstructor
public class InventoryData extends InventoryForm{
    private String productName;
    private int id;

}
