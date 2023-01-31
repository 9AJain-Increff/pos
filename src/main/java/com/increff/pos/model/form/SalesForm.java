package com.increff.pos.model.form;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class SalesForm {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String brandName;
    private String brandCategory;
}
