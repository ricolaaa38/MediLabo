package com.medilabo.patient;

import com.medilabo.patient.model.PatientInformations;
import com.medilabo.patient.repository.PatientInformationRepository;
import com.medilabo.patient.service.PatientInformationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PatientInformationServiceTest {

    @Mock
    private PatientInformationRepository repository;

    @InjectMocks
    private PatientInformationService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll_shouldReturnList() {
        PatientInformations p = new PatientInformations();
        p.setId(1);
        p.setFirstName("John");

        when(repository.findAll()).thenReturn(List.of(p));

        List<PatientInformations> result = service.findAll();

        assertEquals(1, result.size());
        assertEquals("John", result.getFirst().getFirstName());
    }

    @Test
    void findById_shouldReturnPatient() {
        PatientInformations p = new PatientInformations();
        p.setId(1);
        p.setFirstName("Alice");

        when(repository.findById(1)).thenReturn(Optional.of(p));

        PatientInformations result = service.findById(1);

        assertEquals("Alice", result.getFirstName());
    }

    @Test
    void findById_shouldThrowNotFound() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> service.findById(99));
    }

    @Test
    void create_shouldSaveAndReturnPatient() {
        PatientInformations p = new PatientInformations();
        p.setFirstName("Bob");

        when(repository.save(p)).thenReturn(p);

        PatientInformations result = service.create(p);

        assertEquals("Bob", result.getFirstName());
        verify(repository, times(1)).save(p);
    }

    @Test
    void update_shouldModifyAndSavePatient() {
        PatientInformations existing = new PatientInformations();
        existing.setId(1);
        existing.setFirstName("Old");
        existing.setLastName("Name");
        existing.setDateOfBirth(LocalDate.of(1990, 1, 1));
        existing.setGender("M");

        PatientInformations updated = new PatientInformations();
        updated.setFirstName("New");
        updated.setLastName("Name");
        updated.setDateOfBirth(LocalDate.of(1991, 2, 2));
        updated.setGender("F");
        updated.setPostalAddress("Paris");
        updated.setPhoneNumber("0102030405");

        when(repository.findById(1)).thenReturn(Optional.of(existing));
        when(repository.save(any(PatientInformations.class))).thenAnswer(inv -> inv.getArgument(0));

        PatientInformations result = service.update(1, updated);

        assertEquals("New", result.getFirstName());
        assertEquals("F", result.getGender());
        assertEquals("Paris", result.getPostalAddress());
        verify(repository).save(existing);
    }

    @Test
    void update_shouldThrowNotFound() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> service.update(99, new PatientInformations()));
    }
}
