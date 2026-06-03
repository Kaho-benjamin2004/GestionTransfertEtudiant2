package org.gestiontransfertetudiant.gestiontransfertetudiant.GestionNotifiactions.CONTROLLER;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionNotifiactions.DAO.dto.request.NotificationRequestDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionNotifiactions.SERVICE.INotificationMetier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/notifications")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Notifications admin", description = "API d'envoi et de gestion des notifications")
public class AdminNotificationRestController {

    private final INotificationMetier notificationMetier;

    @PostMapping("/envoyer")
    @Operation(summary = "Envoyer une notification", description = "Envoie une notification à un ensemble de destinataires.")
    public ResponseEntity<Void> sendNotification(@Valid @RequestBody NotificationRequestDTO request) {
        notificationMetier.envoyerNotification(request);
        return ResponseEntity.status(201).build();
    }
}
