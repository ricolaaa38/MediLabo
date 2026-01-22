package com.medilabo.evaluation.model;

public enum DiabetesRisk {
    NONE("None"),
    BORDERLINE("Borderline"),
    IN_DANGER("In Danger"),
    EARLY_ONSET("Early onset");

    private final String label;

    DiabetesRisk(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
