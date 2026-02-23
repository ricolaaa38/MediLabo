package com.medilabo.evaluation.model;

import lombok.Data;

import java.time.LocalDate;

/**
 * Data Transfer Object for patient information.
 */
@Data
public class PatientDTO {
    private String gender;
    private LocalDate dateOfBirth;
}
