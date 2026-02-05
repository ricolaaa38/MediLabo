package com.medilabo.evaluation.service;

import com.medilabo.evaluation.exception.NoteNotFoundException;
import com.medilabo.evaluation.exception.PatientNotFoundException;
import com.medilabo.evaluation.model.DiabetesReportDTO;
import com.medilabo.evaluation.model.DiabetesRisk;
import com.medilabo.evaluation.model.NoteDTO;
import com.medilabo.evaluation.model.PatientDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

@Service
public class DiabetesService {

    private final RestTemplate restTemplate;

    @Value("${gateway.url}")
    private String gatewayUrl;


    private static final List<String> TRIGGERS = Arrays.asList(
            "hémoglobine a1c", "microalbumine", "taille", "poids",
            "fume", "anormal", "cholestérol",
            "vertige", "rechute", "réaction", "anticorps"
    );

    public DiabetesService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public DiabetesReportDTO evaluate(String patientId, String authorizationHeader, String userRole) {
        HttpHeaders headers = new HttpHeaders();

        if (authorizationHeader != null) {
            headers.set("Authorization", authorizationHeader);
        }
        if (userRole != null) {
            headers.set("X-User-Role", userRole);
        }

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        PatientDTO patient = fetchPatient(patientId, entity);
        List<NoteDTO> notes = fetchNotes(patientId, entity);

        Integer age = calculateAge(patient.getDateOfBirth());
        String gender = Optional.ofNullable(patient.getGender()).orElse("");
        int triggersCount = countTriggers(notes);
        DiabetesRisk risk = determineRisk(triggersCount, age, gender);
        return new DiabetesReportDTO(age, gender, triggersCount, risk.getLabel());
    }

    private List<NoteDTO> fetchNotes(String patientId, HttpEntity<Void> entity) {
        try {
            String notesUrl = gatewayUrl + "/api/notes/" + patientId;
            ResponseEntity<List<NoteDTO>> notesResp = restTemplate.exchange(
                    notesUrl,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {
                    }
            );
            return notesResp.getBody() != null ? notesResp.getBody() : Collections.emptyList();
        } catch (Exception e) {
            throw new NoteNotFoundException(patientId);
        }
    }

    private PatientDTO fetchPatient(String patientId, HttpEntity<Void> entity) {
        try {
            String url = gatewayUrl + "/api/patients/" + patientId;
            ResponseEntity<PatientDTO> resp = restTemplate.exchange(url, HttpMethod.GET, entity, PatientDTO.class);
            return resp.getBody();
        } catch (Exception e) {
            throw new PatientNotFoundException(patientId);
        }
    }

    private Integer calculateAge(Object dobObj) {
        if (dobObj == null) {
            return null;
        }
        try {
            String dateOnly = dobObj.toString().substring(0, 10);
            LocalDate dob = LocalDate.parse(dateOnly);
            return Period.between(dob, LocalDate.now()).getYears();
        } catch (Exception e) {
            return null;
        }
    }

    private int countTriggers(List<NoteDTO> notes) {
        Set<String> matched = new HashSet<>();
        for (NoteDTO note : notes) {
            if (note == null || note.getNote() == null) {
                continue;
            }
            String text = normalizeAccent(note.getNote());
            for (String trig : TRIGGERS) {
                String normalizedTrigger = normalizeAccent(trig);
                if (text.contains(normalizedTrigger)) {
                    matched.add(normalizedTrigger);
                }
            }
        }
        return matched.size();
    }

    private String normalizeAccent(String input) {
        if (input == null) {
            return "";
        }
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{M}", "");
        return normalized.toLowerCase(Locale.ROOT);
    }

    private boolean isMale(String gender) {
        if (gender == null) {
            return false;
        }
        String g = gender.toLowerCase(Locale.ROOT).trim();
        return g.equals("m") || g.equals("male") || g.equals("masculin") || g.equals("homme");
    }

    private DiabetesRisk determineRisk(int triggers, Integer ageObj, String gender) {
        int age = ageObj == null ? 0 : ageObj;
        boolean over30 = age > 30;
        boolean male = isMale(gender);
        if (triggers == 0) {
            return DiabetesRisk.NONE;
        }
        if (!over30) {
            if (male) {
                if (triggers >= 5) return DiabetesRisk.EARLY_ONSET;
                if (triggers >= 3) return DiabetesRisk.IN_DANGER;
            } else {
                if (triggers >= 7) return DiabetesRisk.EARLY_ONSET;
                if (triggers >= 4) return DiabetesRisk.IN_DANGER;
            }
        } else {
            if (triggers >= 8) return DiabetesRisk.EARLY_ONSET;
            if (triggers == 6 || triggers == 7) return DiabetesRisk.IN_DANGER;
            if (triggers >= 2) return DiabetesRisk.BORDERLINE;
        } return DiabetesRisk.NONE;
    }
}
