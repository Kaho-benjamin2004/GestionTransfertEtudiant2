package org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.CONTROLLEUR;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.request.ProfilRequestDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.response.UtilisateurDetailResponseDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE.UtilisateurService;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE.securty.RestSecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Profil", description = "API de gestion du profil utilisateur connecté")
public class ProfileRestController {

    private final UtilisateurService utilisateurService;

    @GetMapping
    @Operation(summary = "Voir son profil", description = "Retourne les informations du profil de l'utilisateur connecté.")
    public ResponseEntity<UtilisateurDetailResponseDTO> getProfile() {
        UUID userId = RestSecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(utilisateurService.getUserById(userId));
    }

    @PutMapping("/edit")
    @Operation(summary = "Modifier son profil", description = "Met à jour les informations personnelles de l'utilisateur connecté.")
    public ResponseEntity<Void> updateProfile(@Valid @RequestBody ProfilRequestDTO request) {
        UUID userId = RestSecurityUtils.getCurrentUserId();
        utilisateurService.updateProfil(userId, request);
        return ResponseEntity.ok().build();
    }
}
