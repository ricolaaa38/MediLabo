package com.medilabo.notes.controller;

import com.medilabo.notes.model.PatientNotes;
import com.medilabo.notes.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping("/{patientId}")
    public List<PatientNotes> getAllPatientNotes(@PathVariable String patientId) {
        return noteService.findAllNoteByPatientId(patientId);
    }

    @PostMapping
    public PatientNotes addPatientNote(@Valid @RequestBody PatientNotes note) {
        return noteService.create(note);
    }

    @PutMapping("/{id}")
    public PatientNotes updatePatientNote(@PathVariable String id, @Valid @RequestBody PatientNotes updatedNote) {
        return noteService.update(id, updatedNote);
    }

    @DeleteMapping("/{id}")
    public void deletePatientNote(@PathVariable String id) {
        noteService.deletePatientNote(id);
    }
}
