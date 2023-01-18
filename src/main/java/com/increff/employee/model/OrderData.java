package com.increff.employee.model;

import lombok.Data;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class OrderData {

    private int id;
    private LocalDateTime createdOn;


}
