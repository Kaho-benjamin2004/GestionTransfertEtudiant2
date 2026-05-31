package org.gestiontransfertetudiant.gestiontransfertetudiant.GestionEtudiant.SERVICE;


import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionEtudiant.DAO.dto.StatistiquesDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionEtudiant.DAO.dto.reponse.*;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionEtudiant.DAO.dto.request.CreditRequestDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionEtudiant.DAO.dto.request.EtablissementAnterieurRequestDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionEtudiant.DAO.dto.request.NoteRequestDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionEtudiant.DAO.dto.request.SanctionRequestDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.DAO.dto.request.ProfilRequestDTO;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE.execption.BusinessException;
import org.gestiontransfertetudiant.gestiontransfertetudiant.GestionUtilisateur.SERVICE.execption.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface IEtudiantMetier {
    // IEtudiantMetier.java (Module 2)
    EtudiantResponseDTO creerEtudiantPourUtilisateur(UUID utilisateurId, String numeroEtudiant) throws BusinessException;

    // ========== Cas d'utilisation Étudiant ==========

    /**
     * Consulter l'historique académique d'un étudiant (notes, parcours, crédits).
     *

     */
    int getTotalCreditsAcquis(UUID etudiantId) throws ResourceNotFoundException;
    EtudiantResponseDTO consulterHistorique(UUID etudiantId) throws ResourceNotFoundException;

    /**
     * Consulter les crédits validés d'un étudiant.
     */
    List<CreditResponseDTO> consulterCreditsValides(UUID etudiantId) throws ResourceNotFoundException;

    /**
     * Consulter les sanctions disciplinaires d'un étudiant.
     */
    List<SanctionResponseDTO> consulterSanctions(UUID etudiantId) throws ResourceNotFoundException;

    /**
     * Enregistrer les établissements précédents d'un étudiant (lors de l'inscription).
     */
    EtablissementAnterieurResponseDTO ajouterEtablissementAnterieur(UUID etudiantId, EtablissementAnterieurRequestDTO request) throws BusinessException;

    /**
     * Modifier les informations personnelles de l'étudiant (via son utilisateur).
     */
    void modifierInformationsPersonnelles(UUID utilisateurId, ProfilRequestDTO profilRequest) throws ResourceNotFoundException;

    // ========== Cas d'utilisation Agent académique ==========

    /**
     * Rechercher un étudiant par numéro étudiant, nom, prénom, etc.
     */
    Page<EtudiantResponseDTO> rechercherEtudiants(String critere, Pageable pageable);

    /**
     * Consulter le dossier complet d'un étudiant (y compris parcours, notes, sanctions).
     */
    EtudiantResponseDTO consulterDossierComplet(UUID etudiantId) throws ResourceNotFoundException;

    /**
     * Enregistrer les crédits obtenus pour un parcours académique.
     */
    CreditResponseDTO ajouterCredits(CreditRequestDTO request) throws ResourceNotFoundException, BusinessException;

    /**
     * Enregistrer une sanction disciplinaire.
     */
    SanctionResponseDTO ajouterSanction(SanctionRequestDTO request) throws ResourceNotFoundException, BusinessException;

    /**
     * Générer un relevé de notes officiel (PDF).
     */
    byte[] genererReleveNotes(UUID etudiantId, UUID parcoursId) throws ResourceNotFoundException;

    /**
     * Valider le parcours académique (changer le statut à "Validé").
     */
    void validerParcours(UUID parcoursId) throws ResourceNotFoundException, BusinessException;

    /**
     * Calculer la moyenne générale d'un étudiant.
     */
    BigDecimal calculerMoyenneGenerale(UUID etudiantId) throws ResourceNotFoundException;

    // ========== Cas d'utilisation Administration centrale ==========

    /**
     * Exporter les données statistiques (par promotion/filière).
     */
    StatistiquesDTO exporterStatistiques(String filiere, String anneeUniversitaire) throws BusinessException;

    // ========== Cas d'utilisation Commission pédagogique ==========

    /**
     * Comparer les programmes (par exemple, entre deux UE).
     */
    ComparaisonProgrammeDTO comparerProgrammes(UUID ueId1, UUID ueId2) throws ResourceNotFoundException;

    // ========== Méthodes supplémentaires pour la gestion des notes et UE ==========

    /**
     * Enregistrer une note pour un étudiant dans une UE.
     */
    NoteResponseDTO ajouterNote(NoteRequestDTO request) throws ResourceNotFoundException, BusinessException;

    /**
     * Créer ou mettre à jour une unité d'enseignement.
     */
    UniteEnseignementResponseDTO sauvegarderUE(org.gestiontransfertetudiant.gestiontransfertetudiant.GestionEtudiant.DTO.request.UniteEnseignementRequestDTO request) throws BusinessException;

    /**
     * Lister toutes les UE.
     */
    List<UniteEnseignementResponseDTO> listerToutesUE();

    /**
     * Récupérer une UE par son ID.
     */
    UniteEnseignementResponseDTO getUEById(UUID id) throws ResourceNotFoundException;

    /**
     * Récupérer l'identifiant d'un étudiant à partir de son utilisateurId.
     */
    UUID getEtudiantIdByUtilisateurId(UUID utilisateurId) throws ResourceNotFoundException;
}