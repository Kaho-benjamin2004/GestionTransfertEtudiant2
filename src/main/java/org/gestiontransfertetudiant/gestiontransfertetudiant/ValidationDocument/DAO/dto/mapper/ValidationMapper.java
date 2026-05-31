package org.gestiontransfertetudiant.gestiontransfertetudiant.ValidationDocument.DAO.dto.mapper;

import org.gestiontransfertetudiant.gestiontransfertetudiant.ValidationDocument.DAO.dto.request.ValidationRequestDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.ValidationDocument.DAO.dto.response.ValidationResponseDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.ValidationDocument.DAO.entity.Validation;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class ValidationMapper {

    public static ValidationResponseDTO toDTO(Validation validation) {
        if (validation == null) return null;
        return ValidationResponseDTO.builder()
                .id(validation.getId())
                .entiteId(validation.getEntiteId())
                .entiteType(validation.getEntiteType())
                .etape(validation.getEtape())
                .statut(validation.getStatut())
                .dateSoumission(validation.getDateSoumission())
                .dateValidation(validation.getDateValidation())
                .commentaire(validation.getCommentaire())
                .valideurId(validation.getValideurId())
                .historiques(validation.getHistoriques() != null ?
                        validation.getHistoriques().stream()
                        .map(HistoriqueValidationMapper::toDTO)
                        .collect(Collectors.toList()) : null)
                .build();
    }

    public static Validation toEntity(ValidationRequestDTO requestDTO, Validation workflow) {
        if (requestDTO == null) return null;
        Validation validation = new Validation();
        validation.setEntiteId(requestDTO.getEntiteId());
        validation.setEntiteType(requestDTO.getEntiteType());
        validation.setNom(requestDTO.getNom());
        validation.setEtape(1); // première étape
        validation.setStatut("EN_ATTENTE");
        validation.setDateSoumission(LocalDateTime.now());
        // workflow non directement stocké dans Validation, mais utile pour déterminer les étapes
        return validation;
    }
}
