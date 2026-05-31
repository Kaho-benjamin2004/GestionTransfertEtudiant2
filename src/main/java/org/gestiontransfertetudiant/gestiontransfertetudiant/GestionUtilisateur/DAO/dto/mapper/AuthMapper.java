package org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.mapper;

import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.response.AuthResponseDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.response.RefreshTokenResponseDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.entity.Utilisateur;

import java.util.List;
import java.util.stream.Collectors;

public class AuthMapper {

    public static AuthResponseDTO toAuthResponseDTO(String accessToken, String refreshToken,
                                                    Long expiresIn, Utilisateur utilisateur) {
        if (utilisateur == null) {
            return AuthResponseDTO.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .type("Bearer")
                    .expiresIn(expiresIn)
                    .build();
        }
        return AuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .type("Bearer")
                .expiresIn(expiresIn)
                .utilisateurId(utilisateur.getUtilisateurId())
                .login(utilisateur.getLogin())
                .nom(utilisateur.getProfil() != null ? utilisateur.getProfil().getNom() : null)
                .prenom(utilisateur.getProfil() != null ? utilisateur.getProfil().getPrenom() : null)
                .email(utilisateur.getProfil() != null ? utilisateur.getProfil().getEmail() : null)
                .roles(utilisateur.getUtilisateurRoles() != null ?
                        utilisateur.getUtilisateurRoles().stream()
                        .map(ur -> ur.getRole().getNom())
                        .collect(Collectors.toList()) : null)
                .build();
    }

    public static RefreshTokenResponseDTO toRefreshTokenResponseDTO(String accessToken,
                                                                    String refreshToken,
                                                                    Long expiresIn) {
        return RefreshTokenResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(expiresIn)
                .build();
    }
}