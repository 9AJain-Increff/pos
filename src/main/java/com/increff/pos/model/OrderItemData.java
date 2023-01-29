package com.increff.pos.model;

import lombok.Data;

@Data

public class OrderItemData extends OrderItemForm{
    private Integer id;
    private Integer orderId;
    private String name;

}
