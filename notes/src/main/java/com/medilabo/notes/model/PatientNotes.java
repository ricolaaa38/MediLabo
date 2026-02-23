package com.medilabo.notes.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * PatientNotes is a data model class representing a patient's note in the medical application.
 * It contains fields for the note's unique identifier, the patient's ID, the patient's name, and the note content.
 * The class is annotated with @Document to indicate that it should be stored in a MongoDB collection named "notes".
 */
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
