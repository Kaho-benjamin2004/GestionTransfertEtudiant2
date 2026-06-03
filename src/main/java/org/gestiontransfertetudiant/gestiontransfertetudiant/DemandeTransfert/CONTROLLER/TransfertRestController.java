package org.gestiontransfertetudiant.gestiontransfertetudiant.DemandeTransfert.CONTROLLER;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.gestiontransfertetudiant.gestiontransfertetudiant.DemandeTransfert.DAO.dto.TransfertExportDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.DemandeTransfert.DAO.dto.request.CoursEquivalentRequestDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.DemandeTransfert.DAO.dto.request.DemandeTransfertRequestDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.DemandeTransfert.DAO.dto.request.EquivalenceRequestDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.DemandeTransfert.DAO.dto.request.PieceJustificativeRequestDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.DemandeTransfert.DAO.dto.response.DemandeTransfertResponseDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.DemandeTransfert.SERVICE.ITransfertMetier;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE.execption.BusinessException;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE.execption.ResourceNotFoundException;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE.securty.RestSecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Transferts", description = "API des demandes de transfert et de leur traitement")
public class TransfertRestController {

    private final ITransfertMetier transfertMetier;

    @PostMapping("/etudiant/transferts/creer")
    @PreAuthorize("hasRole('ETUDIANT')")
    @Operation(summary = "Créer une demande de transfert", description = "Crée une nouvelle demande de transfert pour l'étudiant connecté.")
    public ResponseEntity<DemandeTransfertResponseDTO> createDemande(@Valid @RequestBody DemandeTransfertRequestDTO request) throws BusinessException {
        return ResponseEntity.status(201).body(transfertMetier.creerDemande(request));
    }

    @GetMapping("/etudiant/transferts/mes-demandes")
    @PreAuthorize("hasRole('ETUDIANT')")
    @Operation(summary = "Mes demandes", description = "Retourne toutes les demandes de transfert de l'étudiant connecté.")
    public ResponseEntity<List<DemandeTransfertResponseDTO>> myRequests() throws ResourceNotFoundException {
        UUID userId = RestSecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(transfertMetier.consulterMesDemandes(userId));
    }

    @PostMapping("/etudiant/transferts/{id}/soumettre")
    @PreAuthorize("hasRole('ETUDIANT')")
    @Operation(summary = "Soumettre une demande", description = "Soumet une demande de transfert pour examen.")
    public ResponseEntity<Void> submitRequest(@PathVariable UUID id) throws BusinessException {
        UUID userId = RestSecurityUtils.getCurrentUserId();
        transfertMetier.soumettreDemande(id, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/etudiant/transferts/{id}/annuler")
    @PreAuthorize("hasRole('ETUDIANT')")
    @Operation(summary = "Annuler une demande", description = "Annule une demande de transfert en brouillon ou soumise.")
    public ResponseEntity<Void> cancelRequest(@PathVariable UUID id) throws BusinessException {
        UUID userId = RestSecurityUtils.getCurrentUserId();
        transfertMetier.annulerDemande(id, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/etudiant/transferts/{id}/ajouter-pieces")
    @PreAuthorize("hasRole('ETUDIANT')")
    @Operation(summary = "Ajouter une pièce justificative", description = "Ajoute une pièce justificative à une demande de transfert.")
    public ResponseEntity<Void> addPiece(@PathVariable UUID id,
                                         @Valid @RequestBody PieceJustificativeRequestDTO request) throws BusinessException {
        UUID userId = RestSecurityUtils.getCurrentUserId();
        transfertMetier.ajouterPieceJustificative(id, request, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/etudiant/transferts/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Suivre une demande", description = "Retourne l'état d'une demande de transfert.")
    public ResponseEntity<DemandeTransfertResponseDTO> followRequest(@PathVariable UUID id) throws ResourceNotFoundException {
        return ResponseEntity.ok(transfertMetier.suivreDemande(id));
    }

    @GetMapping("/etudiant/transferts/historique")
    @PreAuthorize("hasRole('ETUDIANT')")
    @Operation(summary = "Historique des transferts", description = "Retourne l'historique des transferts pour l'étudiant connecté.")
    public ResponseEntity<List<DemandeTransfertResponseDTO>> history() throws ResourceNotFoundException {
        UUID userId = RestSecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(transfertMetier.historiqueTransferts(userId));
    }

    @PostMapping("/universite/origine/demandes/{id}/verifier-eligibilite")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ORIGINE')")
    @Operation(summary = "Vérifier l'éligibilité", description = "Vérifie l'éligibilité de la demande au niveau de l'université d'origine.")
    public ResponseEntity<Void> verifyEligibility(@PathVariable UUID id) throws BusinessException {
        transfertMetier.verifierEligibilite(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/universite/origine/demandes/{id}/valider")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ORIGINE')")
    @Operation(summary = "Valider à l'origine", description = "Valide ou refuse la demande au niveau de l'université d'origine.")
    public ResponseEntity<Void> validateOrigin(@PathVariable UUID id,
                                               @RequestParam boolean accepte,
                                               @RequestParam(required = false) String commentaire) throws BusinessException {
        transfertMetier.validerParUniversiteOrigine(id, accepte, commentaire, RestSecurityUtils.getCurrentUserId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/universite/accueil/demandes/{id}/analyser-equivalences")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ACCUEIL')")
    @Operation(summary = "Analyser les équivalences", description = "Analyse les équivalences pour une demande de transfert.")
    public ResponseEntity<Void> analyzeEquivalences(@PathVariable UUID id,
                                                    @Valid @RequestBody EquivalenceRequestDTO request) throws BusinessException {
        transfertMetier.analyserEquivalences(id, request, RestSecurityUtils.getCurrentUserId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/universite/accueil/demandes/{id}/valider")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ACCUEIL')")
    @Operation(summary = "Valider à l'accueil", description = "Valide ou refuse la demande au niveau de l'université d'accueil.")
    public ResponseEntity<Void> validateReception(@PathVariable UUID id,
                                                  @RequestParam boolean accepte,
                                                  @RequestParam(required = false) String commentaire) throws BusinessException {
        transfertMetier.validerParUniversiteAccueil(id, accepte, commentaire, RestSecurityUtils.getCurrentUserId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/universite/accueil/demandes/{id}/cours-reprise")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ACCUEIL')")
    @Operation(summary = "Définir les cours à reprendre", description = "Définit la liste des cours à reprendre pour une demande acceptée.")
    public ResponseEntity<Void> defineRecoveryCourses(@PathVariable UUID id,
                                                      @Valid @RequestBody List<CoursEquivalentRequestDTO> coursAReprendre) throws BusinessException {
        transfertMetier.definirCoursReprise(id, coursAReprendre, RestSecurityUtils.getCurrentUserId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/admin/transferts/detecter-doublons")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Détecter doublons", description = "Détecte les doublons pour un numéro d'étudiant.")
    public ResponseEntity<Void> detectDuplicates(@RequestParam String numeroEtudiant) throws BusinessException {
        transfertMetier.detecterDoublons(numeroEtudiant);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/transferts/audit")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Auditer les transferts", description = "Retourne une page d'audit des demandes de transfert.")
    public ResponseEntity<Page<DemandeTransfertResponseDTO>> auditTransfers(@RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(transfertMetier.auditerTransferts(PageRequest.of(page, size)));
    }

    @GetMapping("/admin/transferts/stats")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Statistiques de transferts", description = "Retourne des métriques globales de transfert.")
    public ResponseEntity<?> getStats() {
        return ResponseEntity.ok(
                java.util.Map.of(
                        "totalTransferts", transfertMetier.getTotalTransferts(),
                        "transfertsParStatut", transfertMetier.getTransfertsByStatut(""))
        );
    }

    @GetMapping("/admin/transferts/export")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Exporter les transferts", description = "Retourne les données de transfert analysables pour l'export.")
    public ResponseEntity<List<TransfertExportDTO>> exportTransfers(@RequestParam(required = false) String debut,
                                                                     @RequestParam(required = false) String fin,
                                                                     @RequestParam(required = false) String statut) {
        LocalDate start = debut != null ? LocalDate.parse(debut) : null;
        LocalDate end = fin != null ? LocalDate.parse(fin) : null;
        return ResponseEntity.ok(transfertMetier.getTransfertsForExport(start, end, statut));
    }
}
