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

    // String YYYYMMDD -> LocalDate
    public static LocalDate stringToDate(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        try {
            return LocalDate.parse(dateString, formatter);
        } catch (Exception e) {
            // 4. 예외 발생 시 ApiException 던지기
            throw new ApiException(ErrorCode.ACCOUNT_DATA_ERROR);
        }
    }

    // LocalDate -> String (YYYYMMDD 형식)
    public static String dateToString(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return date.format(formatter);
    }

    public static void validateAfterCurrentDateTime(
            LocalDate reservationDate, LocalTime startTime) {
        LocalDateTime now = LocalDateTime.now(); // Current date and time
        LocalDateTime reservationDateTime = LocalDateTime.of(reservationDate, startTime);

        if (!reservationDateTime.isAfter(now)) {
            throw new ApiException(ErrorCode.DATA_NOT_AFTER_CURRENT);
        }
    }
}
