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
                    .expiresIn(expiresIn)
                    .statusCode(200)
                    .message("Login successful")
                    .success(true)
                    .build();
        }
        
        List<String> roles = utilisateur.getUtilisateurRoles() != null ?
                utilisateur.getUtilisateurRoles().stream()
                        .map(ur -> ur.getRole().getNom())
                        .collect(Collectors.toList()) : null;
        
        String nom = utilisateur.getProfil() != null ? utilisateur.getProfil().getNom() : null;
        String prenom = utilisateur.getProfil() != null ? utilisateur.getProfil().getPrenom() : null;
        String email = utilisateur.getProfil() != null ? utilisateur.getProfil().getEmail() : null;
        
        AuthResponseDTO.UserInfoDTO userInfo = AuthResponseDTO.UserInfoDTO.builder()
                .id(utilisateur.getUtilisateurId().toString())
                .email(email)
                .nom(nom)
                .prenom(prenom)
                .roles(roles)
                .build();
        
        return AuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(expiresIn)
                .statusCode(200)
                .message("Login successful")
                .success(true)
                .user(userInfo)
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