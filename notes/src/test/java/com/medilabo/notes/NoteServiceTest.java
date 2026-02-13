package com.medilabo.notes;

import com.medilabo.notes.model.PatientNotes;
import com.medilabo.notes.repository.NoteRepository;
import com.medilabo.notes.service.NoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class NoteServiceTest {

    @Mock
    private NoteRepository repository;

    @InjectMocks
    private NoteService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAllNoteByPatientId_shouldReturnNotes() {
        PatientNotes n = new PatientNotes();
        n.setId("1");
        n.setPatientId("10");
        n.setNote("Test note");

        when(repository.findAllByPatientId("10")).thenReturn(List.of(n));

        List<PatientNotes> result = service.findAllNoteByPatientId("10");

        assertEquals(1, result.size());
        assertEquals("Test note", result.getFirst().getNote());
    }

    @Test
    void create_shouldSaveAndReturnNote() {
        PatientNotes n = new PatientNotes();
        n.setPatientId("10");
        n.setPatientName("John Doe");
        n.setNote("New note");

        when(repository.save(n)).thenReturn(n);

        PatientNotes result = service.create(n);

        assertEquals("New note", result.getNote());
        verify(repository, times(1)).save(n);
    }

    @Test
    void update_shouldModifyAndSaveNote() {
        PatientNotes existing = new PatientNotes();
        existing.setId("1");
        existing.setPatientId("10");
        existing.setPatientName("Old Name");
        existing.setNote("Old note");

        PatientNotes updated = new PatientNotes();
        updated.setPatientId("20");
        updated.setPatientName("New Name");
        updated.setNote("Updated note");

        when(repository.findById("1")).thenReturn(Optional.of(existing));
        when(repository.save(any(PatientNotes.class))).thenAnswer(inv -> inv.getArgument(0));

        PatientNotes result = service.update("1", updated);

        assertEquals("20", result.getPatientId());
        assertEquals("New Name", result.getPatientName());
        assertEquals("Updated note", result.getNote());
        verify(repository).save(existing);
    }

    @Test
    void update_shouldThrowWhenNoteNotFound() {
        when(repository.findById("99")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.update("99", new PatientNotes()));
    }

    @Test
    void deletePatientNote_shouldCallRepositoryDelete() {
        service.deletePatientNote("1");

        verify(repository, times(1)).deleteById("1");
    }
}

