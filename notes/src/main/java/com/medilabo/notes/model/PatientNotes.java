package com.medilabo.notes.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "notes")
@Data
public class PatientNotes {

    @Id
    private String id;

    @NotBlank
    private String patientId;

    @NotBlank
    private String patientName;

    @NotBlank
    private String note;
}
