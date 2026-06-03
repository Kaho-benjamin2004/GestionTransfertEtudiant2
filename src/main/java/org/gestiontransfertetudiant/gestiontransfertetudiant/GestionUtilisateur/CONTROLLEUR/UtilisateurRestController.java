package org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.CONTROLLEUR;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.request.UtilisateurRequestDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.request.UtilisateurUpdateRequestDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.response.*;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE.RoleService;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE.UtilisateurService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/utilisateurs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Utilisateurs", description = "API de gestion des comptes utilisateurs")
public class UtilisateurRestController {

    private final UtilisateurService utilisateurService;
    private final RoleService roleService;

    @GetMapping
    @Operation(summary = "Lister les utilisateurs", description = "Retourne une page d'utilisateurs simples.")
    public ResponseEntity<Page<UtilisateurSimpleResponseDTO>> listUtilisateurs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(utilisateurService.getAllUsers(PageRequest.of(page, size)));
    }

    @PostMapping
    @Operation(summary = "Créer un utilisateur", description = "Crée un utilisateur et lui assigne des rôles.")
    public ResponseEntity<MessageResponseDTO> createUtilisateur(@RequestBody UtilisateurRequestDTO request) {
        utilisateurService.createUser(request);
        return ResponseEntity.ok(MessageResponseDTO.builder()
                .message("Utilisateur créé avec succès.")
                .success(true)
                .statusCode(201)
                .build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Afficher un utilisateur", description = "Retourne le détail d'un utilisateur.")
    public ResponseEntity<UtilisateurDetailResponseDTO> getUtilisateur(@PathVariable UUID id) {
        return ResponseEntity.ok(utilisateurService.getUserById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un utilisateur", description = "Modifie les informations principales d'un utilisateur.")
    public ResponseEntity<UtilisateurResponseDTO> updateUtilisateur(@PathVariable UUID id,
                                                                    @RequestBody UtilisateurUpdateRequestDTO request) {
        return ResponseEntity.ok(utilisateurService.updateUser(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un utilisateur", description = "Supprime un utilisateur par son identifiant.")
    public ResponseEntity<MessageResponseDTO> deleteUtilisateur(@PathVariable UUID id) {
        utilisateurService.deleteUser(id);
        return ResponseEntity.ok(MessageResponseDTO.builder()
                .message("Utilisateur supprimé.")
                .success(true)
                .statusCode(200)
                .build());
    }

    @GetMapping("/{id}/roles")
    @Operation(summary = "Rôles de l'utilisateur", description = "Retourne les rôles assignés à un utilisateur.")
    public ResponseEntity<List<RoleResponseDTO>> getRolesUtilisateur(@PathVariable UUID id) {
        return ResponseEntity.ok(utilisateurService.getUserRoles(id));
    }

    @GetMapping("/roles")
    @Operation(summary = "Lister les rôles disponibles", description = "Retourne la liste de tous les rôles.")
    public ResponseEntity<List<RoleResponseDTO>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }
}
