package org.gestiontransfertetudiant.gestiontransfertetudiant.GestionEtudiant.DAO.repository;

import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionEtudiant.DAO.entity.Etudiant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface EtudiantRepository extends JpaRepository<Etudiant, UUID> {

    @Query("SELECT e FROM Etudiant e WHERE e.utilisateur.utilisateurId= :utilisateurId")
    Optional<Etudiant> findByUtilisateurId(@Param("utilisateurId") UUID utilisateurId);

    Optional<Etudiant> findByNumeroEtudiant(String numeroEtudiant);

    @Query("SELECT e FROM Etudiant e WHERE " +
            "LOWER(e.utilisateur.profil.nom) LIKE LOWER(CONCAT('%', :critere, '%')) OR " +
            "LOWER(e.utilisateur.profil.prenom) LIKE LOWER(CONCAT('%', :critere, '%')) OR " +
            "LOWER(e.numeroEtudiant) LIKE LOWER(CONCAT('%', :critere, '%')) OR " +
            "LOWER(e.utilisateur.login) LIKE LOWER(CONCAT('%', :critere, '%'))")
    Page<Etudiant> rechercherParCritere(@Param("critere") String critere, Pageable pageable);
}
