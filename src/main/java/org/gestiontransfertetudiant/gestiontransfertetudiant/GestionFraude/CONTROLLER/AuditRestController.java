package org.gestiontransfertetudiant.gestiontransfertetudiant.GestionFraude.CONTROLLER;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionTracabilite.SERVICE.AuditService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/audit")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Audit", description = "API de consultation des logs d'audit")
public class AuditRestController {

    private final AuditService auditService;

    @GetMapping("/logs")
    @Operation(summary = "Lister les logs d'audit", description = "Retourne les logs d'audit paginés.")
    public ResponseEntity<?> getLogs(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(auditService.getAuditLogs(PageRequest.of(page, size)));
    }
}
