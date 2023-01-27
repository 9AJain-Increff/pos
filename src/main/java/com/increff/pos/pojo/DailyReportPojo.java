package com.increff.pos.pojo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
@Data
public class DailyReportPojo {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;
    private Integer ordersQuantity;
    private  Integer orderItemsQuantity;
    private Float revenue;

    private LocalDate date;


}
