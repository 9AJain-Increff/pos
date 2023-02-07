package com.increff.pos.pojo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(
        indexes = {@Index(columnList = "date")}
)
public class DailyReportPojo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer ordersQuantity;
    private Integer orderItemsQuantity;
    private Float revenue;

    private LocalDateTime date;


}
