package org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.CONTROLLEUR;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.request.RoleRequestDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.response.RoleDetailResponseDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.response.RoleResponseDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/roles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Rôles", description = "API de gestion des rôles et de leurs permissions")
public class RoleRestController {

    private final RoleService roleService;

    @GetMapping
    @Operation(summary = "Lister les rôles", description = "Retourne la liste de tous les rôles.")
    public ResponseEntity<List<RoleResponseDTO>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @PostMapping
    @Operation(summary = "Créer un rôle", description = "Ajoute un rôle métier avec ses permissions.")
    public ResponseEntity<RoleResponseDTO> createRole(@Valid @RequestBody RoleRequestDTO request) {
        return ResponseEntity.status(201).body(roleService.createRole(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détails d'un rôle", description = "Retourne les données d'un rôle spécifique.")
    public ResponseEntity<RoleDetailResponseDTO> getRole(@PathVariable UUID id) {
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un rôle", description = "Met à jour les informations d'un rôle.")
    public ResponseEntity<RoleResponseDTO> updateRole(@PathVariable UUID id,
                                                      @Valid @RequestBody RoleRequestDTO request) {
        return ResponseEntity.ok(roleService.updateRole(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un rôle", description = "Supprime un rôle par son identifiant.")
    public ResponseEntity<Void> deleteRole(@PathVariable UUID id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}
