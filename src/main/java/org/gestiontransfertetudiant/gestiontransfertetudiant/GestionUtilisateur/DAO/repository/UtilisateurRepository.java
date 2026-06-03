package org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.repository;

import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, UUID> {
    Optional<Utilisateur> findByLogin(String login);
    boolean existsByLogin(String login);
    
    // Find by email via Profil relationship
    @Query("SELECT u FROM Utilisateur u LEFT JOIN FETCH u.utilisateurRoles ur LEFT JOIN FETCH ur.role WHERE u.profil.email = :email")
    Optional<Utilisateur> findByEmail(@Param("email") String email);

    @Modifying
    @Transactional
    @Query("UPDATE Utilisateur u SET u.tentativeEchec = u.tentativeEchec + 1 WHERE u.login = :login")
    void incrementerTentativesEchec(@Param("login") String login);

    @Modifying
    @Transactional
    @Query("UPDATE Utilisateur u SET u.bloqueJusqua = :date WHERE u.login = :login")
    void bloquerUtilisateur(@Param("login") String login, @Param("date") LocalDateTime date);

    @Modifying
    @Transactional
    @Query("UPDATE Utilisateur u SET u.tentativeEchec = 0, u.bloqueJusqua = null WHERE u.login = :login")
    void reinitialiserTentatives(@Param("login") String login);
    
    @Query("SELECT u FROM Utilisateur u LEFT JOIN FETCH u.utilisateurRoles ur LEFT JOIN FETCH ur.role WHERE u.login = :login")
    Optional<Utilisateur> findByLoginWithRoles(@Param("login") String login);

    Utilisateur findByUtilisateurId(UUID utilisateurId);
}