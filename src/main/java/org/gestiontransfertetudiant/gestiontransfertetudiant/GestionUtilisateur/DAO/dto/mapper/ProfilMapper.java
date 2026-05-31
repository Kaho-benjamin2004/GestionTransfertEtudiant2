package org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.mapper;

import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.request.ProfilRequestDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.response.ProfilResponseDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.entity.Profil;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.entity.Utilisateur;

public class ProfilMapper {

    public static ProfilResponseDTO toDTO(Profil profil) {
        if (profil == null) return null;
        return ProfilResponseDTO.builder()
                .id(profil.getId())
                .nom(profil.getNom())
                .prenom(profil.getPrenom())
                .email(profil.getEmail())
                .telephone(profil.getTelephone())
                .matriculeNational(profil.getMatriculeNational())
                .fonction(profil.getFonction())
                .utilisateurId(profil.getUtilisateur() != null ? profil.getUtilisateur().getUtilisateurId(): null)
                .build();
    }

    public static Profil toEntity(ProfilRequestDTO requestDTO, Utilisateur utilisateur) {
        if (requestDTO == null) return null;
        Profil profil = new Profil();
        profil.setNom(requestDTO.getNom());
        profil.setPrenom(requestDTO.getPrenom());
        profil.setEmail(requestDTO.getEmail());
        profil.setTelephone(requestDTO.getTelephone());
        profil.setMatriculeNational(requestDTO.getMatriculeNational());

        profil.setUtilisateur(utilisateur);
        return profil;
    }

    public static Profil toEntity(ProfilRequestDTO requestDTO) {
        return toEntity(requestDTO, null);
    }
}