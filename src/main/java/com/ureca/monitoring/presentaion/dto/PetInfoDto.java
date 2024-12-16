package com.ureca.monitoring.presentaion.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PetInfoDto {
    private String petName;
    private String petImgUrl;
    private String birthDate;
    private String gender;
    private double weight;
    private String specialNotes;
    private boolean isNeutered;
    private String majorBreed;
    private String subBreed;
}
