package org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.CONTROLLEUR;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.request.PermissionRequestDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.response.PermissionResponseDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE.PermissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/permissions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Permissions", description = "API de gestion des permissions d'accès")
public class PermissionRestController {

    private final PermissionService permissionService;

    @GetMapping
    @Operation(summary = "Lister les permissions", description = "Retourne la liste de toutes les permissions.")
    public ResponseEntity<List<PermissionResponseDTO>> getAllPermissions() {
        return ResponseEntity.ok(permissionService.getAllPermissions());
    }

    @PostMapping
    @Operation(summary = "Créer une permission", description = "Ajoute une permission métier.")
    public ResponseEntity<PermissionResponseDTO> createPermission(@Valid @RequestBody PermissionRequestDTO request) {
        return ResponseEntity.status(201).body(permissionService.createPermission(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détails d'une permission", description = "Retourne les informations d'une permission.")
    public ResponseEntity<PermissionResponseDTO> getPermission(@PathVariable UUID id) {
        return ResponseEntity.ok(permissionService.getPermission(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une permission", description = "Supprime une permission par identifiant.")
    public ResponseEntity<Void> deletePermission(@PathVariable UUID id) {
        permissionService.deletePermission(id);
        return ResponseEntity.noContent().build();
    }
}
