package org.gestiontransfertetudiant.gestiontransfertetudiant.GestionDocument.CONTROLLER;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionDocument.SERVICE.IDocumentMetier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin/documents")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Documents administrateur", description = "API de supervision et de traitement des documents")
public class AdminDocumentRestController {

    private final IDocumentMetier documentMetier;

    @GetMapping("/{id}")
    @Operation(summary = "Vérifier un document", description = "Consulte le document et son état.")
    public ResponseEntity<?> consulterDocument(@PathVariable UUID id) {
        return ResponseEntity.ok(documentMetier.consulterDocument(id));
    }

    @PostMapping("/{id}/signer")
    @Operation(summary = "Signer électroniquement", description = "Ajoute une signature électronique à un document.")
    public ResponseEntity<Void> signerDocument(@PathVariable UUID id,
                                               @RequestParam String signature) {
        documentMetier.signerElectroniquement(id, signature);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/archiver")
    @Operation(summary = "Archiver un document", description = "Archive définitivement un document.")
    public ResponseEntity<Void> archiverDocument(@PathVariable UUID id) {
        documentMetier.archiverDocument(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/detecter-falsification")
    @Operation(summary = "Détecter falsification", description = "Lance une vérification de falsification pour un document.")
    public ResponseEntity<Void> detecterFalsification(@PathVariable UUID id) {
        documentMetier.detecterFalsification(id);
        return ResponseEntity.ok().build();
    }
}
