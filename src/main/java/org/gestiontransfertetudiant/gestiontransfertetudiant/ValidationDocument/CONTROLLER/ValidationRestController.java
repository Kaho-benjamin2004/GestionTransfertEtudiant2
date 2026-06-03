package org.gestiontransfertetudiant.gestiontransfertetudiant.ValidationDocument.CONTROLLER;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE.execption.BusinessException;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE.securty.RestSecurityUtils;
import org.gestiontransfertetudiant.gestiontransfertetudiant.ValidationDocument.DAO.dto.request.ValidationRequestDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.ValidationDocument.DAO.dto.request.WorkflowRequestDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.ValidationDocument.DAO.dto.response.ValidationResponseDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.ValidationDocument.DAO.dto.response.WorkflowResponseDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.ValidationDocument.SERVICE.IValidationMetier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Validation", description = "API de gestion des workflows et des validations")
public class ValidationRestController {

    private final IValidationMetier validationMetier;

    @PostMapping("/validations/soumettre")
    @PreAuthorize("hasAnyRole('AGENT', 'ETUDIANT')")
    @Operation(summary = "Soumettre une validation", description = "Soumet une entité à un workflow de validation.")
    public ResponseEntity<ValidationResponseDTO> submitValidation(@Valid @RequestBody ValidationRequestDTO request) throws BusinessException {
        UUID userId = RestSecurityUtils.getCurrentUserId();
        return ResponseEntity.status(201).body(validationMetier.soumettreValidation(request, userId));
    }

    @GetMapping("/validations/historique")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    @Operation(summary = "Historique des validations", description = "Retourne l'historique des validations pour une entité.")
    public ResponseEntity<List<ValidationResponseDTO>> history(@RequestParam UUID entiteId,
                                                               @RequestParam String entiteType) {
        return ResponseEntity.ok(validationMetier.getHistoriqueValidations(entiteId, entiteType));
    }

    @PostMapping("/admin/validations/workflow/creer")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Créer un workflow", description = "Définit un workflow de validation.")
    public ResponseEntity<WorkflowResponseDTO> createWorkflow(@Valid @RequestBody WorkflowRequestDTO request) throws BusinessException {
        UUID userId = RestSecurityUtils.getCurrentUserId();
        return ResponseEntity.status(201).body(validationMetier.definirWorkflow(request, userId));
    }

    @PostMapping("/admin/validations/{id}/annuler")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Annuler une validation", description = "Annule une validation existante.")
    public ResponseEntity<Void> cancelValidation(@PathVariable UUID id) throws BusinessException {
        validationMetier.annulerValidation(id, RestSecurityUtils.getCurrentUserId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/admin/validations/{id}/attribuer-validateur")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Attribuer un validateur", description = "Assigne un valideur à une validation.")
    public ResponseEntity<Void> assignValidator(@PathVariable UUID id,
                                                @RequestParam UUID valideurId) throws BusinessException {
        validationMetier.attribuerValidateur(id, valideurId, RestSecurityUtils.getCurrentUserId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/admin/validations/{id}/suspendre")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Suspendre une validation", description = "Suspend une validation en cours.")
    public ResponseEntity<Void> suspendValidation(@PathVariable UUID id,
                                                  @RequestParam String raison) throws BusinessException {
        validationMetier.suspendreValidation(id, RestSecurityUtils.getCurrentUserId(), raison);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/admin/validations/{id}/reprendre")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reprendre une validation", description = "Reprend une validation suspendue.")
    public ResponseEntity<Void> resumeValidation(@PathVariable UUID id) throws BusinessException {
        validationMetier.reprendreValidation(id, RestSecurityUtils.getCurrentUserId());
        return ResponseEntity.ok().build();
    }
}
