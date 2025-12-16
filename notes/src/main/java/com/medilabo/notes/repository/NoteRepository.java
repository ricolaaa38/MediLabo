package com.medilabo.notes.repository;

import com.medilabo.notes.model.PatientNotes;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends MongoRepository<PatientNotes, String> {

    List<PatientNotes> findAllByPatientId(String patientId);
}
