package org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.mapper;

import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.request.UtilisateurRequestDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.request.UtilisateurUpdateRequestDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.response.UtilisateurDetailResponseDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.response.UtilisateurResponseDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.response.UtilisateurSimpleResponseDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.entity.Utilisateur;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class UtilisateurMapper {

    public static UtilisateurResponseDTO toDTO(Utilisateur utilisateur) {
        if (utilisateur == null) return null;
        return UtilisateurResponseDTO.builder()
                .id(utilisateur.getUtilisateurId())
                .login(utilisateur.getLogin())
                .actif(utilisateur.getActif())
                .dateCreation(utilisateur.getDateCreation())
                .derniereConnexion(utilisateur.getDerniereConnexion())
                .estBloque(utilisateur.getBloqueJusqua() != null &&
                        utilisateur.getBloqueJusqua().isAfter(LocalDateTime.now()))
                .build();
    }

    public static Utilisateur toEntity(UtilisateurRequestDTO requestDTO) {
        if (requestDTO == null) return null;
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setLogin(requestDTO.getLogin());
        utilisateur.setActif(requestDTO.getActif() != null ? requestDTO.getActif() : true);
        // Le mot de passe sera hashé par le service, on le mettra à part
        return utilisateur;
    }

    public static Utilisateur toEntity(UtilisateurUpdateRequestDTO requestDTO) {
        if (requestDTO == null) return null;
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setLogin(requestDTO.getLogin());
        utilisateur.setActif(requestDTO.getActif());
        return utilisateur;
    }

    public static UtilisateurSimpleResponseDTO toSimpleDTO(Utilisateur utilisateur) {
        if (utilisateur == null) return null;
        return UtilisateurSimpleResponseDTO.builder()
                .id(utilisateur.getUtilisateurId())
                .login(utilisateur.getLogin())
                .nom(utilisateur.getProfil() != null ? utilisateur.getProfil().getNom() : null)
                .prenom(utilisateur.getProfil() != null ? utilisateur.getProfil().getPrenom() : null)
                .email(utilisateur.getProfil() != null ? utilisateur.getProfil().getEmail() : null)
                .actif(utilisateur.getActif())
                .build();
    }

    public static UtilisateurDetailResponseDTO toDetailDTO(Utilisateur utilisateur) {
        if (utilisateur == null) return null;
        return UtilisateurDetailResponseDTO.builder()
                .id(utilisateur.getUtilisateurId())
                .login(utilisateur.getLogin())
                .actif(utilisateur.getActif())
                .dateCreation(utilisateur.getDateCreation())
                .derniereConnexion(utilisateur.getDerniereConnexion())
                .profil(ProfilMapper.toDTO(utilisateur.getProfil()))
                .roles(utilisateur.getUtilisateurRoles() != null ?
                        utilisateur.getUtilisateurRoles().stream()
                        .map(ur -> RoleMapper.toDTO(ur.getRole()))
                        .collect(Collectors.toList()) : null)
                .permissions(null) // ou calculer via les rôles si besoin
                .build();
    }
}
