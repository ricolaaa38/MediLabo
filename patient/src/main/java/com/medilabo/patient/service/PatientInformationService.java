package com.medilabo.patient.service;

import com.medilabo.patient.model.PatientInformations;
import com.medilabo.patient.repository.PatientInformationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PatientInformationService {

    @Autowired
    private final PatientInformationRepository patientInformationRepository;

    public PatientInformationService(PatientInformationRepository patientInformationRepository) {
        this.patientInformationRepository = patientInformationRepository;
    }

    public List<PatientInformations> findAll() {
        return patientInformationRepository.findAll();
    }

    public PatientInformations findById(String id) {
        return patientInformationRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient non trouvé !"));
    }

    public PatientInformations create(PatientInformations patientInformations) {
        return patientInformationRepository.save(patientInformations);
    }

    public PatientInformations update(String id, PatientInformations updatedPatientInformations) {
        PatientInformations existingPatientInformations = patientInformationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient non trouvé !"));

        existingPatientInformations.setFirstName(updatedPatientInformations.getFirstName());
        existingPatientInformations.setLastName(updatedPatientInformations.getLastName());
        existingPatientInformations.setDateOfBirth(updatedPatientInformations.getDateOfBirth());
        existingPatientInformations.setGender(updatedPatientInformations.getGender());
        existingPatientInformations.setPostalAddress(updatedPatientInformations.getPostalAddress());
        existingPatientInformations.setPhoneNumber(updatedPatientInformations.getPhoneNumber());

        return patientInformationRepository.save(existingPatientInformations);
    }
}
