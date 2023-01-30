package com.increff.pos.model.data;

import com.increff.pos.model.form.OrderItemForm;
import lombok.Data;

@Data

public class OrderItemData extends OrderItemForm {
    private Integer id;
    private Integer orderId;
    private String name;

}
