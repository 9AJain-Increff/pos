package com.increff.pos.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateTimeUtil {

    public static LocalDateTime convertToLocalDateViaInstant(Date dateToConvert) {

        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate().atStartOfDay();
    }
}
