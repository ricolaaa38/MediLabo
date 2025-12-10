package com.medilabo.patient.repository;

import com.medilabo.patient.model.PatientInformations;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientInformationRepository extends MongoRepository<PatientInformations, String> {
}
