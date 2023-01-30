package com.increff.pos.model.data;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderData {

    private int id;
    private LocalDateTime createdOn;


}
