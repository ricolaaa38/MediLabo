package com.medilabo.patient.controller;

import com.medilabo.patient.model.PatientInformations;
import com.medilabo.patient.service.PatientInformationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientInformationController {

    private final PatientInformationService patientInformationService;

    public PatientInformationController(PatientInformationService patientInformationService) {
        this.patientInformationService = patientInformationService;
    }

    @GetMapping
    public List<PatientInformations> getAllPatients() {
        return patientInformationService.findAll();
    }

    @GetMapping("/{id}")
    public PatientInformations getPatientInformations(@PathVariable String id) {
        return patientInformationService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PatientInformations addPatient(@Valid @RequestBody PatientInformations patientInformations) {
        return patientInformationService.create(patientInformations);
    }

    @PutMapping("/{id}")
    public PatientInformations updatePatient(@PathVariable String id, @Valid @RequestBody PatientInformations patientInformations) {
        return patientInformationService.update(id, patientInformations);
    }
}
