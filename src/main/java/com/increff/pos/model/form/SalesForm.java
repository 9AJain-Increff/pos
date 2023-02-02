package com.increff.pos.model.form;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class SalesForm {
    private Date startTime;
    private Date endTime;
    private String brandName;
    private String brandCategory;
}
