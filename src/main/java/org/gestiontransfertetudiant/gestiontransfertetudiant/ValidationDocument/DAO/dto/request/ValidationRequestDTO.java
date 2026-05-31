package org.gestiontransfertetudiant.gestiontransfertetudiant.ValidationDocument.DAO.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class ValidationRequestDTO {
    @NotNull(message = "L'ID de l'entité à valider est obligatoire")
    private UUID entiteId;
    @NotNull(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "Le type d'entité est obligatoire (ex: DEMANDE_TRANSFERT)")
    private String entiteType;

    @NotNull(message = "L'ID du workflow est obligatoire")
    private UUID workflowId;
}