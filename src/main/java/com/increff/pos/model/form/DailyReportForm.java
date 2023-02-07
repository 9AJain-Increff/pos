package com.increff.pos.model.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyReportForm {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
