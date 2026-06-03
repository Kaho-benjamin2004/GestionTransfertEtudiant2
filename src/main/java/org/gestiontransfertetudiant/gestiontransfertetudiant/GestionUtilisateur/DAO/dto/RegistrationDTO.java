package org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto;

import jakarta.validation.Valid;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.request.RegisterUserRequestDTO;


@Data
@NoArgsConstructor
public class RegistrationDTO {
    @Valid
    private RegisterUserRequestDTO registerRequest = new RegisterUserRequestDTO();
}