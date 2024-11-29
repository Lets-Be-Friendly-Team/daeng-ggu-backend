package com.ureca.reservation.presentation.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DesignerAvailableDatesResponseDto {
    private LocalDate date; // 날짜
    private List<Integer> availableTimes; // 가능한 시간대 리스트
}
