package com.finance.dispatch.worker.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {

    public static LocalDateTime convert(LocalDateTime localDateTime){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        String result = localDateTime.format(formatter);

        return LocalDateTime.parse(result, formatter);
    }

    public static String convertSimpleDate() {
        return LocalDate.now()
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }
}
