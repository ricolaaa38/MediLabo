package com.medilabo.patient.repository;

import com.medilabo.patient.model.PatientInformations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientInformationRepository extends JpaRepository<PatientInformations, Integer> {
}
