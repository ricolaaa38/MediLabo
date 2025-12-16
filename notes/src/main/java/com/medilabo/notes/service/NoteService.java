package com.medilabo.notes.service;

import com.medilabo.notes.model.PatientNotes;
import com.medilabo.notes.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {

    @Autowired
    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public List<PatientNotes> findAllNoteByPatientId(String patientId) {
        return noteRepository.findAllByPatientId(patientId);
    }

    public PatientNotes create(PatientNotes patientNotes) {
        return noteRepository.save(patientNotes);
    }

    public PatientNotes update(String id, PatientNotes updatedPatientNotes) {
        PatientNotes existingPatientNotes = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note non trouvée !"));

        existingPatientNotes.setPatientId(updatedPatientNotes.getPatientId());
        existingPatientNotes.setPatientName(updatedPatientNotes.getPatientName());
        existingPatientNotes.setNote(updatedPatientNotes.getNote());

        return noteRepository.save(existingPatientNotes);
    }

    public void deletePatientNote(String id) {
        noteRepository.deleteById(id);
    }
}
