package com.medilabo.evaluation.controller;

import com.medilabo.evaluation.model.DiabetesReportDTO;
import com.medilabo.evaluation.service.DiabetesService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/evaluations")
public class EvaluationController {

    private final DiabetesService diabetesService;

    public EvaluationController(DiabetesService diabetesService) {
        this.diabetesService = diabetesService;
    }

    @GetMapping("/{patientId}")
    public ResponseEntity<?> getDiabetesReport(@PathVariable String patientId, HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        String userRole = request.getHeader("X-User-Role");
        DiabetesReportDTO report = diabetesService.evaluate(patientId, auth, userRole);
        if (report == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(report);
    }
}
