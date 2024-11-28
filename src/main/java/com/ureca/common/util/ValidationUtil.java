package com.ureca.common.util;

import com.ureca.common.exception.ApiException;
import com.ureca.common.exception.ErrorCode;
import java.time.LocalTime;

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
}
