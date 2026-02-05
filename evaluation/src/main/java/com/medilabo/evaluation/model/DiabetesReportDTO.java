package com.medilabo.evaluation.model;

import lombok.Data;

@Data
public class DiabetesReportDTO {
    private Integer age;
    private String gender;
    private int triggersCount;
    private String risk;

    public DiabetesReportDTO(Integer age, String gender, int triggersCount, String risk) {
        this.age = age;
        this.gender = gender;
        this.triggersCount = triggersCount;
        this.risk = risk;
    }
}
