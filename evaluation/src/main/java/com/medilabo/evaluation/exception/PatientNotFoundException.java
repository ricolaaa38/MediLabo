package com.medilabo.evaluation.exception;

public class PatientNotFoundException extends RuntimeException {

    public PatientNotFoundException(String message) {
        super("Patient not found: " + message);
    }
}
