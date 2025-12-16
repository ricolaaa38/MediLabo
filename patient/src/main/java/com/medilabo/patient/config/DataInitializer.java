package com.medilabo.patient.config;

import com.medilabo.patient.model.PatientInformations;
import com.medilabo.patient.repository.PatientInformationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class DataInitializer {
    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    public CommandLineRunner initialisePatients(PatientInformationRepository patientInformationRepository) {
        return args -> {
            long count = patientInformationRepository.count();
            if (count > 0) {
                log.info("La collection patients contient déjà {} document(s), initialisation ignorée.", count);
                return;
            }

            PatientInformations p1 = new PatientInformations();
            p1.setId("1");
            p1.setFirstName("TestNone");
            p1.setLastName("Test");
            p1.setDateOfBirth(LocalDate.parse("1966-12-31"));
            p1.setGender("F");
            p1.setPostalAddress("1 Brookside St");
            p1.setPhoneNumber("100-222-3333");

            PatientInformations p2 = new PatientInformations();
            p2.setId("2");
            p2.setFirstName("TestBorderline");
            p2.setLastName("Test");
            p2.setDateOfBirth(LocalDate.parse("1945-06-24"));
            p2.setGender("M");
            p2.setPostalAddress("2 High St");
            p2.setPhoneNumber("200-333-4444");

            PatientInformations p3 = new PatientInformations();
            p3.setId("3");
            p3.setFirstName("TestInDanger");
            p3.setLastName("Test");
            p3.setDateOfBirth(LocalDate.parse("2004-06-18"));
            p3.setGender("M");
            p3.setPostalAddress("3 Club Road");
            p3.setPhoneNumber("300-444-5555");


            PatientInformations p4 = new PatientInformations();
            p4.setId("4");
            p4.setFirstName("TestEarlyOnset");
            p4.setLastName("Test");
            p4.setDateOfBirth(LocalDate.parse("2002-06-28"));
            p4.setGender("F");
            p4.setPostalAddress("4 Valley Dr");
            p4.setPhoneNumber("400-555-6666");

            patientInformationRepository.saveAll(List.of(p1, p2, p3, p4));
            log.info("Initialisation patients terminée : 4 patients créés.");
        };
    }
}
