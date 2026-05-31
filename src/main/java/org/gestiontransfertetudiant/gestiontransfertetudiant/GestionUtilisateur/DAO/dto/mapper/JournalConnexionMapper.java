package org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.mapper;

import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.response.JournalConnexionResponseDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.entity.JournalConnexion;

public class JournalConnexionMapper {

    public static JournalConnexionResponseDTO toDTO(JournalConnexion journal) {
        if (journal == null) return null;
        return JournalConnexionResponseDTO.builder()
                .id(journal.getId())
                .dateHeure(journal.getDateHeure())
                .succes(journal.getSucces())
                .adresseIP(journal.getAdresseIP())
                .raisonEchec(journal.getRaisonEchec())
                .utilisateurId(journal.getUtilisateur() != null ? journal.getUtilisateur().getUtilisateurId() : null)
                .build();
    }

    // Pas de toEntity : le journal est en écriture seule
}