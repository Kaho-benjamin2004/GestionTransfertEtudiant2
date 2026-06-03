package org.gestiontransfertetudiant.gestiontransfertetudiant.GestionFraude.CONTROLLER;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionFraude.DAO.dto.AnomalieRequestDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionFraude.DAO.dto.AnomalieResponseDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionFraude.SERVICE.IFraudeMetier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fraude/signalement")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Signalement fraude", description = "API de signalement manuel d'anomalies")
public class SignalementAnomalieRestController {

    private final IFraudeMetier fraudeMetier;

    @PostMapping
    @Operation(summary = "Signaler une anomalie", description = "Crée un signalement d'anomalie dans le système de fraude.")
    public ResponseEntity<AnomalieResponseDTO> reportAnomaly(@Valid @RequestBody AnomalieRequestDTO request) {
        return ResponseEntity.status(201).body(fraudeMetier.signalerAnomalie(request));
    }
}
