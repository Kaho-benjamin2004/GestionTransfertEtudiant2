package org.gestiontransfertetudiant.gestiontransfertetudiant.GestionTracabilite.CONTROLLER;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionFraude.DAO.dto.*;

import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionFraude.SERVICE.IFraudeMetier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Fraude", description = "API de gestion des règles de fraude et des anomalies")
public class FraudeRestController {

    private final IFraudeMetier fraudeMetier;

    @GetMapping("/admin/fraude/regles")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lister les règles de fraude", description = "Retourne une page des règles de détection.")
    public ResponseEntity<Page<RegleDetectionResponseDTO>> listRules(@RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(fraudeMetier.listerRegles(PageRequest.of(page, size)));
    }

    @PostMapping("/admin/fraude/regles")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Créer une règle de fraude", description = "Ajoute une règle de détection de fraude.")
    public ResponseEntity<RegleDetectionResponseDTO> createRule(@Valid @RequestBody RegleDetectionRequestDTO request) {
        return ResponseEntity.status(201).body(fraudeMetier.creerRegle(request));
    }

    @GetMapping("/admin/fraude/regles/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Voir une règle", description = "Retourne une règle de détection.")
    public ResponseEntity<RegleDetectionResponseDTO> getRule(@PathVariable UUID id) {
        return ResponseEntity.ok(fraudeMetier.getRegle(id));
    }

    @PutMapping("/admin/fraude/regles/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Modifier une règle", description = "Modifie une règle de détection.")
    public ResponseEntity<RegleDetectionResponseDTO> updateRule(@PathVariable UUID id,
                                                                @Valid @RequestBody RegleDetectionRequestDTO request) {
        return ResponseEntity.ok(fraudeMetier.modifierRegle(id, request));
    }

    @DeleteMapping("/admin/fraude/regles/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer une règle", description = "Supprime une règle de détection.")
    public ResponseEntity<Void> deleteRule(@PathVariable UUID id) {
        fraudeMetier.supprimerRegle(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/admin/fraude/verifier")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Vérifier une entité", description = "Déclenche une vérification manuelle d'une entité.")
    public ResponseEntity<DetectionResultDTO> verifyEntity(@RequestParam String entiteType,
                                                           @RequestParam UUID entiteId) {
        return ResponseEntity.ok(fraudeMetier.declencherVerificationManuelle(entiteType, entiteId));
    }

    @GetMapping("/admin/fraude/anomalies")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lister les anomalies", description = "Retourne une page d'anomalies détectées.")
    public ResponseEntity<Page<AnomalieResponseDTO>> listAnomalies(@RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(fraudeMetier.listerAnomalies(PageRequest.of(page, size)));
    }

    @GetMapping("/admin/fraude/anomalies/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Voir une anomalie", description = "Retourne le détail d'une anomalie.")
    public ResponseEntity<AnomalieResponseDTO> getAnomaly(@PathVariable UUID id) {
        return ResponseEntity.ok(fraudeMetier.getAnomalie(id));
    }

    @PostMapping("/admin/fraude/anomalies/{id}/resoudre")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Résoudre une anomalie", description = "Marque une anomalie comme résolue.")
    public ResponseEntity<Void> resolveAnomaly(@PathVariable UUID id,
                                               @RequestParam String commentaire) {
        fraudeMetier.resoudreAnomalie(id, commentaire);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/fraude/journal")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Consulter le journal de fraude", description = "Retourne le journal de détection de fraude.")
    public ResponseEntity<Page<JournalFraudeResponseDTO>> getJournal(@RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(fraudeMetier.consulterJournal(PageRequest.of(page, size)));
    }

    @GetMapping("/admin/fraude/statistiques")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Statistiques fraude", description = "Retourne les indicateurs de fraude.")
    public ResponseEntity<StatistiqueFraudeDTO> getStatistics() {
        return ResponseEntity.ok(fraudeMetier.getStatistiques());
    }
}
