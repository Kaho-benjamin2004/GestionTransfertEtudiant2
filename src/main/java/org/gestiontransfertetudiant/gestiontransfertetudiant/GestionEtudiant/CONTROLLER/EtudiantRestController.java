package org.gestiontransfertetudiant.gestiontransfertetudiant.GestionEtudiant.CONTROLLER;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionEtudiant.DAO.dto.StatistiquesDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionEtudiant.DAO.dto.reponse.*;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionEtudiant.DAO.dto.request.NoteRequestDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionEtudiant.DAO.dto.request.SanctionRequestDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionEtudiant.SERVICE.IEtudiantMetier;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE.execption.BusinessException;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE.execption.ResourceNotFoundException;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE.securty.RestSecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Étudiant", description = "API de gestion académique des étudiants")
public class EtudiantRestController {

    private final IEtudiantMetier etudiantMetier;

    @GetMapping("/agent/recherche")
    @PreAuthorize("hasRole('AGENT')")
    @Operation(summary = "Rechercher des étudiants", description = "Recherche paginée d'étudiants par critère.")
    public ResponseEntity<Page<EtudiantResponseDTO>> searchStudents(@RequestParam String critere,
                                                                    @RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(etudiantMetier.rechercherEtudiants(critere, PageRequest.of(page, size)));
    }

    @GetMapping("/agent/etudiants/{id}/dossier")
    @PreAuthorize("hasRole('AGENT')")
    @Operation(summary = "Consulter le dossier d'un étudiant", description = "Retourne le dossier complet d'un étudiant.")
    public ResponseEntity<EtudiantResponseDTO> getStudentFile(@PathVariable UUID id) throws ResourceNotFoundException {
        return ResponseEntity.ok(etudiantMetier.consulterDossierComplet(id));
    }

    @PostMapping("/agent/notes/ajouter")
    @PreAuthorize("hasRole('AGENT')")
    @Operation(summary = "Ajouter une note", description = "Enregistre une note pour un étudiant.")
    public ResponseEntity<NoteResponseDTO> addNote(@Valid @RequestBody NoteRequestDTO request) throws BusinessException {
        return ResponseEntity.status(201).body(etudiantMetier.ajouterNote(request));
    }

    @PostMapping("/agent/sanctions/ajouter")
    @PreAuthorize("hasRole('AGENT')")
    @Operation(summary = "Ajouter une sanction", description = "Enregistre une sanction disciplinaire.")
    public ResponseEntity<SanctionResponseDTO> addSanction(@Valid @RequestBody SanctionRequestDTO request) throws BusinessException {
        return ResponseEntity.status(201).body(etudiantMetier.ajouterSanction(request));
    }

    @PostMapping("/agent/parcours/{id}/valider")
    @PreAuthorize("hasRole('AGENT')")
    @Operation(summary = "Valider un parcours", description = "Valide un parcours académique.")
    public ResponseEntity<Void> validatePath(@PathVariable UUID id) throws BusinessException {
        etudiantMetier.validerParcours(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/agent/releve/{etudiantId}/{parcoursId}")
    @PreAuthorize("hasRole('AGENT')")
    @Operation(summary = "Générer un relevé de notes", description = "Retourne le relevé de notes en binaire Base64.")
    public ResponseEntity<byte[]> getTranscript(@PathVariable UUID etudiantId,
                                                @PathVariable UUID parcoursId) throws ResourceNotFoundException {
        return ResponseEntity.ok(etudiantMetier.genererReleveNotes(etudiantId, parcoursId));
    }

    @GetMapping("/commission/comparer")
    @PreAuthorize("hasRole('COMMISSAIRE') or hasRole('ADMIN')")
    @Operation(summary = "Comparer deux programmes", description = "Compare deux unités d'enseignement.")
    public ResponseEntity<ComparaisonProgrammeDTO> comparePrograms(@RequestParam UUID ueId1,
                                                                    @RequestParam UUID ueId2) throws ResourceNotFoundException {
        return ResponseEntity.ok(etudiantMetier.comparerProgrammes(ueId1, ueId2));
    }

    @GetMapping("/etudiant/credits")
    @PreAuthorize("hasRole('ETUDIANT')")
    @Operation(summary = "Consulter les crédits", description = "Retourne le total de crédits acquis par l'étudiant connecté.")
    public ResponseEntity<Integer> getCredits() throws ResourceNotFoundException {
        UUID userId = RestSecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(etudiantMetier.getTotalCreditsAcquis(etudiantMetier.getEtudiantIdByUtilisateurId(userId)));
    }

    @GetMapping("/etudiant/historique")
    @PreAuthorize("hasRole('ETUDIANT')")
    @Operation(summary = "Historique académique", description = "Retourne l'historique académique complet de l'étudiant connecté.")
    public ResponseEntity<EtudiantResponseDTO> getAcademicHistory() throws ResourceNotFoundException {
        UUID userId = RestSecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(etudiantMetier.consulterHistorique(etudiantMetier.getEtudiantIdByUtilisateurId(userId)));
    }

    @GetMapping("/etudiant/sanctions")
    @PreAuthorize("hasRole('ETUDIANT')")
    @Operation(summary = "Consulter les sanctions", description = "Retourne les sanctions disciplinaires de l'étudiant connecté.")
    public ResponseEntity<List<SanctionResponseDTO>> getSanctions() throws ResourceNotFoundException {
        UUID userId = RestSecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(etudiantMetier.consulterSanctions(etudiantMetier.getEtudiantIdByUtilisateurId(userId)));
    }

    @PutMapping("/etudiant/profil/edit")
    @PreAuthorize("hasRole('ETUDIANT')")
    @Operation(summary = "Modifier le profil étudiant", description = "Met à jour les informations personnelles de l'étudiant connecté.")
    public ResponseEntity<Void> updateStudentProfile(@Valid @RequestBody org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.request.ProfilRequestDTO request) throws ResourceNotFoundException {
        UUID userId = RestSecurityUtils.getCurrentUserId();
        etudiantMetier.modifierInformationsPersonnelles(userId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/statistiques/export")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Exporter des statistiques", description = "Retourne les statistiques académiques exportables.")
    public ResponseEntity<StatistiquesDTO> exportStatistics(@RequestParam String filiere,
                                                            @RequestParam String anneeUniversitaire) throws BusinessException {
        return ResponseEntity.ok(etudiantMetier.exporterStatistiques(filiere, anneeUniversitaire));
    }
}
