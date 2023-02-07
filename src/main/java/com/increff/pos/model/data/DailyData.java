package com.increff.pos.model.data;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class DailyData {
    private Integer ordersQuantity;
    private Integer orderItemsQuantity;
    private Float revenue;
    private LocalDateTime date;

}
