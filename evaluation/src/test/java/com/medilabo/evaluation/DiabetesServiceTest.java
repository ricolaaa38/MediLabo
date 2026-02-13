package com.medilabo.evaluation;

import com.medilabo.evaluation.exception.NoteNotFoundException;
import com.medilabo.evaluation.exception.PatientNotFoundException;
import com.medilabo.evaluation.model.*;
import com.medilabo.evaluation.service.DiabetesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class DiabetesServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private DiabetesService diabetesService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(diabetesService, "gatewayUrl", "http://gateway");
    }

    @Test
    void evaluate_shouldReturnReport() {
        PatientDTO patient = new PatientDTO();
        patient.setGender("M");
        patient.setDateOfBirth(LocalDate.of(1980, 1, 1));

        NoteDTO n1 = new NoteDTO();
        n1.setNote("Patient fume et a du cholestérol");

        NoteDTO n2 = new NoteDTO();
        n2.setNote("RAS");

        when(restTemplate.exchange(
                eq("http://gateway/api/patients/1"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(PatientDTO.class)
        )).thenReturn(ResponseEntity.ok(patient));

        ParameterizedTypeReference<List<NoteDTO>> typeRef =
                new ParameterizedTypeReference<>() {};

        when(restTemplate.exchange(
                eq("http://gateway/api/notes/1"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(typeRef)
        )).thenReturn(ResponseEntity.ok(List.of(n1, n2)));

        DiabetesReportDTO report = diabetesService.evaluate("1", null, null);

        assertEquals("M", report.getGender());
        assertEquals(2, report.getTriggersCount());
        assertEquals("Borderline", report.getRisk());
    }

    @Test
    void evaluate_shouldThrowPatientNotFound() {
        when(restTemplate.exchange(
                eq("http://gateway/api/patients/99"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(PatientDTO.class)
        )).thenThrow(new RuntimeException("404"));

        assertThrows(PatientNotFoundException.class, () ->
                diabetesService.evaluate("99", null, null)
        );
    }

    @Test
    void evaluate_shouldThrowNoteNotFound() {
        PatientDTO patient = new PatientDTO();
        patient.setGender("F");
        patient.setDateOfBirth(LocalDate.of(1990, 1, 1));

        when(restTemplate.exchange(
                eq("http://gateway/api/patients/1"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(PatientDTO.class)
        )).thenReturn(ResponseEntity.ok(patient));

        ParameterizedTypeReference<List<NoteDTO>> typeRef =
                new ParameterizedTypeReference<>() {};


        when(restTemplate.exchange(
                eq("http://gateway/api/notes/1"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(typeRef)
        )).thenThrow(new RuntimeException("404"));

        assertThrows(NoteNotFoundException.class, () ->
                diabetesService.evaluate("1", null, null)
        );
    }

    @Test
    void countTriggers_shouldDetectUniqueTriggers() {
        NoteDTO n1 = new NoteDTO();
        n1.setNote("Hémoglobine A1C élevée, patient fume");

        NoteDTO n2 = new NoteDTO();
        n2.setNote("Microalbumine détectée");

        NoteDTO n3 = new NoteDTO();
        n3.setNote("Patient FUME beaucoup");

        Integer count = ReflectionTestUtils.invokeMethod(
                diabetesService,
                "countTriggers",
                List.of(n1, n2, n3)
        );

        assertEquals(3, count);
    }

    @Test
    void determineRisk_shouldReturnEarlyOnsetForYoungMaleWith5Triggers() {
        DiabetesRisk risk = ReflectionTestUtils.invokeMethod(
                diabetesService,
                "determineRisk",
                5, 25, "M"
        );

        assertEquals(DiabetesRisk.EARLY_ONSET, risk);
    }

    @Test
    void determineRisk_shouldReturnNoneWhenNoTriggers() {
        DiabetesRisk risk = ReflectionTestUtils.invokeMethod(
                diabetesService,
                "determineRisk",
                0, 50, "F"
        );

        assertEquals(DiabetesRisk.NONE, risk);
    }

    @Test
    void normalizeAccent_shouldRemoveAccents() {
        String result = ReflectionTestUtils.invokeMethod(
                diabetesService,
                "normalizeAccent",
                "Hémoglobine"
        );

        assertEquals("hemoglobine", result);
    }
}
