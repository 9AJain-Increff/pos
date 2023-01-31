package com.increff.pos.model.data;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DailyData {
    private Integer ordersQuantity;
    private Integer orderItemsQuantity;
    private Float revenue;
    private LocalDate date;

}
