package org.gestiontransfertetudiant.gestiontransfertetudiant.ValidationDocument.DAO.repository;

import jakarta.validation.constraints.NotBlank;
import org.gestiontransfertetudiant.gestiontransfertetudiant.ValidationDocument.DAO.entity.Validation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ValidationWorkflowRepository extends JpaRepository<Validation, UUID> {
    long countByStatut(String statut);
    List<Validation> findByEntiteIdAndEntiteType(UUID entiteId, String entiteType);
    Optional<Validation> findFirstByEntiteIdAndEntiteTypeOrderByDateSoumissionDesc(UUID entiteId, String entiteType);

    Optional<Object> findByNom(@NotBlank(message = "Le nom du workflow est obligatoire") String nom);
}

