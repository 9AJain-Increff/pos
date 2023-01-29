package com.increff.pos.model;

import lombok.Data;

@Data
// TODO: 29/01/23 remove the comments
//@AllArgsConstructor
public class InventoryData extends InventoryForm{
    private String productName;
    private int id;

}
