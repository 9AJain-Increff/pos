package com.increff.employee.model;

import lombok.Data;
import org.hibernate.secure.spi.IntegrationException;

import java.time.LocalDate;
@Data
public class DailyData {
    private Integer ordersQuantity;
    private  Integer orderItemsQuantity;
    private Float revenue;
    private LocalDate date;

}
