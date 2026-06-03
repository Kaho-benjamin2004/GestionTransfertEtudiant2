package org.gestiontransfertetudiant.gestiontransfertetudiant.GestionTracabilite.SERVICE;

import lombok.RequiredArgsConstructor;

import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionTracabilite.DAO.entity.AuditLog;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionTracabilite.DAO.repository.AuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Transactional
    public void enregistrerAction(String action, String entiteType, String entiteId,
                                  String details, String utilisateurLogin, String adresseIP) {
        AuditLog log = AuditLog.builder()
                .action(action)
                .entiteType(entiteType)
                .entiteId(entiteId)
                .details(details)
                .utilisateurLogin(utilisateurLogin)
                .adresseIP(adresseIP)
                .dateAction(LocalDateTime.now())
                .build();
        auditLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> getAuditLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }
}