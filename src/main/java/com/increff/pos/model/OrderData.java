package com.increff.pos.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderData {

    private int id;
    private LocalDateTime createdOn;


}
