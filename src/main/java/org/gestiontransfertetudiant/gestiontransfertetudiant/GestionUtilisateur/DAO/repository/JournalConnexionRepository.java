package org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.repository;

import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.entity.JournalConnexion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface JournalConnexionRepository extends JpaRepository<JournalConnexion, UUID> {
    // Dans JournalConnexionRepository
    @Query("SELECT e FROM Etudiant e WHERE e.utilisateur.utilisateurId= :utilisateurId")
    Page<JournalConnexion> findByUtilisateurId(UUID utilisateurId, Pageable pageable);
}