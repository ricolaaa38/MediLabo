package com.medilabo.notes.config;

import com.medilabo.notes.model.PatientNotes;
import com.medilabo.notes.repository.NoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    public CommandLineRunner initialisePatientsNotes(NoteRepository noteRepository) {
        return args -> {
            Long count = noteRepository.count();
            if (count > 0) {
                log.info("La collection notes contient déjà {} document(s), initialisation ignorée.", count);
                return;
            }

            PatientNotes n1 = new PatientNotes();
            n1.setPatientId("1");
            n1.setPatientName("TestNone");
            n1.setNote("Le patient déclare qu'il 'se sent très bien' Poids égal ou inférieur au poids recommandé");

            PatientNotes n2 = new PatientNotes();
            n2.setPatientId("2");
            n2.setPatientName("TestBorderline");
            n2.setNote("Le patient déclare qu'il ressent beaucoup de stress au travail Il se plaint également que son audition est anormale dernièrement");

            PatientNotes n3 = new PatientNotes();
            n3.setPatientId("2");
            n3.setPatientName("TestBorderline");
            n3.setNote("Le patient déclare avoir fait une réaction aux médicaments au cours des 3 derniers mois Il remarque également que son audition continue d'être anormale");

            PatientNotes n4 = new PatientNotes();
            n4.setPatientId("3");
            n4.setPatientName("TestInDanger");
            n4.setNote("Le patient déclare qu'il fume depuis peu");

            PatientNotes n5 = new PatientNotes();
            n5.setPatientId("3");
            n5.setPatientName("TestInDanger");
            n5.setNote("Le patient déclare qu'il est fumeur et qu'il a cessé de fumer l'année dernière Il se plaint également de crises d’apnée respiratoire anormales Tests de laboratoire indiquant un taux de cholestérol LDL élevé");

            PatientNotes n6 = new PatientNotes();
            n6.setPatientId("4");
            n6.setPatientName("TestEarlyOnset");
            n6.setNote("Le patient déclare qu'il lui est devenu difficile de monter les escaliers Il se plaint également d’être essoufflé Tests de laboratoire indiquant que les anticorps sont élevés Réaction aux médicaments");

            PatientNotes n7 = new PatientNotes();
            n7.setPatientId("4");
            n7.setPatientName("TestEarlyOnset");
            n7.setNote("Le patient déclare qu'il a mal au dos lorsqu'il reste assis pendant longtemps");

            PatientNotes n8 = new PatientNotes();
            n8.setPatientId("4");
            n8.setPatientName("TestEarlyOnset");
            n8.setNote("Le patient déclare avoir commencé à fumer depuis peu Hémoglobine A1C supérieure au niveau recommandé");

            PatientNotes n9 = new PatientNotes();
            n9.setPatientId("4");
            n9.setPatientName("TestEarlyOnset");
            n9.setNote("Taille, Poids, Cholestérol, Vertige et Réaction");

            noteRepository.saveAll(List.of(n1, n2, n3, n4, n5, n6, n7, n8, n9));
            log.info("Initialisation notes terminée : 9 notes créées.");
        };
    }
}
