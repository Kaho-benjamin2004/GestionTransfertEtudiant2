package org.gestiontransfertetudiant.gestiontransfertetudiant.GestionDocument.CONTROLLER;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionDocument.DAO.dto.request.DocumentRequestDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionDocument.DAO.dto.response.DocumentResponseDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionDocument.SERVICE.IDocumentMetier;

import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE.securty.RestSecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/etudiant/documents")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ETUDIANT')")
@Tag(name = "Documents étudiants", description = "API REST du dépôt et de la consultation de documents")
public class DocumentRestController {

    private final IDocumentMetier documentMetier;

    @PostMapping(value = "/deposer", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Déposer un document", description = "Permet à l'étudiant de déposer un document avec pièce jointe.")
    public ResponseEntity<DocumentResponseDTO> deposerDocument(@Valid @ModelAttribute DocumentRequestDTO request) {
        UUID userId = RestSecurityUtils.getCurrentUserId();
        return ResponseEntity.status(201).body(documentMetier.deposerDocument(request, userId));
    }

    @GetMapping("/mes-documents")
    @Operation(summary = "Lister mes documents", description = "Retourne les documents de l'étudiant connecté en pagination.")
    public ResponseEntity<Page<DocumentResponseDTO>> mesDocuments(@RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "10") int size) {
        UUID userId = RestSecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(documentMetier.listerDocumentsParProprietaire(userId, PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Voir un document", description = "Retourne les métadonnées d'un document.")
    public ResponseEntity<DocumentResponseDTO> voirDocument(@PathVariable UUID id) {
        return ResponseEntity.ok(documentMetier.consulterDocument(id));
    }

    @GetMapping("/{id}/versions")
    @Operation(summary = "Historique des versions", description = "Retourne l'historique des versions du document.")
    public ResponseEntity<Object> historiqueVersions(@PathVariable UUID id) {
        return ResponseEntity.ok(documentMetier.historiqueVersions(id));
    }
}
