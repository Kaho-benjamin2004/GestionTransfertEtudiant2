package org.gestiontransfertetudiant.gestiontransfertetudiant.GestionNotifiactions.CONTROLLER;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionNotifiactions.DAO.dto.request.PreferenceNotificationRequestDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionNotifiactions.DAO.dto.response.NotificationResponseDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionNotifiactions.DAO.dto.response.PreferenceNotificationResponseDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionNotifiactions.SERVICE.INotificationMetier;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE.securty.RestSecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Notifications", description = "API de lecture et d'administration des notifications utilisateur")
public class NotificationRestController {

    private final INotificationMetier notificationMetier;

    @GetMapping
    @Operation(summary = "Lister les notifications", description = "Retourne les notifications de l'utilisateur connecté.")
    public ResponseEntity<Page<NotificationResponseDTO>> listNotifications(@RequestParam(defaultValue = "0") int page,
                                                                           @RequestParam(defaultValue = "20") int size) {
        UUID userId = RestSecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(notificationMetier.getNotificationsByUser(userId, PageRequest.of(page, size)));
    }

    @PostMapping("/{id}/lire")
    @Operation(summary = "Marquer une notification comme lue", description = "Met une notification en état lue.")
    public ResponseEntity<Void> markAsRead(@PathVariable UUID id) {
        UUID userId = RestSecurityUtils.getCurrentUserId();
        notificationMetier.marquerCommeLue(id, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/archiver")
    @Operation(summary = "Archiver une notification", description = "Archive une notification pour l'utilisateur connecté.")
    public ResponseEntity<Void> archiveNotification(@PathVariable UUID id) {
        UUID userId = RestSecurityUtils.getCurrentUserId();
        notificationMetier.archiverNotification(id, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une notification", description = "Supprime définitivement une notification.")
    public ResponseEntity<Void> deleteNotification(@PathVariable UUID id) {
        UUID userId = RestSecurityUtils.getCurrentUserId();
        notificationMetier.supprimerNotification(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/preferences")
    @Operation(summary = "Voir les préférences", description = "Retourne les préférences de notification de l'utilisateur.")
    public ResponseEntity<List<PreferenceNotificationResponseDTO>> getPreferences() {
        UUID userId = RestSecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(notificationMetier.getPreferencesByUser(userId));
    }

    @PostMapping("/preferences")
    @Operation(summary = "Mettre à jour les préférences", description = "Enregistre les préférences de notification de l'utilisateur.")
    public ResponseEntity<Void> savePreferences(@Valid @RequestBody PreferenceNotificationRequestDTO request) {
        UUID userId = RestSecurityUtils.getCurrentUserId();
        notificationMetier.configurerPreference(userId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/non-lues/count")
    @Operation(summary = "Compter les notifications non lues", description = "Retourne le nombre de notifications non lues pour l'utilisateur connecté.")
    public ResponseEntity<Long> countUnread() {
        UUID userId = RestSecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(notificationMetier.getNombreNonLues(userId));
    }
}
