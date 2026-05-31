package org.gestiontransfertetudiant.gestiontransfertetudiant.ValidationDocument.DAO.repository;

import org.gestiontransfertetudiant.gestiontransfertetudiant.ValidationDocument.DAO.entity.Workflow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WorkFlowRepository extends JpaRepository<Workflow, UUID> {
}
