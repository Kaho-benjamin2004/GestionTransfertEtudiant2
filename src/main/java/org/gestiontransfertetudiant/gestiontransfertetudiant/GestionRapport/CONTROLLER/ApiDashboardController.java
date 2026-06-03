package org.gestiontransfertetudiant.gestiontransfertetudiant.GestionRapport.CONTROLLER;

import lombok.RequiredArgsConstructor;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionRapport.SERVICE.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class ApiDashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> stats() {
        return ResponseEntity.ok(dashboardService.getStatistics());
    }

    @GetMapping("/transfers")
    public ResponseEntity<Map<String, Object>> transfers() {
        return ResponseEntity.ok(dashboardService.getTransfersLastMonths(6));
    }

    @GetMapping("/etablissements")
    public ResponseEntity<Map<String, Object>> etablissements() {
        return ResponseEntity.ok(dashboardService.getEtablissementDistribution());
    }
}
