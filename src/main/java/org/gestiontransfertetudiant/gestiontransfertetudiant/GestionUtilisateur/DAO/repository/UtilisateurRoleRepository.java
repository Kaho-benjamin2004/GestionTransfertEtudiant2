package org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.repository;

import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.entity.UtilisateurRole;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.entity.UtilisateurRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

public interface UtilisateurRoleRepository extends JpaRepository<UtilisateurRole, UtilisateurRoleId> {
    @Query("SELECT e FROM Etudiant e WHERE e.utilisateur.utilisateurId= :utilisateurId")
    List<UtilisateurRole> findByUtilisateurId(UUID utilisateurId);
   

    @Query("SELECT ur.role.nom FROM UtilisateurRole ur WHERE ur.utilisateur.utilisateurId= :utilisateurId")
    List<String> findRoleNomsByUtilisateurId(@Param("utilisateurId") UUID utilisateurId);


    @Modifying
    @Transactional
    @Query("DELETE FROM UtilisateurRole ur WHERE ur.utilisateur.id = :utilisateurId AND ur.role.id = :roleId")
    void deleteByUtilisateurIdAndRoleId(UUID utilisateurId, UUID roleId);
}