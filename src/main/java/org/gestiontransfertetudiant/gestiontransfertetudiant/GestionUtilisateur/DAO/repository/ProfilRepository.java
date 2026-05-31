package org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.repository;

import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.entity.Profil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ProfilRepository extends JpaRepository<Profil, UUID> {
    Optional<Profil> findByEmail(String email);

    @Query("SELECT p FROM Profil p WHERE p.utilisateur.utilisateurId= :utilisateurId")
    Optional<Profil> findByUtilisateurId(@Param("utilisateurId") UUID utilisateurId);
    
    Optional<Profil> findByMatriculeNational(String matriculeNational);
}