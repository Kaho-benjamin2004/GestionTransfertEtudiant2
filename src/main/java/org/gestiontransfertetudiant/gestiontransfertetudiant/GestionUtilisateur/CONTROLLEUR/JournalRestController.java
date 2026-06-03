package org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.CONTROLLEUR;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE.JournalService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/journaux")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Journaux", description = "API de consultation des journaux de connexion")
public class JournalRestController {

    private final JournalService journalService;

    @GetMapping
    @Operation(summary = "Lister les journaux", description = "Retourne les journaux de connexion paginés.")
    public ResponseEntity<?> getJournaux(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(journalService.getAllJournaux(PageRequest.of(page, size)));
    }
}
