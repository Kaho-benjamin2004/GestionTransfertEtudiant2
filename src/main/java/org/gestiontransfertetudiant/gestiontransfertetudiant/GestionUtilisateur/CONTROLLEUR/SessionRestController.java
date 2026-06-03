package org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.CONTROLLEUR;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE.SessionService;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.response.SessionResponseDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE.securty.RestSecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Sessions", description = "API de consultation des sessions actives")
public class SessionRestController {

    private final SessionService sessionService;

    @GetMapping
    @Operation(summary = "Lister les sessions", description = "Retourne les sessions actives de l'utilisateur connecté.")
    public ResponseEntity<List<SessionResponseDTO>> getSessions() {
        UUID userId = RestSecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(sessionService.getSessionsByUser(userId));
    }
}
