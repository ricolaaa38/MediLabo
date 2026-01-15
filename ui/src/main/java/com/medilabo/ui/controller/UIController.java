package com.medilabo.ui.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@Controller
@RequestMapping("/ui")
public class UIController {

    private static final Logger log = LoggerFactory.getLogger(UIController.class);

    private final RestTemplate restTemplate;

    public UIController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${patient.url:http://localhost:8081}")
    private String patientUrl;

    @Value("${note.url:http://localhost:8081}")
    private String noteUrl;

    @GetMapping({"", "/"})
    public String patients(Model model, HttpServletRequest request) {
        String url = UriComponentsBuilder.fromHttpUrl(patientUrl).path("/api/patients").toUriString();
        log.info("UI -> appel patient: url={}", url);
        HttpHeaders headers = new HttpHeaders();

        String auth = request.getHeader("Authorization");
        if (auth != null) {
            headers.set("Authorization", auth);
        }

        String userRole = request.getHeader("X-User-Role");
        if (userRole != null) {
            headers.set("X-User-Role", userRole);
        }
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<List> resp = restTemplate.exchange(url, HttpMethod.GET, entity, List.class);
            List<Map<String, Object>> patients = resp.getBody();
            model.addAttribute("patients", patients);
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            // 4xx du service patient -> message plus propre
            log.warn("Erreur d'appel au service patient {} : {}", url, e.getStatusCode());
            model.addAttribute("patients", java.util.Collections.emptyList());
            model.addAttribute("errorMessage", "Impossible de récupérer la liste des patients (" + e.getStatusCode() + ").");
        }
        return "patients";
    }

    @GetMapping("/patients")
    public String patientsList(Model model, HttpServletRequest request) {
        return patients(model, request);
    }

    @GetMapping({"/patients/{id}"})
    public String patientDetail(@PathVariable String id, Model model, HttpServletRequest request) {
        String url = UriComponentsBuilder.fromHttpUrl(patientUrl).path("/api/patients/").path(id).toUriString();

        HttpHeaders headers = new HttpHeaders();

        String auth = request.getHeader("Authorization");
        if (auth != null) {
            headers.set("Authorization", auth);
        }

        String userRole = request.getHeader("X-User-Role");
        if (userRole != null) {
            headers.set("X-User-Role", userRole);
        }
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> resp = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        Map<String, Object> patient = resp.getBody();

        model.addAttribute("patient", patient);
        model.addAttribute("userRole", userRole);

        if ("PRATICIEN".equals(userRole)) {
            try {
                String notesUrl = UriComponentsBuilder.fromHttpUrl(noteUrl)
                        .path("/api/notes/")
                        .path(id)
                        .toUriString();

                HttpHeaders notesHeaders = new HttpHeaders();

                String notesAuth = request.getHeader("Authorization");
                if (notesAuth != null) {
                    notesHeaders.set("Authorization", notesAuth);
                }
                HttpEntity<Void> notesEntity = new HttpEntity<>(notesHeaders);

                ResponseEntity<List> notesResp = restTemplate.exchange(notesUrl, HttpMethod.GET, notesEntity, List.class);
                List<Map<String, Object>> notes = notesResp.getBody();
                model.addAttribute("notes", notes != null ? notes : Collections.emptyList());
            } catch (Exception e) {
                log.warn("Impossible de récupérer les notes pour patient {} : {}", id, e.getMessage());
                model.addAttribute("notes", Collections.emptyList());
            }
        } else {
            model.addAttribute("notes", Collections.emptyList());
        }
        return "patient-details";
    }

    @PostMapping("/patients/{id}")
    public String updatePatient(@PathVariable String id, @RequestParam Map<String, String> params, HttpServletRequest request) {
        String url = UriComponentsBuilder.fromHttpUrl(patientUrl).path("/api/patients/").path(id).toUriString();

        Map<String, Object> payload = new HashMap<>();

        payload.put("id", id);
        payload.put("firstName", params.get("firstName"));
        payload.put("lastName", params.get("lastName"));

        payload.put("dateOfBirth", params.get("dateOfBirth"));
        payload.put("gender", params.get("gender"));
        payload.put("postalAddress", params.get("postalAddress"));
        payload.put("phoneNumber", params.get("phoneNumber"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String auth = request.getHeader("Authorization");
        if (auth != null) {
            headers.set("Authorization", auth);
        }

        String userRole = request.getHeader("X-User-Role");
        if (userRole != null) {
            headers.set("X-User-Role", userRole);
        }
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        log.info("UIController PUT patient -> id={} url={} roleForwarded={}", id, url, userRole != null);

        restTemplate.exchange(url, HttpMethod.PUT, entity, Map.class);

        return "redirect:http://localhost:8081/ui/patients/" + id;
    }

    @PostMapping("/patients/{id}/notes")
    public String addNote(@PathVariable String id,
                          @RequestParam String note,
                          @RequestParam(required = false) String patientName,
                          HttpServletRequest request) {
        String userRole = request.getHeader("X-User-Role");
        if (!"PRATICIEN".equals(userRole)) {
            return "redirect:/patients/" + id;
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("patientId", id);
        payload.put("patientName", patientName != null ? patientName : "");
        payload.put("note", note);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String auth = request.getHeader("Authorization");
        if (auth != null) {
            headers.set("Authorization", auth);
        }

       if  (userRole != null) {
            headers.set("X-User-Role", userRole);
       }

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        try {
            String target = UriComponentsBuilder.fromHttpUrl(noteUrl).path("/api/notes").toUriString();
            restTemplate.postForEntity(target, entity, Map.class);
        } catch (Exception e) {
            log.warn("Échec POST note patient {} : {}", id, e.getMessage());
        }

        return "redirect:http://localhost:8081/ui/patients/" + id;
    }
}
