package org.gestiontransfertetudiant.gestiontransfertetudiant.DemandeTransfert.DAO.repository;

import org.gestiontransfertetudiant.gestiontransfertetudiant.DemandeTransfert.DAO.entity.DemandeTransfert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface DemandeTransfertRepository extends JpaRepository<DemandeTransfert, UUID> {
    List<DemandeTransfert> findByEtudiantId(UUID etudiantId);
    List<DemandeTransfert> findByDateDemandeBetween(LocalDate dateDebut, LocalDate dateFin);

    long countByStatut(String statut);
    Page<DemandeTransfert> findByStatut(String statut, Pageable pageable);
    @Query("SELECT d FROM DemandeTransfert d WHERE d.etudiant.utilisateur.utilisateurId = :utilisateurId")
    List<DemandeTransfert> findByUtilisateurId(@Param("utilisateurId") UUID utilisateurId);

    long countByEtudiantIdAndStatutNotIn(UUID etudiantId, List<String> acceptee);

    @Query("SELECT COUNT(d) FROM DemandeTransfert d WHERE d.etudiant.id = :etudiantId AND d.statut NOT IN ('ACCEPTEE', 'REFUSEE', 'ANNULEE')")
    long countByEtudiantIdAndStatutNotIn(@Param("etudiantId") UUID etudiantId);
}
