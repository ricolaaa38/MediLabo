package com.medilabo.evaluation.service;

import com.medilabo.evaluation.exception.NoteNotFoundException;
import com.medilabo.evaluation.exception.PatientNotFoundException;
import com.medilabo.evaluation.model.DiabetesReportDTO;
import com.medilabo.evaluation.model.DiabetesRisk;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${patient.url:http://localhost:8081}")
    private String patientUrl;

    @Value("${note.url:http://localhost:8081}")
    private String noteUrl;

    private static final List<String> TRIGGERS = Arrays.asList(
            "hémoglobine a1c", "microalbumine", "taille", "poids",
            "fumeur", "fumeuse", "fumer", "anormal", "cholestérol",
            "vertiges", "rechute", "réaction", "anticorps"
    );

    public DiabetesService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public DiabetesReportDTO evaluate(String patientId, String authorizationHeader, String userRole) {
        HttpHeaders headers = new HttpHeaders();

        if (authorizationHeader != null) headers.set("Authorization", authorizationHeader);
        if (userRole != null) headers.set("X-User-Role", userRole);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        Map<String, Object> patient = fetchPatient(patientId, entity);

        List<Map<String, Object>> notes = fetchNotes(patientId, entity);

        String fullName = (Objects.toString(patient.get("firstName"), "") + " " + Objects.toString(patient.get("lastName"), "")).trim();
        Integer age = computeAge(patient.get("dateOfBirth")); String gender = Objects.toString(patient.get("gender"), "");
        int triggersCount = countTriggers(notes);
        DiabetesRisk risk = determineRisk(triggersCount, age, gender);
        return new DiabetesReportDTO(patientId, fullName, age, gender, triggersCount, risk.getLabel());
    }

    private List<Map<String, Object>> fetchNotes(String patientId, HttpEntity<Void> entity) {
        try {
            String notesUrl = noteUrl + "/api/notes/" + patientId;
            ResponseEntity<List> notesResp = restTemplate.exchange(notesUrl, HttpMethod.GET, entity, List.class);
            return notesResp.getBody() != null ? notesResp.getBody() : Collections.emptyList();
        } catch (Exception e) {
            throw new NoteNotFoundException(patientId);
        }
    }

    private Map<String, Object> fetchPatient(String patientId, HttpEntity<Void> entity) {

        try {
            String url = patientUrl + "/api/patients/" + patientId;
            ResponseEntity<Map> resp = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            return resp.getBody();
        } catch (Exception e) {
            throw new PatientNotFoundException(patientId);
        }
    }

    private Integer computeAge(Object dobObj) {
        if (dobObj == null) return null;
        try {
            String dateOnly = dobObj.toString().substring(0, 10);
            LocalDate dob = LocalDate.parse(dateOnly);
            return Period.between(dob, LocalDate.now()).getYears();
        } catch (Exception e) {
            return null;
        }
    }

    private int countTriggers(List<Map<String, Object>> notes) {
        Set<String> matched = new HashSet<>();
        for (Map<String, Object> n : notes) {
            Object noteObj = n.get("note");
            if (noteObj == null) continue;
            String text = normalize(noteObj.toString());
            for (String trig : TRIGGERS) {
                String normalizedTrigger = normalize(trig);
                if (text.contains(normalizedTrigger)) {
                    matched.add(normalizedTrigger);
                }
            }
        }
        return matched.size();
    }

    private String normalize(String input) {
        if (input == null) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{M}", "");
        return normalized.toLowerCase(Locale.ROOT);
    }


    private boolean isMale(String genderRaw) {
        if (genderRaw == null) return false;
        String g = genderRaw.toLowerCase(Locale.ROOT).trim();
        return g.equals("m") || g.equals("male") || g.equals("masculin") || g.equals("homme");
    }

    private DiabetesRisk determineRisk(int triggers, Integer ageObj, String genderRaw) {
        int age = ageObj == null ? 0 : ageObj;
        boolean over30 = age > 30;
        boolean male = isMale(genderRaw);
        if (triggers == 0) return DiabetesRisk.NONE;
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
            if (triggers >= 2 && triggers <= 5) return DiabetesRisk.BORDERLINE;
        } return DiabetesRisk.NONE;
    }
}
