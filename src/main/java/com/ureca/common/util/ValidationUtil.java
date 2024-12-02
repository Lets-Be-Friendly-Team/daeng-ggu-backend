package com.ureca.common.util;

import com.ureca.common.exception.ApiException;
import com.ureca.common.exception.ErrorCode;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ValidationUtil {

    public static void validateYearAndMonth(int year, int month) {
        if (year < 1900 || month < 1 || month > 12) {
            throw new ApiException(ErrorCode.ACCOUNT_DATA_ERROR);
        }
    }

    public static void validateReservationTime(LocalTime startTime, LocalTime endTime) {
        if (startTime.isAfter(endTime)) {
            throw new ApiException(ErrorCode.DATA_VALIDATION_ERROR);
        }
    }

    // String YYYYMMDD -> Date
    public static Date stringToDate(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        try {
            LocalDate localDate = LocalDate.parse(dateString, formatter);
            LocalDateTime localDateTime = localDate.atStartOfDay();
            return Date.from(localDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant());
        } catch (Exception e) {
            // 4. 예외 발생 시 ApiException 던지기
            throw new ApiException(ErrorCode.ACCOUNT_DATA_ERROR);
        }
    }
}
