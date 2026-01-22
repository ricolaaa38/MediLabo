package com.medilabo.evaluation.model;

import lombok.Data;

@Data
public class DiabetesReportDTO {
    private String patientId;
    private String fullName;
    private Integer age;
    private String gender;
    private int triggersCount;
    private String risk;

    public DiabetesReportDTO(String patientId, String fullName, Integer age, String gender, int triggersCount, String risk) {
        this.patientId = patientId;
        this.fullName = fullName;
        this.age = age;
        this.gender = gender;
        this.triggersCount = triggersCount;
        this.risk = risk;
    }
}
